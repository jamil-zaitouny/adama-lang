/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

/** look up a variable in the current environment and then extract its value */
public class Lookup extends Expression {
  public final Token variableToken;
  private boolean addGet;
  private boolean hide;

  /** the variable to look up */
  public Lookup(final Token variableToken) {
    this.variableToken = variableToken;
    hide = false;
    addGet = false;
    ingest(variableToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(variableToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    var type = environment.lookup(variableToken.text, environment.state.isContextComputation(), this, false);

    if (type instanceof TyNativeGlobalObject) {
      hide = true;
      return type;
    }
    if (type == null) {
      environment.document.createError(this, String.format("The variable '%s' was not defined", variableToken.text));
    }
    if (type != null && environment.state.isContextComputation() && type instanceof DetailComputeRequiresGet) {
      addGet = true;
      type = ((DetailComputeRequiresGet) type).typeAfterGet(environment);
      if (type != null) {
        type = type.makeCopyWithNewPosition(this, type.behavior);
      }
    }
    return type;
  }

  /**
   * note: the context matters here. If we are assigning, then we must return a relevant mode of
   * assignment to the underlying variable.
   */
  @Override
  // move the context into the environment
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.lookup(variableToken.text, environment.state.isContextComputation(), this, true);
    if (!hide) {
      sb.append(variableToken.text);
      if (addGet) {
        sb.append(".get()");
      }
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    environment.require(variableToken.text);
  }
}
