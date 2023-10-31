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
import org.adamalang.translator.tree.expressions.AnonymousTuple;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;
import org.adamalang.translator.tree.types.traits.details.DetailRequiresResolveCall;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TyNativeTuple extends TyType implements //
    CanBeNativeArray, //
    DetailRequiresResolveCall {

  private final Token readonlyToken;
  private final Token tupleToken;
  private final ArrayList<PrefixedType> types;
  private final StructureStorage storage;
  private Token endToken;
  public TyNativeTuple(final TypeBehavior behavior, final Token readonlyToken, final Token tupleToken) {
    super(behavior);
    this.readonlyToken = readonlyToken;
    this.tupleToken = tupleToken;
    this.types = new ArrayList<>();
    this.endToken = null;
    this.storage = new StructureStorage(Token.WRAP("__Tuple"), StorageSpecialization.Message, true, tupleToken);
    ingest(readonlyToken);
    ingest(tupleToken);
  }

  @Override
  public void emitInternal(Consumer<Token> yielder) {
    if (readonlyToken != null) {
      yielder.accept(readonlyToken);
    }
    yielder.accept(tupleToken);
    for (PrefixedType pt : types) {
      yielder.accept(pt.token);
      pt.type.emit(yielder);
    }
    yielder.accept(endToken);
  }

  @Override
  public String getAdamaType() {
    StringBuilder sb = new StringBuilder();
    sb.append("tuple");
    for (PrefixedType pt : types) {
      sb.append(pt.token.text);
      sb.append(pt.type.getAdamaType());
    }
    sb.append(">");
    return sb.toString();
  }

  @Override
  public String getJavaBoxType(Environment environment) {
    throw new UnsupportedOperationException("the tuple must be resolved");
  }

  @Override
  public String getJavaConcreteType(Environment environment) {
    throw new UnsupportedOperationException("the tuple must be resolved");
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(DocumentPosition position, TypeBehavior newBehavior) {
    TyNativeTuple tuple = new TyNativeTuple(this.behavior, readonlyToken, tupleToken);
    for (PrefixedType pt : types) {
      tuple.add(pt.token, pt.type);
    }
    tuple.finish(endToken);
    return tuple.withPosition(position);
  }

  public void add(Token token, TyType type) {
    ingest(token);
    storage.add(FieldDefinition.invent(type, AnonymousTuple.nameOf(types.size())));
    types.add(new PrefixedType(token, type));
  }

  public void finish(Token token) {
    this.endToken = token;
    ingest(endToken);
  }

  @Override
  public void typing(Environment environment) {
    // handled by environment.rules.Revolve
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_tuple");
    writeAnnotations(writer);
    writer.writeObjectFieldIntro("types");
    writer.beginArray();
    for (PrefixedType pt : types) {
      pt.type.writeTypeReflectionJson(writer, source);
    }
    writer.endArray();
    writer.endObject();
  }

  @Override
  public TyType resolve(Environment environment) {
    TyType typeEstimate = new TyNativeMessage(this.behavior, tupleToken, Token.WRAP("_TupleConvert_" + environment.autoVariable()), storage);
    return environment.rules.EnsureRegisteredAndDedupe(typeEstimate, false);
  }

  public static class PrefixedType {
    public final Token token;
    public final TyType type;

    public PrefixedType(Token token, TyType type) {
      this.token = token;
      this.type = type;
    }
  }
}
