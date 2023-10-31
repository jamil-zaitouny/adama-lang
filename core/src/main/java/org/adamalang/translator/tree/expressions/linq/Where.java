/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.linq;

import org.adamalang.translator.codegen.CodeGenIndexing;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.FieldLookup;
import org.adamalang.translator.tree.expressions.Lookup;
import org.adamalang.translator.tree.expressions.operators.BinaryExpression;
import org.adamalang.translator.tree.expressions.operators.Parentheses;
import org.adamalang.translator.tree.operands.BinaryOp;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetCommon;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/** performs a filter on an sql statemeent ($sql) where (expr) */
public class Where extends LinqExpression implements LatentCodeSnippet {
  public final Expression expression;
  public final Token tokenWhere;
  private final Token aliasToken;
  private final ArrayList<String> applyQuerySetStatements;
  private final TreeMap<String, String> closureTypes;
  private final TreeMap<String, TyType> closureTyTypes;
  private final Token colonToken;
  private final StringBuilder exprCode;
  private final StringBuilder indexKeysExpr;
  private final StringBuilder primaryKeyExpr;
  private final ArrayList<String> trapBuilder;
  private int generatedClassId;
  private String iterType;
  private StructureStorage structureStorage;
  private boolean writtenDependentExpressionsForClosure;

  public Where(final Expression sql, final Token tokenWhere, final Token aliasToken, final Token colonToken, final Expression expression) {
    super(sql);
    this.tokenWhere = tokenWhere;
    this.expression = expression;
    this.aliasToken = aliasToken;
    this.colonToken = colonToken;
    ingest(sql);
    ingest(expression);
    structureStorage = null;
    trapBuilder = new ArrayList<>();
    closureTypes = new TreeMap<>();
    generatedClassId = -1;
    iterType = "?";
    exprCode = new StringBuilder();
    primaryKeyExpr = new StringBuilder();
    indexKeysExpr = new StringBuilder();
    applyQuerySetStatements = new ArrayList<>();
    closureTyTypes = new TreeMap<>();
    writtenDependentExpressionsForClosure = false;
  }

  public static Expression findIndex(final Expression root, final String aliasName, final String indexName, BinaryOp mode) {
    if (root instanceof Parentheses) {
      return findIndex(((Parentheses) root).expression, aliasName, indexName, mode);
    }
    if (root instanceof BinaryExpression) {
      // if it is an &&, then search both branches
      if (((BinaryExpression) root).op == BinaryOp.LogicalAnd) {
        final var left = findIndex(((BinaryExpression) root).left, aliasName, indexName, mode);
        if (left != null) {
          return left;
        }
        return findIndex(((BinaryExpression) root).right, aliasName, indexName, mode);
      }
      if (((BinaryExpression) root).op == mode) {
        if (isExpressionIndexedVariable(((BinaryExpression) root).left, aliasName, indexName)) {
          return ((BinaryExpression) root).right;
        } else if (isExpressionIndexedVariable(((BinaryExpression) root).right, aliasName, indexName)) {
          return ((BinaryExpression) root).left;
        }
      }
    }
    return null;
  }

  public static boolean isExpressionIndexedVariable(final Expression root, final String aliasName, final String variableCheck) {
    if (root instanceof Parentheses) {
      return isExpressionIndexedVariable(((Parentheses) root).expression, aliasName, variableCheck);
    }
    if (aliasName == null && root instanceof Lookup) {
      return variableCheck.equals(((Lookup) root).variableToken.text);
    } else if (aliasName != null && root instanceof FieldLookup) {
      if (((FieldLookup) root).fieldNameToken.text.equals(variableCheck)) {
        return isExpressionIndexedVariable(((FieldLookup) root).expression, null, aliasName);
      }
    }
    return false;
  }

