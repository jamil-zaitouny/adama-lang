/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.expressions.constants.NoOneClientConstant;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TySimpleNative;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsOrderable;
import org.adamalang.translator.tree.types.traits.assign.AssignmentViaNative;
import org.adamalang.translator.tree.types.traits.details.DetailHasDeltaType;
import org.adamalang.translator.tree.types.traits.details.DetailTypeHasMethods;

import java.util.function.Consumer;

public class TyNativeSecurePrincipal extends TySimpleNative implements //
    DetailHasDeltaType, //
    CanBeMapDomain, //
    IsOrderable, //
    DetailTypeHasMethods, //
    AssignmentViaNative //
{
  public final Token readonlyToken;
  public final Token secureToken;
  public final Token openToken;
  public final Token principalToken;
  public final Token closedToken;

  public TyNativeSecurePrincipal(final TypeBehavior behavior, final Token readonlyToken, final Token secureToken, final Token openToken, final Token principalToken, final Token closedToken) {
    super(behavior, "NtPrincipal", "NtPrincipal");
    this.readonlyToken = readonlyToken;
    this.secureToken = secureToken;
    this.openToken = openToken;
    this.principalToken = principalToken;
    this.closedToken = closedToken;
    ingest(secureToken);
    ingest(closedToken);
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(secureToken);
    yielder.accept(openToken);
    yielder.accept(principalToken);
    yielder.accept(closedToken);
  }

  @Override
  public String getRxStringCodexName() {
    return "RxMap.PrincipalCodec";
  }

  @Override
  public String getAdamaType() {
    return "secure<principal>";
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeSecurePrincipal(newBehavior, readonlyToken, secureToken, openToken, principalToken, closedToken).withPosition(position);
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_value");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("type");
    writer.writeString("secure<principal>");
    writer.endObject();
  }

  @Override
  public String getDeltaType(final Environment environment) {
    return "DPrincipal";
  }

  @Override
  public Expression inventDefaultValueExpression(final DocumentPosition forWhatExpression) {
    return new NoOneClientConstant(Token.WRAP("@no_one")).withPosition(forWhatExpression);
  }

  @Override
  public TyNativeFunctional lookupMethod(String name, Environment environment) {
    TyNativeFunctional exclusive = environment.state.globals.findExtension(this, name);
    if (exclusive != null) {
      return exclusive;
    }
    return environment.state.globals.findExtension(new TyNativePrincipal(behavior, readonlyToken, principalToken), name);
  }
}
