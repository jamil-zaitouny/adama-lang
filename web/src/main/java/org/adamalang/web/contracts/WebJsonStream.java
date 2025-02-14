/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** a very simple stream for the client to get a json stream from the web proxy */
public interface WebJsonStream {
  void data(int connection, ObjectNode node);

  void complete();

  void failure(int code);
}
