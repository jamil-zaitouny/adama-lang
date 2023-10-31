/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.adamalang.ErrorCodes;
import org.adamalang.cli.Config;
import org.adamalang.common.*;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.Measurable;
import org.adamalang.common.cache.SyncCacheLRU;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.extern.aws.AWSConfig;
import org.adamalang.extern.aws.AWSMetrics;
import org.adamalang.extern.aws.S3;
import org.adamalang.extern.aws.SQS;
import org.adamalang.extern.prometheus.PrometheusMetricsFactory;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.internal.InternalSigner;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.data.Domain;
import org.adamalang.mysql.model.*;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.TargetsQuorum;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.client.routing.ClientRouter;
import org.adamalang.net.client.routing.finder.MachinePicker;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.contracts.CertificateFinder;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicBoolean;

/** Common service initialization */
public class CommonServiceInit {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceInit.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);
  public final AtomicBoolean alive;
  public final String region;
  public final String role;
  public final int servicePort;
  public final int monitoringPort;
  public final MachineIdentity identity;
  public final PrivateKey hostKey;
  public final PrometheusMetricsFactory metricsFactory;
  public final DataBase database;
  public final NetBase netBase;
  public final Finder finder;
  public final SimpleExecutor system;
  public final SimpleExecutor picker;
  public final S3 s3;
  public final AWSConfig awsConfig;
  public final AWSMetrics awsMetrics;
  public final String machine;
  public final org.adamalang.common.gossip.Engine engine;
  public final int publicKeyId;
  public final WebConfig webConfig;
  public final WebClientBase webBase;
  public final String masterKey;
  public final SQS sqs;
  public final int minDomainsToHoldTo;
  public final int maxDomainsToHoldTo;
  public final int maxDomainAge;
  public final SimpleExecutor services;

  public CommonServiceInit(Config config, Role role) throws Exception {
    MachineHeat.install();
    ConfigObject configObjectForWeb = new ConfigObject(config.get_or_create_child(role == Role.Overlord ? "overlord-web" : "web"));
    if (role == Role.Overlord) {
      configObjectForWeb.intOf("http-port", 8081);
    }
    this.masterKey = config.get_string("master-key", null);
    this.minDomainsToHoldTo = config.get_int("domains-minimum-hold", 64);
    this.maxDomainsToHoldTo = config.get_int("domains-maximum-hold", 2048);
    this.maxDomainAge = config.get_int("domains-max-age-ms", 60 * 60 * 1000);
    this.webConfig = new WebConfig(configObjectForWeb);
    WebConfig webConfig = new WebConfig(configObjectForWeb);
    String identityFileName = config.get_string("identity-filename", "me.identity");
    KeyPair keyPair = PublicKeyCodec.inventHostKey();
    this.alive = new AtomicBoolean(true);
    this.region = config.get_string("region", null);
    this.role = role.name;
    switch (role) {
      case Adama:
        this.servicePort = config.get_int("adama-port", 8001);
        break;
      default:
        this.servicePort = webConfig.port;
    }
    this.monitoringPort = config.get_int("monitoring-" + role.name + "-port", role.monitoringPort);
    this.identity = MachineIdentity.fromFile(identityFileName);
    this.hostKey = keyPair.getPrivate();
    this.metricsFactory = new PrometheusMetricsFactory(monitoringPort);
    this.database = new DataBase(new DataBaseConfig(new ConfigObject(config.read())), new DataBaseMetrics(metricsFactory));
    this.netBase = new NetBase(new NetMetrics(metricsFactory), identity, 1, 2);
    this.finder = new Finder(database, region);
    this.system = SimpleExecutor.create("system");
    this.picker = SimpleExecutor.create("picker");
    this.machine = this.identity.ip + ":" + servicePort;
    this.publicKeyId = Hosts.initializeHost(database, this.region, this.machine, role.name, PublicKeyCodec.encodePublicKey(keyPair));
    this.webBase = new WebClientBase(this.webConfig);

    system.schedule(new NamedRunnable("database-ping") {
      @Override
      public void execute() throws Exception {
        try {
          Health.pingDataBase(database);
        } catch (Exception ex) {
          LOGGER.error("health-check-failure-database", ex);
        }
        if (alive.get()) {
          system.schedule(this, (int) (30000 + 30000 * Math.random()));
        }
      }
    }, 5000);

    this.awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    this.awsMetrics = new AWSMetrics(metricsFactory);
    this.s3 = new S3(webBase, awsConfig, awsMetrics);
    this.sqs = new SQS(webBase, awsConfig, awsMetrics);
    String prefix = role.name + "/" + identity.ip + "/" + monitoringPort;
    system.schedule(new NamedRunnable("archive-s3") {
      @Override
      public void execute() throws Exception {
        try {
          s3.uploadLogs(new File("logs"), prefix);
        } catch (Exception ex) {
          LOGGER.error("error-uploading-logs", ex);
        } finally {
          if (alive.get()) {
            system.schedule(this, 60000);
          }
        }
      }
    }, 5000);
    engine = netBase.startGossiping();
    // TODO: promote the concept of the multi-region client as "everyone needs a client"
    services = SimpleExecutor.create("executor");
    FirstPartyServices.install(services, metricsFactory, database, webBase, masterKey, new InternalSigner(publicKeyId, hostKey));

    System.out.println("[Setup]");
    System.out.println("         role:" + role.name);
    System.out.println("           ip: " + identity.ip);
    System.out.println(" service-port: " + servicePort);
    System.out.println(" monitor-port: " + monitoringPort);
    System.out.println("     database: " + database.databaseName);
    System.out.println("     identity: " + identity.ip);
    System.out.println("  logs-prefix: " + prefix);
    System.out.println("[/Setup]");

    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      alive.set(false);
      try {
        system.shutdown();
      } catch (Exception ex) {
      }
      try {
        picker.shutdown();
      } catch (Exception ex) {
      }
      try {
        netBase.shutdown();
      } catch (Exception ex) {
      }
      try {
        webBase.shutdown();
      } catch (Exception ex) {
      }
      try {
        services.shutdown();
      } catch (Exception ex) {

      }
    })));
  }

  public MultiRegionClient makeGlobalClient(Client client) {
    return new MultiRegionClient(database, client, region, hostKey, publicKeyId, finder);
  }

  public Client makeClient(HeatMonitor heat) {
    ClientConfig clientConfig = new ClientConfig();
    ClientMetrics metrics = new ClientMetrics(metricsFactory);
    MachinePicker fallback = (key, callback) -> {
      try {
        String machine = Hosts.pickStableHostFromRegion(database, region, "adama", key.space);
        if (machine == null) {
          throw new NullPointerException("no capacity available");
        }
        callback.success(machine);
      } catch (Exception ex) {
        callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.NET_FINDER_ROUTER_NULL_MACHINE, ex, EXLOGGER));
      }
    };
    ClientRouter router = ClientRouter.FINDER(metrics, finder, fallback, region);
    Client client = new Client(netBase, clientConfig, metrics, router, heat);

    TargetsQuorum targetsQuorum = new TargetsQuorum(metrics, client.getTargetPublisher());
    system.schedule(new NamedRunnable("list-hosts-database") {
      @Override
      public void execute() throws Exception {
        targetsQuorum.deliverDatabase(Hosts.listHosts(database, region, "adama"));
      }
    }, 50);
    this.engine.subscribe("adama", targetsQuorum::deliverGossip);
    return client;
  }

  public class MeasuredSslContext implements Measurable {
    public final SslContext context;

    public MeasuredSslContext(SslContext context) {
      this.context = context;
    }

    @Override
    public long measure() {
      return 1;
    }
  }

  public CertificateFinder makeCertificateFinder() {
    SyncCacheLRU<String, MeasuredSslContext> realCache = new SyncCacheLRU<>(TimeSource.REAL_TIME, minDomainsToHoldTo, maxDomainsToHoldTo, maxDomainsToHoldTo * 2, maxDomainAge, (domain, context) -> {});
    SimpleExecutor executor = SimpleExecutor.create("domain-cache");
    SimpleExecutor executor_db = SimpleExecutor.create("domain-db");
    AsyncSharedLRUCache<String, MeasuredSslContext> cache = new AsyncSharedLRUCache<>(executor, realCache, (domain, callback) -> {
      executor_db.execute(new NamedRunnable("domain-lookup") {
        @Override
        public void execute() throws Exception {
          try {
            Domain lookup = Domains.get(database, domain);
            if (lookup != null && lookup.certificate != null) {
              ObjectNode certificate = Json.parseJsonObject(MasterKey.decrypt(masterKey, lookup.certificate));
              ByteArrayInputStream keyInput = new ByteArrayInputStream(certificate.get("key").textValue().getBytes(StandardCharsets.UTF_8));
              ByteArrayInputStream certInput = new ByteArrayInputStream(certificate.get("cert").textValue().getBytes(StandardCharsets.UTF_8));
              SslContext contextToUse = SslContextBuilder.forServer(certInput, keyInput).build();
              callback.success(new MeasuredSslContext(contextToUse));
              return;
            }
            callback.failure(new ErrorCodeException(ErrorCodes.DOMAIN_TRANSLATE_FAILURE));
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOMAIN_LOOKUP_FAILURE, ex, EXLOGGER));
          }
        }
      });
    });
    return (rawDomain, callback) -> {
      String _domainToLookup = rawDomain;
      { // hyper fast optimistic path
        if (_domainToLookup == null) { // no SNI provided -> use default
          callback.success(null);
          return;
        }
        if (!webConfig.specialDomains.contains(_domainToLookup)) {
          if (_domainToLookup.endsWith("." + webConfig.regionalDomain)) { // the regional domain -> use default
            callback.success(null);
            return;
          }
          for (String globalDomain : webConfig.globalDomains) {
            if (_domainToLookup.endsWith("." + globalDomain)) {
              _domainToLookup = "wildcard." + globalDomain;
              break;
            }
          }
        }
      }

      cache.get(_domainToLookup, new Callback<MeasuredSslContext>() {
        @Override
        public void success(MeasuredSslContext value) {
          callback.success(value.context);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    };
  }
}
