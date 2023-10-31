/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.privacy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

/** a policy that refers to code within the record */
public class UseCustomPolicy extends Policy {
  public final Token customToken;
  public final ArrayList<String> policyToChecks;
  public final TokenizedItem<Token>[] policyToCheckTokens;
  private final HashSet<String> globals;

  public UseCustomPolicy(final Token customToken, final TokenizedItem<Token>[] policyToCheckTokens) {
    this.customToken = customToken;
    policyToChecks = new ArrayList<>();
    ingest(customToken);
    for (final TokenizedItem<Token> token : policyToCheckTokens) {
      policyToChecks.add(token.item.text);
      ingest(token.item);
      for (final Token afterToken : token.after) {
        ingest(afterToken);
      }
    }
    this.policyToCheckTokens = policyToCheckTokens;
    globals = new HashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(customToken);
    for (final TokenizedItem<Token> token : policyToCheckTokens) {
      token.emitBefore(yielder);
      yielder.accept(token.item);
      token.emitAfter(yielder);
    }
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
    for (final String policyToCheck : policyToChecks) {
      var dcp = owningStructureStorage.policies.get(policyToCheck);
      if (dcp == null) {
        globals.add(policyToCheck);
        dcp = environment.document.root.storage.policies.get(policyToCheck);
        if (dcp == null) {
          environment.document.createError(this, String.format("Policy '%s' was not found", policyToCheck));
        }
      }
    }
  }

  @Override
  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
    sb.append("if (");
    var first = true;
    for (final String policyToCheck : policyToChecks) {
      if (first) {
        first = false;
      } else {
        sb.append(" && ");
      }
      if (globals.contains(policyToCheck)) {
        sb.append("__policy_cache.").append(policyToCheck);
      } else {
        sb.append("__item.__POLICY_").append(policyToCheck).append("(__writer.who)");
      }

    }
    sb.append(") {").tabUp().writeNewline();
    return true;
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.writeString("policy");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
