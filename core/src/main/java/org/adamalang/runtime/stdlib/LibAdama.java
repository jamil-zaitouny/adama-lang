/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

public class LibAdama {
  public static NtDynamic validate(String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("Validate");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, Scope.makeRootDocument());
      parser.document().accept(document);
      if (!document.check(state)) {
        return new NtDynamic(document.errorsJson());
      } else {
        return new NtDynamic("[]");
      }
    } catch (AdamaLangException ale) {
      return new NtDynamic("[{\"error\":true}]");
    }
  }

  public static NtDynamic reflect(String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      document.setClassName("Validate");
      final var tokenEngine = new TokenEngine("<direct code>", code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, Scope.makeRootDocument());
      parser.document().accept(document);
      if (document.check(state)) {
        JsonStreamWriter writer = new JsonStreamWriter();
        document.writeTypeReflectionJson(writer);
        return new NtDynamic(writer.toString());
      }
    } catch (AdamaLangException ale) {
    }
    return new NtDynamic("null");
  }
}
