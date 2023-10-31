/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser;

import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Test;

public class ParserSanityTests {
  @Test
  public void testGithub138_a() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();//\n}".codePoints().iterator()), Scope.makeRootDocument());
    p.document();
  }

  @Test
  public void testGithub138_b() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();/* yay */\n}".codePoints().iterator()), Scope.makeRootDocument());
    p.document();
  }
}
