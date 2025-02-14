/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.reflect;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class TypeBridgeTests {
  @Test
  public void basics() {
    Assert.assertEquals("int", TypeBridge.getAdamaType(Integer.class, null).getAdamaType());
    Assert.assertEquals("int", TypeBridge.getAdamaType(int.class, null).getAdamaType());
    Assert.assertEquals("bool", TypeBridge.getAdamaType(Boolean.class, null).getAdamaType());
    Assert.assertEquals("bool", TypeBridge.getAdamaType(boolean.class, null).getAdamaType());
    Assert.assertEquals("double", TypeBridge.getAdamaType(Double.class, null).getAdamaType());
    Assert.assertEquals("double", TypeBridge.getAdamaType(double.class, null).getAdamaType());
    Assert.assertEquals("string", TypeBridge.getAdamaType(String.class, null).getAdamaType());
    Assert.assertEquals("long", TypeBridge.getAdamaType(Long.class, null).getAdamaType());
    Assert.assertEquals("long", TypeBridge.getAdamaType(long.class, null).getAdamaType());
    Assert.assertEquals("principal", TypeBridge.getAdamaType(NtPrincipal.class, null).getAdamaType());
  }

  @Test
  public void ntlistNoAnnotation() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(NtList.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }

  @Test
  public void ntMaybeNoAnnotation() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(NtMaybe.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }

  @Test
  public void sanityTestVoid() {
    Assert.assertEquals(null, TypeBridge.getAdamaType(Void.class, null));
    Assert.assertEquals(null, TypeBridge.getAdamaType(void.class, null));
  }

  @Test
  public void unknownType() {
    var worked = false;
    try {
      TypeBridge.getAdamaType(TypeBridgeTests.class, null);
      worked = true;
    } catch (final RuntimeException re) {
    }
    Assert.assertFalse(worked);
  }
}
