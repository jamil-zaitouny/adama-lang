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

/** classical for(statement;condition;statement) block loop */
public class For extends Statement {
  public final Token endConditionSemicolon;
  public final Token endParen;
  public final Token forToken;
  public final Token noInitialSemicolon;
  public final Token openParen;
  public Statement advance;
  public Block code;
  public Expression condition;
  public Statement initial;

  public For(final Token forToken, final Token openParen, final Statement initial, final Token noInitialSemicolon, final Expression condition, final Token endConditionSemicolon, final Statement advance, final Token endParen, final Block code) {
    this.forToken = forToken;
    ingest(forToken);
    this.openParen = openParen;
    this.initial = initial;
    this.noInitialSemicolon = noInitialSemicolon;
    this.condition = condition;
    this.endConditionSemicolon = endConditionSemicolon;
    this.advance = advance;
    this.endParen = endParen;
    this.code = code;
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(forToken);
    yielder.accept(openParen);
    if (initial != null) {
      initial.emit(yielder);
    } else {
      yielder.accept(noInitialSemicolon);
    }
    if (condition != null) {
      condition.emit(yielder);
    }
    yielder.accept(endConditionSemicolon);
    if (advance != null) {
      advance.emit(yielder);
    }
    yielder.accept(endParen);
    code.emit(yielder);
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    final var next = environment.scope();
    if (initial != null) {
      initial.typing(next);
    }
    if (condition != null) {
      final var conditionType = condition.typing(next.scopeWithComputeContext(ComputeContext.Computation), null);
      environment.rules.IsBoolean(conditionType, false);
    }
    if (advance != null) {
      advance.typing(next);
    }
    code.typing(next);
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    final var next = environment.scope();
    sb.append("{").tabUp().writeNewline();
    if (initial != null) {
      initial.writeJava(sb, next);
      sb.writeNewline();
    }
    sb.append("for (");
    sb.append(";");
    if (condition != null) {
      if (environment.state.isStatic()) {
        sb.append("__static_state.__goodwill(").append(condition.toArgs(true)).append(") && (");
      } else {
        sb.append("__goodwill(").append(condition.toArgs(true)).append(") && (");
      }
      condition.writeJava(sb, next.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(")");
    } else {
      if (environment.state.isStatic()) {
        sb.append("__static_state.__goodwill(").append(toArgs(true)).append(")");
      } else {
        sb.append("__goodwill(").append(toArgs(true)).append(")");
      }
    }
    sb.append(";");
    if (advance != null) {
      advance.writeJava(sb, next);
    }
    sb.append(") ");
    code.writeJava(sb, next);
    sb.tabDown().writeNewline().append("}");
  }

  @Override
  public void free(FreeEnvironment environment) {
    if (initial != null) {
      initial.free(environment);
    }
    if (condition != null) {
      condition.free(environment);
    }
    if (advance != null) {
      advance.free(environment);
    }
    code.free(environment.push());
  }
}
