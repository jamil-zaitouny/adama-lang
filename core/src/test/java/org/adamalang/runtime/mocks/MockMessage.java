/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.algo.HashBuilder;

public class MockMessage extends NtMessageBase {
  public int x;
  public int y;

  public MockMessage() {
    x = 42;
    y = 13;
  }

  public MockMessage(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public MockMessage(JsonStreamReader reader) {
    __ingest(reader);
  }

  @Override
  public void __ingest(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "x":
            x = reader.readInteger();
            break;
          case "y":
            y = reader.readInteger();
            break;
          default:
            reader.skipValue();
        }
      }
    }
  }

  @Override
  public int[] __getIndexValues() {
    return new int[0];
  }

  @Override
  public String[] __getIndexColumns() {
    return new String[0];
  }

  @Override
  public void __writeOut(final JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("x");
    writer.writeInteger(x);
    writer.writeObjectFieldIntro("y");
    writer.writeInteger(y);
    writer.endObject();
  }

  @Override
  public void __hash(HashBuilder __hash) {
    __hash.hashInteger(x);
    __hash.hashInteger(y);
  }

  @Override
  public String toString() {
    JsonStreamWriter writer = new JsonStreamWriter();
    __writeOut(writer);
    return writer.toString();
  }
}
