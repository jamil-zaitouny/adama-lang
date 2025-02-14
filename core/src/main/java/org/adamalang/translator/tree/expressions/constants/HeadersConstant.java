/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMap;
import org.adamalang.translator.tree.types.natives.TyNativeString;

import java.util.function.Consumer;

public class HeadersConstant extends Expression {
  public final Token token;

  public HeadersConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (environment.state.isWeb()) {
      environment.mustBeComputeContext(this);
      TyNativeString str = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string"));
      return new TyNativeMap(TypeBehavior.ReadOnlyNativeValue, null, null, null, str, null, str, null);
    } else {
      environment.document.createError(this, "@headers is only available in web paths");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__request.headers");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}