  private void buildIndex(final Environment environment) {
    final var intersectCodeByName = new TreeMap<String, String>();
    final var intersectModeByName = new TreeMap<String, String>();
    indexKeysExpr.append("new int[] {");
    var first = true;
    var index = 0;
    for (final Map.Entry<String, FieldDefinition> entry : structureStorage.fields.entrySet()) {
      if (!structureStorage.indexSet.contains(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      CodeGenIndexing.IndexClassification classification = new CodeGenIndexing.IndexClassification(fieldType);
      if (classification.good) {
        // Here is where we wrap this to search for <, <=, ==, >=, >
        // This will let us complete #20 for integers and enums
        var indexValue = findIndex(expression, aliasToken != null ? aliasToken.text : null, entry.getKey(), BinaryOp.Equal);
        String indexLookupMode = "IndexQuerySet.LookupMode.Equals";

        if (indexValue == null && classification.isIntegral) {
          for (BinaryOp mode : new BinaryOp[] {BinaryOp.LessThan, BinaryOp.LessThanOrEqual, BinaryOp.GreaterThan, BinaryOp.GreaterThanOrEqual}) {
            indexValue = findIndex(expression, aliasToken != null ? aliasToken.text : null, entry.getKey(), mode);
            if (indexValue != null) {
              indexLookupMode = "IndexQuerySet.LookupMode." + mode;
              break;
            }
          }
        }

        if (indexValue != null) {
          intersectModeByName.put(entry.getKey(), indexLookupMode);
          var indexValueString = compileIndexExpr(indexValue, environment);
          if (indexValueString != null) {
            if (classification.useHashCode) {
              indexValueString += ".hashCode()";
            }
            if (classification.requiresToInt) {
              indexValueString += ".toInt()";
            }
            if (classification.isBoolean) {
              indexValueString = "((" + indexValueString + ") ? 1 : 0)";
            }
            intersectCodeByName.put(entry.getKey(), indexValueString);
            if (first) {
              first = false;
            } else {
              indexKeysExpr.append(", ");
            }
            indexKeysExpr.append(index).append(", ").append(indexValueString);
          }
        }
        index++;
      }
    }
    indexKeysExpr.append("}");
    for (var k = 0; k < structureStorage.indices.size(); k++) {
      String nameToUse = structureStorage.indices.get(k).nameToken.text;
      final var code = intersectCodeByName.get(nameToUse);
      if (code != null) {
        applyQuerySetStatements.add("__set.intersect(" + k + ", " + code + ", "+intersectModeByName.get(nameToUse)+");");
      }
    }
  }

  private String compileIndexExpr(final Expression indexValue, final Environment prior) {
    try {
      final var nope = prior.watch((x, ty) -> {
        throw new RuntimeException();
      }).scopeWithComputeContext(ComputeContext.Computation);
      for (final Map.Entry<String, TyType> whatIs : closureTyTypes.entrySet()) {
        nope.define(whatIs.getKey(), whatIs.getValue(), true, whatIs.getValue());
      }
      final var str = new StringBuilder();
      indexValue.writeJava(str, nope);
      return str.toString();
    } catch (final Throwable t) {
      return null;
    }
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    sql.emit(yielder);
    yielder.accept(tokenWhere);
    if (aliasToken != null) {
      yielder.accept(aliasToken);
      yielder.accept(colonToken);
    }
    expression.emit(yielder);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    generatedClassId = environment.document.inventClassId();
    environment.document.add(this);
    // the environment must have a read boundary for native types, because we about
    // to misbehave
    final var typeFrom = sql.typing(environment, null);
    if (typeFrom != null && environment.rules.IsNativeListOfStructure(typeFrom, false)) {
      final var storageType = (IsStructure) environment.rules.Resolve(((TyNativeList) environment.rules.Resolve(typeFrom, false)).elementType, false);
      structureStorage = storageType.storage();
      final var watch = environment.watch((name, tyUn) -> {
        TyType ty = environment.rules.Resolve(tyUn, false);
        if (GlobalObjectPool.ignoreCapture(name, ty)) {
          return;
        }
        if (!closureTypes.containsKey(name) && ty != null) {
          closureTyTypes.put(name, ty);
          closureTypes.put(name, ty.getJavaConcreteType(environment));
        }
      }).captureSpecials();
      HashMap<String, TyType> specialsUsed = watch.specials();
      final var next = watch.scopeWithComputeContext(ComputeContext.Computation);
      iterType = "RTx" + storageType.name();
      var toUse = next;
      if (aliasToken != null) {
        next.define(aliasToken.text, (TyType) storageType, true, new DocumentPosition().ingest(aliasToken));
      } else {
        toUse = next.trap(name -> {
          final var result = next.lookupDirect(name);
          if (result != null) {
            return result;
          }
          final var fieldDef = structureStorage.fields.get(name);
          TyType resolvedType = null;
          if (fieldDef != null) {
            resolvedType = RuleSetCommon.Resolve(environment, fieldDef.type, false);
            var addGet = false;
            if (resolvedType instanceof DetailComputeRequiresGet) {
              addGet = true;
              resolvedType = RuleSetCommon.Resolve(environment, ((DetailComputeRequiresGet) resolvedType).typeAfterGet(environment), false);
            }
            if (resolvedType != null) {
              final var nativeType = resolvedType.getJavaConcreteType(environment);
              final var trapToAdd = nativeType + " " + name + " = __obj." + name + (addGet ? ".get();" : ";");
              trapBuilder.add(trapToAdd);
              next.define(name, resolvedType, true, resolvedType);
            }
          }
          return resolvedType;
        });
      }
      final var expressionType = expression.typing(toUse, null);
      for (Map.Entry<String, TyType> entry : specialsUsed.entrySet()) {
        closureTyTypes.put(entry.getKey(), entry.getValue());
        closureTypes.put(entry.getKey(), entry.getValue().getJavaConcreteType(environment));
      }
      environment.rules.IsBoolean(expressionType, false);
      return typeFrom.makeCopyWithNewPosition(this, typeFrom.behavior);
    }
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (passedTypeChecking() && structureStorage != null) {
      sql.writeJava(sb, environment);
      sb.append(".where(").append(intermediateExpression ? "false, " : "true, ").append("new __CLOSURE_WhereClause" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getKey());
      }
      // list the variables
      sb.append("))");
      if (!writtenDependentExpressionsForClosure) {
        expression.writeJava(exprCode, environment.scopeWithComputeContext(ComputeContext.Computation));
        final var primaryKey = findIndex(expression, aliasToken != null ? aliasToken.text : null, "id", BinaryOp.Equal);
        if (primaryKey != null) {
          boolean forceId = false;
          if (primaryKey.typing(environment, null) instanceof TyNativeMaybe) {
            primaryKeyExpr.append("LibMath.forceId(");
            forceId = true;
          }
          primaryKey.writeJava(primaryKeyExpr, environment.scopeWithComputeContext(ComputeContext.Computation));
          if (forceId) {
            primaryKeyExpr.append(")");
          }
        } else {
          primaryKeyExpr.append("null");
        }
        buildIndex(environment.scopeWithComputeContext(ComputeContext.Computation));
        writtenDependentExpressionsForClosure = true;
      }
    }
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    sb.append("private class __CLOSURE_WhereClause" + generatedClassId + " implements WhereClause<" + iterType + "> {").tabUp().writeNewline();
    for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
      sb.append("private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public int[] getIndices() {").tabUp().writeNewline();
    sb.append("return ").append(indexKeysExpr.toString()).append(";");
    sb.tabDown().writeNewline().append("}").writeNewline();
    sb.append("@Override").writeNewline();
    if (applyQuerySetStatements.size() == 0) {
      sb.append("public void scopeByIndicies(IndexQuerySet __set) {}").writeNewline();
    } else {
      sb.append("public void scopeByIndicies(IndexQuerySet __set) {").tabUp().writeNewline();
      for (var k = 0; k < applyQuerySetStatements.size(); k++) {
        sb.append(applyQuerySetStatements.get(k));
        if (k + 1 < applyQuerySetStatements.size()) {
          sb.writeNewline();
        }
      }
      sb.tabDown().writeNewline().append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public Integer getPrimaryKey() {").tabUp().writeNewline();
    sb.append("return ").append(primaryKeyExpr.toString()).append(";");
    sb.tabDown().writeNewline().append("}").writeNewline();
    if (closureTypes.size() > 0) {
      sb.append("private __CLOSURE_WhereClause" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getValue()).append(" ").append(entry.getKey());
      }
      sb.append(") {").tabUp().writeNewline();
      var untilTabDown = closureTypes.size();
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        sb.append("this." + entry.getKey() + " = " + entry.getKey() + ";");
        if (--untilTabDown <= 0) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public boolean test(").append(iterType);
    if (aliasToken != null) {
      sb.append(" ").append(aliasToken.text).append(") {");
    } else {
      sb.append(" __obj) {");
    }
    sb.tabUp().writeNewline();
    for (final String trapToWrite : trapBuilder) {
      sb.append(trapToWrite).writeNewline();
    }
    sb.append(String.format("__code_cost ++;")).writeNewline();
    sb.append("return " + exprCode.toString() + ";").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  @Override
  public void free(FreeEnvironment environment) {
    sql.free(environment);
    FreeEnvironment next = environment;
    if (aliasToken != null) {
      next = next.push();
      next.define(aliasToken.text);
    }
    expression.free(next);
  }
}
