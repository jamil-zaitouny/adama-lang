/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

public class DocumentIndex {
  public final String space;
  public final String key;
  public final String created;
  public final String updated;
  public final int seq;
  public final String archive;

  public DocumentIndex(String space, String key, String created, String updated, int seq, String archive) {
    this.space = space;
    this.key = key;
    this.created = created;
    this.updated = updated;
    this.seq = seq;
    this.archive = archive;
  }
}
