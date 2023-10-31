/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.capacity.MachinePicker;

import java.util.HashMap;

public class MockMachinePicker implements MachinePicker {
  private HashMap<Key, String> map;

  public MockMachinePicker() {
    this.map = new HashMap<>();
  }

  @Override
  public synchronized void pickHost(Key key, Callback<String> callback) {
    String value = map.get(key);
    if (value != null) {
      callback.success(value);
    } else {
      callback.failure(new ErrorCodeException(-123));
    }
  }
}
