/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.operators.Parentheses;

public abstract class LinqExpression extends Expression {
  public final Expression sql;
  protected boolean intermediateExpression;

  public LinqExpression(final Expression sql) {
    intermediateExpression = false;
    this.sql = sql;
    ingest(sql);
    indicateIntermediateExpression(sql);
  }

  protected void indicateIntermediateExpression(final Expression expression) {
    if (expression != null) {
      if (expression instanceof Parentheses) {
        indicateIntermediateExpression(((Parentheses) expression).expression);
      } else if (expression instanceof LinqExpression) {
        ((LinqExpression) expression).intermediateExpression = true;
      }
    }
  }
}
