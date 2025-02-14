/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets;

import org.adamalang.common.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/** Facts about an asset which are computed */
public class AssetFact {
  public final long size;
  public final String md5;
  public final String sha384;

  public AssetFact(long size, String md5, String sha384) {
    this.size = size;
    this.md5 = md5;
    this.sha384 = sha384;
  }

  public static AssetFact of(AssetUploadBody body) throws IOException  {
    MessageDigest md5 = Hashing.md5();
    MessageDigest sha384 = Hashing.sha384();
    long size = 0;
    File file = body.getFileIfExists();
    if (file != null && file.exists()) {
      try(FileInputStream input = new FileInputStream(file)) {
        byte[] chunk = new byte[8196];
        int sz;
        while ((sz = input.read(chunk)) >= 0) {
          size += sz;
          md5.update(chunk, 0, sz);
          sha384.update(chunk, 0, sz);
       }
      }
    } else {
      byte[] bytes = body.getBytes();
      size = bytes.length;
      md5.update(bytes);
      sha384.update(bytes);
    }
    return new AssetFact(size, Hashing.finishAndEncode(md5), Hashing.finishAndEncode(sha384));
  }
}
