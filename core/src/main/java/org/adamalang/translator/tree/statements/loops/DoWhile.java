/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.statements.loops;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.statements.Statement;

import java.util.function.Consumer;

/** classic do {} while (cond); loop */
public class DoWhile extends Statement {
  public final Token closeParen;
  public final Block code;
  public final Expression condition;
  public final Token doToken;
  public final Token endToken;
  public final Token openParen;
  public final Token whileToken;

  public DoWhile(final Token doToken, final Block code, final Token whileToken, final Token openParen, final Expression condition, final Token closeParen, final Token endToken) {
    this.doToken = doToken;
    this.code = code;
    this.whileToken = whileToken;
    this.openParen = openParen;
    this.condition = condition;
    this.closeParen = closeParen;
    this.endToken = endToken;
    ingest(doToken);
    ingest(endToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(doToken);
    code.emit(yielder);
    yielder.accept(whileToken);
    yielder.accept(openParen);
    condition.emit(yielder);
    yielder.accept(closeParen);
    yielder.accept(endToken);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var flow = code.typing(environment.scope());
    final var conditionType = condition.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
    environment.rules.IsBoolean(conditionType, false);
    return flow;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("do ");
    code.writeJava(sb, environment.scope());
    if (environment.state.isStatic()) {
      sb.append(" while (__static_state.__goodwill(").append(condition.toArgs(true)).append(") && (");
    } else {
      sb.append(" while (__goodwill(").append(condition.toArgs(true)).append(") && (");
    }
    condition.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
    sb.append("));");
  }

  @Override
  public void free(FreeEnvironment environment) {
    condition.free(environment);
    code.free(environment.push());
  }
}
