/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.ops;

import org.junit.Test;

public class SilentDocumentMonitorTests {
  @Test
  public void coverage() {
    final var monitor = new SilentDocumentMonitor();
    monitor.assertFailureAt(0, 0, 0, 0, 0, 0);
    monitor.goodwillFailureAt(0, 0, 0, 0);
    monitor.push("hi");
    monitor.pop(0, false);
  }
}
