/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Test;

import java.util.HashMap;

public class HashKeyTests {
  @Test
  public void flow() {
    HashMap<String, String> map = new HashMap<>();
    while (map.size() < 10000) {
      String key = HashKey.keyOf("xyz", map);
      map.put(key, key);
    }
  }
}
