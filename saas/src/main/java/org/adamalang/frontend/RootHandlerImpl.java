/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.connection.Session;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.*;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.mysql.model.*;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.io.NoOpJsonResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class RootHandlerImpl implements RootHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RootHandlerImpl.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);
  private final ExternNexus nexus;
  private final Random rng;
  private final SpacePolicyLocator policyLocator;

  public RootHandlerImpl(ExternNexus nexus, SpacePolicyLocator policyLocator) throws Exception {
    this.nexus = nexus;
    this.rng = new Random();
    this.policyLocator = policyLocator;
  }

  @Override
  public void handle(Session session, ConfigureMakeOrGetAssetKeyRequest request, AssetKeyResponder responder) {
    if (session.authenticator.assetKey() == null) {
      session.authenticator.updateAssetKey(SecureAssetUtil.makeAssetKeyHeader());
    }
    responder.complete(session.authenticator.assetKey());
  }

  @Override
  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        String hash = SCryptUtil.scrypt(request.password, 16384, 8, 1);
        Users.setPasswordHash(nexus.database, request.who.id, hash);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_ONLY_ADAMA_DEV_EXCEPTION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_PASSWORD_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private void commonAuthorize(Session session, Key key, String username, String password, InitiationResponder responder) {
    nexus.adama.authorize(session.authenticator.ip(), session.authenticator.origin(), key.space, key.key, username, password, new Callback<String>() {
      @Override
      public void success(String agent) {
        try {
          String identity = Secrets.getOrCreateDocumentSigningKey(nexus.database, nexus.masterKey, key.space, key.key).signDocument(key.space, key.key, agent);
          responder.complete(identity);
        } catch (Exception ex) {
          responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_AUTH_DOCUMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder) {
    commonAuthorize(session, new Key(request.space, request.key), request.username, request.password, responder);
  }

  @Override
  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder) {
    try {
      Domain domain = Domains.get(nexus.database, fixDomain(request.domain));
      if (domain != null) {
        if (domain.key != null) {
          commonAuthorize(session, new Key(domain.space, domain.key), request.username, request.password, responder);
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_NO_KEY_MAPPED));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_AUTH_DOMAIN_AUTH_DOMAIN_INVALID_MAPPED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_AUTH_DOMAIN_AUTH_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder) {
    responder.complete(SCryptUtil.scrypt(request.password, 16384, 8, 1));
  }

  @Override
  public void handle(Session session, AccountGetPaymentPlanRequest request, PaymentResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // Do the DB lookup now
        String paymentInfo = Users.getPaymentInfo(nexus.database, request.who.id);
        // TODO: if no customerId, then bind one here and put it back into storage (need to handle conflicts better, or just use a better schema)
        // TODO: parse out information that is useful for stripe to update credit card, etc...
        responder.complete("none", "disabled");
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GET_PAYMENT_INFO_ONLY_ADAMA_DEV_EXCEPTION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GET_PAYMENT_INFO_UNKNOWN, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder) {
    try {
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + request.accessToken);
      SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://www.googleapis.com/oauth2/v1/userinfo", headers, SimpleHttpRequestBody.EMPTY);
      nexus.webBase.execute(get, new StringCallbackHttpResponder(LOG, nexus.frontendMetrics.google_account_translate.start(), new Callback<String>() {
        @Override
        public void success(String value) {
          ObjectNode googleProfile = Json.parseJsonObject(value);
          try {
            String email = googleProfile.get("email").textValue();
            ValidateEmail.validate(email);
            int userId = Users.getOrCreateUserId(nexus.database, email);
            String profileOld = Users.getProfile(nexus.database, userId);
            ObjectNode profile = Json.parseJsonObject(profileOld);
            boolean changedProfile = false;
            if (!profile.has("name") && googleProfile.has("name")) {
              profile.set("name", googleProfile.get("name"));
              changedProfile = true;
            }
            if (!profile.has("picture") && googleProfile.has("picture")) {
              profile.set("picture", googleProfile.get("picture"));
              changedProfile = true;
            }
            if (!profile.has("locale") && googleProfile.has("locale")) {
              profile.set("locale", googleProfile.get("locale"));
              changedProfile = true;
            }
            if (changedProfile) {
              Users.setProfileIf(nexus.database, userId, profile.toString(), profileOld);
            }
            KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
            String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
            long expiry = System.currentTimeMillis() + 3 * 24 * 60 * 60000;
            Users.addKey(nexus.database, userId, publicKey, expiry);
            responder.complete(Jwts.builder().setSubject("" + userId).setExpiration(new Date(expiry)).setIssuer("adama").signWith(pair.getPrivate()).compact());
          } catch (Exception ex) {
            responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CONVERT_TOKEN_VALIDATE_EXCEPTION, ex, RootHandlerImpl.LOGGER));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      }));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CONVERT_TOKEN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder) {
    try {
      String hash = Users.getPasswordHash(nexus.database, request.userId);
      if (SCryptUtil.check(request.password, hash)) {
        KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
        String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
        long expiry = System.currentTimeMillis() + 14 * 24 * 60 * 60000;
        Users.addKey(nexus.database, request.userId, publicKey, expiry);
        responder.complete(Jwts.builder().setSubject("" + request.userId).setExpiration(new Date(expiry)).setIssuer("adama").signWith(pair.getPrivate()).compact());
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_PASSWORD_INVALID));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LOGIN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder) {
    try {
      String generatedCode = generateCode();
      String hash = SCryptUtil.scrypt(generatedCode, 16384, 8, 1);
      Users.addInitiationPair(nexus.database, request.userId, hash, System.currentTimeMillis() + 15 * 60000);
      nexus.email.sendCode(request.email, generatedCode);
      responder.complete();
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_SETUP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder) {
    try {
      for (IdHashPairing idHash : Users.listInitiationPairs(nexus.database, request.userId)) {
        if (SCryptUtil.check(request.code, idHash.hash)) {
          KeyPair pair = Keys.keyPairFor(SignatureAlgorithm.ES256);
          String publicKey = new String(Base64.getEncoder().encode(pair.getPublic().getEncoded()));
          if (request.revoke != null && request.revoke) {
            Users.removeAllKeys(nexus.database, request.userId);
          }
          Users.addKey(nexus.database, request.userId, publicKey, System.currentTimeMillis() + 14 * 24 * 60 * 60000);
          responder.complete(Jwts.builder().setSubject("" + request.userId).setIssuer("adama").signWith(pair.getPrivate()).compact());
          Users.validateUser(nexus.database, request.userId);
          Users.deleteInitiationPairing(nexus.database, idHash.id);
          return;
        }
      }
      responder.error(new ErrorCodeException(ErrorCodes.API_INIT_COMPLETE_CODE_MISMATCH));
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_INIT_COMPLETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private String generateCode() {
    StringBuilder code = new StringBuilder();
    for (int j = 0; j < 2; j++) {
      int val = rng.nextInt(26 * 26 * 26 * 26);
      for (int k = 0; k < 4; k++) {
        code.append(('A' + (val % 26)));
        val /= 26;
      }
    }
    return code.toString();
  }

  @Override
  public void handle(Session session, ProbeRequest request, SimpleResponder responder) {
    responder.complete();
  }

  @Override
  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        String authority = ProtectedUUID.generate();
        Authorities.createAuthority(nexus.database, request.who.id, authority);
        responder.complete(authority);
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: setKeystore validates ownership
        Authorities.setKeystore(nexus.database, request.who.id, request.authority, request.keyStore.toString());
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: getKeystorePublic validates ownership
        responder.complete(Json.parseJsonObject(Authorities.getKeystorePublic(nexus.database, request.who.id, request.authority)));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GET_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GET_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (String authority : Authorities.list(nexus.database, request.who.id)) {
          responder.next(authority);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        // NOTE: deleteAuthority validates ownership
        Authorities.deleteAuthority(nexus.database, request.who.id, request.authority);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  /** HACK: change *.blah.com to wildcard.blah.com */
  private String fixDomain(String domain) {
    return domain.replaceAll(Pattern.quote("*"), "wildcard");
  }

  @Override
  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        Domain domain = Domains.get(nexus.database, fixDomain(request.domain));
        if (domain != null) {
          responder.complete(domain.space);
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_GET_NOT_FOUND));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_GET_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_GET_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  private void handleDomainMap(SpacePolicy policy, AuthenticatedUser who, String domain, String certificate, String space, String key, boolean route, SimpleResponder responder) {
    try {
      if (policy.canUserManageDomain(who)) {
        String cert = certificate != null ? MasterKey.encrypt(nexus.masterKey, certificate) : null;
        if (Domains.map(nexus.database, who.id, fixDomain(domain), space, key, route, cert)) { // Domains.map ensures ownership on UPDATE to prevent conflicts
          if (cert == null) {
            nexus.signalControl.raiseAutomaticDomain(domain);
          }
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_MAP_FAILED));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_MAP_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_MAP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DomainMapRequest request, SimpleResponder responder) {
    handleDomainMap(request.policy, request.who, request.domain, request.certificate, request.space, null, false, responder);
  }

  @Override
  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder) {
    handleDomainMap(request.policy, request.who, request.domain, request.certificate, request.space, request.key, false, responder);
  }

  @Override
  public void handle(Session session, DomainListRequest request, DomainListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (Domain domain : Domains.list(nexus.database, request.who.id)) {
          responder.next(domain.domain, domain.space, domain.key, domain.routeKey);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_LIST_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        if (Domains.unmap(nexus.database, request.who.id, fixDomain(request.domain))) {
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_UNMAP_FAILED));
        }
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_DOMAIN_UNMAP_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_DOMAIN_UNMAP_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder) {
    try {
      if (request.policy.canUserGetRxHTML(request.who)) {
        responder.complete(Spaces.getRxHtml(nexus.database, request.policy.id));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_RXHTML_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_RXHTML_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetRxHTML(request.who)) {
        Spaces.setRxHtml(nexus.database, request.policy.id, request.rxhtml);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_SET_RXHTML_NOT_AUTHORIZED));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_RXHTML_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder) {
    try {
      if ("ide".equals(request.space) || "wildcard".equals(request.space) || "billing".equals(request.space)) {
        responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_SPACE_RESERVED));
        return;
      }
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        int spaceId = Spaces.createSpace(nexus.database, request.who.id, request.space);
        SpaceTemplates.SpaceTemplate template = SpaceTemplates.REGISTRY.of(request.template);
        Spaces.setRxHtml(nexus.database, spaceId, template.initialRxHTML(request.space)); // TODO: put into createSpace? Or, rely on the document
        nexus.adama.create(request.who, "ide", request.space, null, template.idearg(request.space), new Callback<Void>() {
          @Override
          public void success(Void value) {
            SpacePolicy policy = new SpacePolicy(new SpaceInfo(spaceId, request.who.id, Collections.singleton(request.who.id), true, 0));
            handle(session, new SpaceSetRequest(request.identity, request.who, request.space, policy, template.plan()), new SimpleResponder(new NoOpJsonResponder()));
            responder.complete();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            try {
              Spaces.delete(nexus.database, spaceId, request.who.id);
            } catch (Exception failure) {
              responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_IDE_DOCUMENT_FAILED_CANT_DELETE_UNKNOWN_EXCEPTION, ex, RootHandlerImpl.LOGGER));
              return;
            }
            responder.error(ex);
          }
        });
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_CREATE_FAILED_NOT_ADAMA_DEVELOPER));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_CREATE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceUsageRequest request, BillingUsageResponder responder) {
    try {
      if (request.policy.canUserGetBillingUsage(request.who)) {
        for (BillingUsage usage : Billing.usageReport(nexus.database, request.policy.id, request.limit != null ? request.limit.intValue() : 336)) {
          responder.next(usage.hour, usage.cpu, usage.memory, usage.connections, usage.documents, usage.messages, usage.storageBytes, usage.bandwidth, usage.firstPartyServiceCalls, usage.thirdPartyServiceCalls);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_BILLING_USAGE_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_BILLING_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGetRequest request, PlanResponder responder) {
    try {
      if (request.policy.canUserGetPlan(request.who)) {
        responder.complete(Json.parseJsonObject(Spaces.getPlan(nexus.database, request.policy.id)));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_GET_PLAN_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetPlan(request.who)) {
        String planJson = request.plan.toString();
        // hash the plan
        MessageDigest digest = Hashing.md5();
        digest.digest(planJson.getBytes(StandardCharsets.UTF_8));
        String hash = Hashing.finishAndEncode(digest);
        // Change the master plan
        Spaces.setPlan(nexus.database, request.policy.id, planJson, hash);
        // iterate the targets with this space loaded
        nexus.adama.deployLocal(request.space);
        nexus.adama.deployCrossRegion(request.who, request.space);
        nexus.adama.waitForCapacity(request.space, 30000, (found) -> {
          if (found) {
            responder.complete();
          } else {
            responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_DEPLOYMENT_FAILED_FINDING_CAPACITY));
          }
        });
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_PLAN_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserSetPlan(request.who)) {
        nexus.adama.deployLocal(request.space);
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_KICK_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_KICK_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder) {
    try {
      if (request.policy.canUserDeleteSpace(request.who)) {
        if (FinderOperations.list(nexus.database, request.space, null, 1).size() > 0) {
          responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NOT_EMPTY));
          return;
        }
        Spaces.changePrimaryOwner(nexus.database, request.policy.id, request.policy.owner, 0);
        Domains.deleteSpace(nexus.database, request.space);
        Capacity.removeAll(nexus.database, request.space);
        responder.complete();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_DELETE_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_DELETE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder) {
    try {
      Role role = Role.from(request.role);
      if (request.policy.canUserSetRole(request.who)) {
        Spaces.setRole(nexus.database, request.policy.id, request.userId, role);
        SpaceInfo updatedSpaceInfo = Spaces.getSpaceInfo(nexus.database, request.space);
        JsonStreamWriter syncDevelopers = new JsonStreamWriter();
        syncDevelopers.beginObject();
        syncDevelopers.writeObjectFieldIntro("developers");
        syncDevelopers.beginArray();
        for (Integer devId : updatedSpaceInfo.developers) {
          syncDevelopers.writeNtPrincipal(new NtPrincipal("" + devId, "adama"));
        }
        syncDevelopers.endArray();
        syncDevelopers.endObject();
        nexus.adama.directSend(request.who, "ide", request.space, null, "set_developers_from_frontend", syncDevelopers.toString(), new Callback<Integer>() {
          @Override
          public void success(Integer value) {
            responder.complete();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_SET_ROLE_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceListDevelopersRequest request, DeveloperResponder responder) {
    try {
      if (request.policy.canUserListDeveloper(request.who)) {
        for (Developer developer : Spaces.listDevelopers(nexus.database, request.policy.id)) {
          responder.next(developer.email, developer.role);
        }
        responder.finish();
      } else {
        throw new ErrorCodeException(ErrorCodes.API_SPACE_LIST_DEVELOPERS_NO_PERMISSION_TO_EXECUTE);
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_DEVELOPERS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder) {
    if (request.policy.canUserSeeReflection(request.who)) {
      nexus.adama.reflect(request.space, request.key, new Callback<String>() {
        @Override
        public void success(String value) {
          responder.complete(Json.parseJsonObject(value));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          responder.error(ex);
        }
      });
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_REFLECT_NO_PERMISSION_TO_EXECUTE));
    }
  }

  @Override
  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder) {
    try {
      if (request.who.source == AuthenticatedUser.Source.Adama) {
        for (SpaceListingItem spaceListingItem : Spaces.list(nexus.database, request.who.id, request.marker, request.limit == null ? 100 : request.limit)) {
          responder.next(spaceListingItem.name, spaceListingItem.callerRole, spaceListingItem.created, spaceListingItem.enabled, spaceListingItem.storageBytes);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_SPACE_LIST_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, DocumentDeleteRequest request, SimpleResponder responder) {
    nexus.adama.delete(request.who, request.space, request.key, new Callback<>() {
      @Override
      public void success(Void value) {
        responder.complete();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    if ("ide".equals(request.space) || "wildcard".equals(request.space) || "billing".equals(request.space)) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CREATE_DOCUMENT_SPACE_RESERVED));
      return;
    }
    nexus.adama.create(request.who, request.space, request.key, request.entropy, request.arg.toString(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        responder.complete();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder) {
    try {
      if (request.policy.canUserSeeKeyListing(request.who)) {
        for (DocumentIndex item : FinderOperations.list(nexus.database, request.space, request.marker, request.limit != null ? request.limit : 100)) {
          responder.next(item.key, item.created, item.updated, item.seq);
        }
        responder.finish();
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_LIST_DOCUMENTS_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_LIST_DOCUMENTS_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, SpaceGenerateKeyRequest request, KeyPairResponder responder) {
    try {
      if (request.policy.canUserGeneratePrivateKey(request.who)) {
        KeyPair pair = PublicPrivateKeyPartnership.genKeyPair();
        String privateKeyEncrypted = MasterKey.encrypt(nexus.masterKey, PublicPrivateKeyPartnership.privateKeyOf(pair));
        int keyId = Secrets.insertSecretKey(nexus.database, request.space, privateKeyEncrypted);
        responder.complete(keyId, PublicPrivateKeyPartnership.publicKeyOf(pair));
      } else {
        responder.error(new ErrorCodeException(ErrorCodes.API_GENERATE_KEY_NO_PERMISSION));
      }
    } catch (Exception ex) {
      responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_GENERATE_KEY_UNKNOWN_EXCEPTION, ex, LOGGER));
    }
  }

  @Override
  public void handle(Session session, MessageDirectSendRequest request, SeqResponder responder) {
    nexus.adama.directSend(request.who, request.space, request.key, null, request.channel, request.message.toString(), new Callback<Integer>() {
      @Override
      public void success(Integer seq) {
        responder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public void handle(Session session, MessageDirectSendOnceRequest request, SeqResponder responder) {
    nexus.adama.directSend(request.who, request.space, request.key, request.dedupe, request.channel, request.message.toString(), new Callback<Integer>() {
      @Override
      public void success(Integer seq) {
        responder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        responder.error(ex);
      }
    });
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder) {
    if (request.resolvedDomain.domain == null || request.resolvedDomain.policy == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CONNECT_DOMAIN_NO_SPACE_FOUND));
      return null;
    }
    if (request.resolvedDomain.domain.key == null) {
      responder.error(new ErrorCodeException(ErrorCodes.API_CONNECT_DOMAIN_NO_KEY_FOUND));
      return null;
    }
    return handle(session, new ConnectionCreateRequest(request.identity, request.who, request.resolvedDomain.domain.space, request.resolvedDomain.policy, request.resolvedDomain.domain.key, request.viewerState), responder);
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest connect, DataResponder responder) {
    return new DocumentStreamHandler() {
      private AdamaStream connection;

      @Override
      public void bind() {
        connection = nexus.adama.connect(connect.who, connect.space, connect.key, connect.viewerState != null ? connect.viewerState.toString() : "{}", new SimpleEvents() {
          @Override
          public void connected() {
          }

          @Override
          public void delta(String data) {
            responder.next(Json.parseJsonObject(data));
          }

          @Override
          public void error(int code) {
            responder.error(new ErrorCodeException(code));
          }

          @Override
          public void disconnected() {
            responder.finish();
          }
        });
      }

      @Override
      public void handle(ConnectionUpdateRequest request, SimpleResponder responder) {
        connection.update(request.viewerState != null ? request.viewerState.toString() : "{}");
        responder.complete();
      }

      @Override
      public void handle(ConnectionCanAttachRequest request, YesResponder responder) {
       connection.canAttach(new Callback<Boolean>() {
         @Override
         public void success(Boolean value) {
           responder.complete(value);
         }

         @Override
         public void failure(ErrorCodeException ex) {
          responder.error(ex);
         }
       });
      }

      @Override
      public void handle(ConnectionAttachRequest request, SeqResponder responder) {
        if (connect.who.internal) {
          connection.attach(request.assetId, request.filename, request.contentType, request.size, request.digestMd5, request.digestSha384, WRAP(responder));
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.API_LOCKED_INTERNAL));
        }
      }

      @Override
      public void handle(ConnectionSendOnceRequest request, SeqResponder responder) {
        connection.send(request.channel, request.dedupe, request.message.toString(), WRAP(responder));
      }

      @Override
      public void handle(ConnectionSendRequest request, SeqResponder responder) {
        connection.send(request.channel, null, request.message.toString(), WRAP(responder));
      }

      @Override
      public void handle(ConnectionPasswordRequest request, SeqResponder responder) {
        // public void authorize(String ip, String origin, String space, String key, String username, String password, Callback<String> callback) {
        nexus.adama.authorize(session.authenticator.ip(), session.authenticator.origin(), connect.space, connect.key, request.username, request.password, new Callback<String>() {
          @Override
          public void success(String identity) {
            connection.password(request.password, WRAP(responder));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            responder.error(ex);
          }
        });
      }

      @Override
      public void handle(ConnectionEndRequest request, SimpleResponder responder) {
        connection.close();
        responder.complete();
      }

      @Override
      public void disconnect(long id) {
        connection.close();
      }

      @Override
      public void logInto(ObjectNode node) {
        connect.logInto(node);
      }
    };
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder startResponder) {
    AtomicBoolean clean = new AtomicBoolean(false);
    AtomicBoolean killed = new AtomicBoolean(false);
    AtomicReference<AdamaStream> connection = new AtomicReference<>(null);
    Runnable kickOff = () -> {
      connection.set(nexus.adama.connect(request.who, request.space, request.key, "{}", new SimpleEvents() {
        @Override
        public void connected() {
        }

        @Override
        public void delta(String data) {
          // don't care for right now
        }

        @Override
        public void error(int code) {
          if (!killed.get()) {
            killed.set(true);
            startResponder.error(new ErrorCodeException(code));
          }
        }

        @Override
        public void disconnected() {
          if (!clean.get()) {
            if (!killed.get()) {
              killed.set(true);
              startResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_ATTACHMENT_LOST_CONNECTION));
            }
          } else {
            startResponder.finish();
          }
        }
      }));

      connection.get().canAttach(new Callback<Boolean>() {
        @Override
        public void success(Boolean value) {
          if (value) {
            startResponder.next(65536);
          } else {
            if (!killed.get()) {
              killed.set(true);
              startResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_ATTACHMENT_NOT_ALLOWED));
            }
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          killed.set(true);
          startResponder.error(ex);
        }
      });
    };

    return new AttachmentUploadHandler() {
      String id;
      FileOutputStream output;
      File file;
      MessageDigest digestMD5;
      MessageDigest digestSHA384;
      int size;

      @Override
      public void bind() {
        try {
          id = ProtectedUUID.generate();
          file = new File(nexus.attachmentRoot, id + ".upload");
          file.deleteOnExit();
          digestMD5 = MessageDigest.getInstance("MD5");
          digestSHA384 = MessageDigest.getInstance("SHA-384");
          output = new FileOutputStream(file);
          size = 0;
          kickOff.run();
        } catch (Exception ex) {
          startResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_FAILED_BIND, ex, LOGGER));
        }
      }

      @Override
      public void handle(AttachmentAppendRequest attachChunk, SimpleResponder chunkResponder) {
        try {
          byte[] chunk = Base64.getDecoder().decode(attachChunk.base64Bytes);
          size += chunk.length;
          output.write(chunk);
          digestMD5.update(chunk);
          digestSHA384.update(chunk);
          MessageDigest chunkDigest = Hashing.md5();
          chunkDigest.update(chunk);
          if (!Hashing.finishAndEncode(chunkDigest).equals(attachChunk.chunkMd5)) {
            chunkResponder.error(new ErrorCodeException(ErrorCodes.API_ASSET_CHUNK_BAD_DIGEST));
            output.close();
            disconnect(0);
          } else {
            chunkResponder.complete();
            startResponder.next(65536);
          }
        } catch (Exception ex) {
          chunkResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_CHUNK_UNKNOWN_EXCEPTION, ex, LOGGER));
          disconnect(0);
        }
      }

      @Override
      public void handle(AttachmentFinishRequest attachFinish, AssetIdResponder finishResponder) {
        try {
          output.flush();
          output.close();
          String md5_64 = Hashing.finishAndEncode(digestMD5);
          String sha384_64 = Hashing.finishAndEncode(digestSHA384);
          NtAsset asset = new NtAsset(id, request.filename, request.contentType, this.size, md5_64, sha384_64);
          nexus.assets.upload(new Key(request.space, request.key), asset, AssetUploadBody.WRAP(file), new Callback<Void>() {
            @Override
            public void success(Void value) {
              clean.set(true);
              connection.get().attach(id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  disconnect(0L);
                  finishResponder.complete(id);
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  if (!killed.get()) {
                    killed.set(true);
                    startResponder.error(ex);
                  }
                  disconnect(0L);
                  finishResponder.error(ex);
                }
              });
            }

            @Override
            public void failure(ErrorCodeException ex) {
              if (!killed.get()) {
                killed.set(true);
                startResponder.error(ex);
              }
              disconnect(0L);
              finishResponder.error(ex);
            }
          });
        } catch (Exception ex) {
          finishResponder.error(ErrorCodeException.detectOrWrap(ErrorCodes.API_ASSET_ATTACHMENT_UNKNOWN_EXCEPTION, ex, LOGGER));
          disconnect(0);
        }
      }

      @Override
      public void disconnect(long id) {
        file.delete();
        connection.get().close();
      }

      @Override
      public void logInto(ObjectNode node) {
        request.logInto(node);
      }
    };
  }

  @Override
  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder) {
    if (request.who.source == AuthenticatedUser.Source.Super) {
      try {
        Sentinel.ping(nexus.database, "super", System.currentTimeMillis());
        responder.complete();
      } catch (Exception ex) {
        responder.error(new ErrorCodeException(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_CHECKIN));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_CHECKIN));
    }
  }

  @Override
  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder) {
    if (request.who.source == AuthenticatedUser.Source.Super) {
      try {
        for (Domain domain : Domains.superListAutoDomains(nexus.database, request.timestamp)) {
          responder.next(domain.domain, domain.timestamp);
        }
        responder.finish();
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_LIST, ex, LOGGER));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_LIST));
    }
  }

  @Override
  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder) {
    if (request.who.source == AuthenticatedUser.Source.Super) {
      try {
        if (request.certificate != null) {
          Domains.superSetAutoCert(nexus.database, request.domain, MasterKey.encrypt(nexus.masterKey, request.certificate), request.timestamp);
          responder.complete();
        } else {
          responder.error(new ErrorCodeException(ErrorCodes.SUPER_INVALID_CERT_SET_AUTOMATIC));
          return;
        }
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(ErrorCodes.SUPER_UNEXPECTED_EXCEPTION_SET_AUTOMATIC, ex, LOGGER));
      }
    } else {
      responder.error(new ErrorCodeException(ErrorCodes.SUPER_NOT_AUTHORIZED_SET_AUTOMATIC));
    }
  }

  @Override
  public void disconnect() {
  }

  private static Callback<Integer> WRAP(SeqResponder seqResponder) {
    return new Callback<>() {
      @Override
      public void success(Integer seq) {
        seqResponder.complete(seq);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        seqResponder.error(ex);
      }
    };
  }
}
