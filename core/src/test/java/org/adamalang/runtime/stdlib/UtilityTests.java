/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

public class UtilityTests {
  @Test
  public void convert_array() {
    final Function<Integer, Integer[]> makeArray = (final Integer n) -> new Integer[n];
    final Function<String, Integer> conv = (final String s) -> Integer.parseInt(s);
    final var data = new String[] {"123", "42"};
    final var arr = Utility.convertMultiple(data, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }

  @Test
  public void read_array() {
    JsonStreamReader reader = new JsonStreamReader("[1,2,3]");
    Integer[] x = Utility.readArray(reader, (r) -> r.readInteger(), (n) -> new Integer[n]);
    Assert.assertEquals(1, (int) x[0]);
    Assert.assertEquals(2, (int) x[1]);
    Assert.assertEquals(3, (int) x[2]);
  }

  @Test
  public void convert_list() {
    final Function<Integer, Integer[]> makeArray = (final Integer n) -> new Integer[n];
    final Function<String, Integer> conv = (final String s) -> Integer.parseInt(s);
    final var list = new ArrayList<String>();
    list.add("123");
    list.add("42");
    final var arr = Utility.convertMultiple(list, makeArray, conv);
    Assert.assertEquals(123, (int) arr[0]);
    Assert.assertEquals(42, (int) arr[1]);
  }

  @Test
  public void convert_maybe() {
    final var ms = new NtMaybe<String>();
    Assert.assertFalse(Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).has());
    ms.set("123");
    Assert.assertTrue(Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).has());
    Assert.assertEquals(
        123, (int) Utility.convertMaybe(ms, (final String s) -> Integer.parseInt(s)).get());
  }

  @Test
  public void convert_single() {
    Assert.assertEquals(
        123, (int) Utility.convertSingle("123", (final String s) -> Integer.parseInt(s)));
  }

  @Test
  public void coverage() {
    Utility.identity(123);
    Utility.identity(123L);
  }

  @Test
  public void lookup() {
    final var X = new Integer[] {1, 3, 5};
    Assert.assertEquals(1, (int) Utility.lookup(X, 0).get());
    Assert.assertEquals(3, (int) Utility.lookup(X, 1).get());
    Assert.assertEquals(5, (int) Utility.lookup(X, 2).get());
    Assert.assertFalse(Utility.lookup(X, -1).has());
    Assert.assertFalse(Utility.lookup(X, 4).has());
  }

  @Test
  public void lookup_maybe() {
    final var X = new Integer[] {1, 3, 5};
    Assert.assertEquals(1, (int) Utility.lookup(X, new NtMaybe<>(0)).get());
    Assert.assertEquals(3, (int) Utility.lookup(X, new NtMaybe<>(1)).get());
    Assert.assertEquals(5, (int) Utility.lookup(X, new NtMaybe<>(2)).get());
    Assert.assertFalse(Utility.lookup(X, new NtMaybe<>(-1)).has());
    Assert.assertFalse(Utility.lookup(X, new NtMaybe<>(4)).has());
    Assert.assertFalse(Utility.lookup(X, new NtMaybe<>()).has());
  }
}
