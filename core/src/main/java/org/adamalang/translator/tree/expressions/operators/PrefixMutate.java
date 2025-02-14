/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.PrefixMutateOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.CanBumpResult;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.function.Consumer;

/** prefix mutation ($e--, $e++) and prefix change (!, -) */
public class PrefixMutate extends Expression {
  public final Expression expression;
  public final PrefixMutateOp op;
  public final Token opToken;
  private final boolean addGet;
  private CanBumpResult bumpResult;

  public PrefixMutate(final Expression expression, final Token opToken) {
    this.expression = expression;
    this.opToken = opToken;
    op = PrefixMutateOp.fromText(opToken.text);
    ingest(opToken);
    ingest(expression);
    addGet = false;
    bumpResult = CanBumpResult.No;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(opToken);
    expression.emit(yielder);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    final var newContext = op.requiresAssignment ? ComputeContext.Assignment : ComputeContext.Computation;
    TyType result = null;
    if (op == PrefixMutateOp.BumpUp || op == PrefixMutateOp.BumpDown) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpNumeric(result, false);
    } else if (op == PrefixMutateOp.NegateNumber) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpNumeric(result, false);
    } else if (op == PrefixMutateOp.NegateBool) {
      result = expression.typing(environment.scopeWithComputeContext(newContext), null);
      bumpResult = environment.rules.CanBumpBool(result, false);
    }
    if (bumpResult == CanBumpResult.No) {
      return null;
    }
    if (result instanceof DetailComputeRequiresGet && bumpResult.reactive) {
      return ((DetailComputeRequiresGet) result).typeAfterGet(environment).makeCopyWithNewPosition(this, result.behavior);
    }
    return result.makeCopyWithNewPosition(this, result.behavior);
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var newContext = op.requiresAssignment ? ComputeContext.Assignment : ComputeContext.Computation;
    switch (bumpResult) {
      case YesWithNative:
        sb.append(op.javaOp);
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        break;
      case YesWithSetter:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(op.functionCall);
        break;
      case YesWithListTransformSetter:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(".transform((item) -> item").append(op.functionCall).append(")");
        break;
      case YesWithListTransformNative:
        expression.writeJava(sb, environment.scopeWithComputeContext(newContext));
        sb.append(".transform((item) -> ").append(op.javaOp).append("item)");
        break;
    }
  }

  @Override
  public void free(FreeEnvironment environment) {
    expression.free(environment);
  }
}
