/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.junit.Assert;
import org.junit.Test;

public class RxGuardTests {
  @Test
  public void dump() {
    final var d = new RxGuard(null);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("", writer.toString());
  }

  @Test
  public void flow() {
    final var guard = new RxGuard(null);
    Assert.assertEquals(1, guard.getGeneration());
    Assert.assertEquals(true, guard.invalid);
    guard.__commit(null, null, null);
    Assert.assertEquals(65522, guard.getGeneration());
    Assert.assertEquals(false, guard.invalid);
    final var child = new MockRxChild();
    guard.__subscribe(child);
    guard.__raiseInvalid();
    child.assertInvalidateCount(0);
    Assert.assertEquals(-1900333, guard.getGeneration());
    Assert.assertEquals(true, guard.invalid);
    guard.__revert();
    Assert.assertEquals(false, guard.invalid);
    Assert.assertEquals(42333092, guard.getGeneration());
    guard.__insert(null);
    guard.__patch(null);
  }
}
