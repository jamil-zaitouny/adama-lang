/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.Callback;

/** tuple of a runnable action along with a callback. This allows us to perform a unit of work, or abort it */
public class Action {
  public final Runnable action;
  public final Callback<?> callback;

  public Action(Runnable action, Callback<?> callback) {
    this.action = action;
    this.callback = callback;
  }
}
