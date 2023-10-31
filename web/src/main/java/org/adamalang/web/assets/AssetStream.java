/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets;

/** an asset being streamed */
public interface AssetStream {
  void headers(long length, String contentType, String contentMd5);

  void body(byte[] chunk, int offset, int length, boolean last);

  void failure(int code);
}
