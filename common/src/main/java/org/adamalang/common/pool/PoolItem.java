/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.pool;

/** a wrapper around an item which is used to report status on the item */
public interface PoolItem<S> {
  /** @return the item */
  S item();

  /** signal the item has a failure and should not be re-used again */
  void signalFailure();

  /** return the item to the pool */
  void returnToPool();
}
