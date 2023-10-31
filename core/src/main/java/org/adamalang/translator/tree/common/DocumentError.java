/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.json.JsonStreamWriter;

/**
 * Defines an error within the document that can also be tied to a specific position within the
 * document
 */
public class DocumentError {
  public final String file;
  public final String message;
  public final DocumentPosition position;

  /**
   * construct the error
   * @param position where within the file the error happened
   * @param message what is the message for the error
   */
  public DocumentError(final DocumentPosition position, final String message) {
    if (position == null || message == null) {
      throw new NullPointerException();
    }
    this.file = position.getSource();
    this.message = message;
    this.position = position;
  }

  /** write the error out into the given ObjectNode using the LSP format */
  public String json() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("range");
    position.dump(writer);

    writer.writeObjectFieldIntro("severity");
    writer.writeInteger(1);

    writer.writeObjectFieldIntro("source");
    writer.writeString("error");

    writer.writeObjectFieldIntro("message");
    writer.writeString(message);

    if (file != null) {
      writer.writeObjectFieldIntro("file");
      writer.writeString(file);
    }

    writer.endObject();
    return writer.toString();
  }
}
