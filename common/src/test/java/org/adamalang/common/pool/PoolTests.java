/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.pool;

import org.junit.Assert;
import org.junit.Test;

public class PoolTests {
  @Test
  public void trivial() {
    Pool<String> p = new Pool<>();
    Assert.assertEquals(0, p.size());
    p.bumpUp();
    Assert.assertEquals(1, p.size());
    p.bumpDown();
    Assert.assertEquals(0, p.size());
    p.bumpUp();
    p.bumpUp();
    p.bumpUp();
    Assert.assertEquals(3, p.size());
    p.bumpDown();
    Assert.assertEquals(2, p.size());
    p.add("xyz");
    Assert.assertEquals("xyz", p.next());
    Assert.assertNull(p.next());
    p.add("x");
    p.add("y");
    p.add("z");
    Assert.assertEquals("x", p.next());
    Assert.assertEquals("y", p.next());
    Assert.assertEquals("z", p.next());
    Assert.assertNull(p.next());
  }
}
