/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

/** Just enough information about the caller to be dangerous */
public interface Caller {
  /** get the pathway to deliver a message */
  Deliverer __getDeliverer();

  /** the key of the document making the call */
  String __getKey();

  /** the space of the document making the call */
  String __getSpace();
}
