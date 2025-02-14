/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.codegen;

import org.junit.Assert;
import org.junit.Test;

public class VariablePoolTests {
  @Test
  public void flow() {
    VariablePool pool = new VariablePool();
    String a = pool.ask();
    Assert.assertEquals("a", a);
    String b = pool.ask();
    Assert.assertEquals("b", b);
    String c = pool.ask();
    Assert.assertEquals("c", c);
    pool.give(a);
    Assert.assertEquals("a", pool.ask());
    while (!pool.ask().equals("_if")) {
    }
    while (!pool.ask().equals("_do")) {
    }
  }
}
