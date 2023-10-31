/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.LocationType;
import org.adamalang.runtime.data.DocumentLocation;
import org.junit.Assert;

public class SimpleFinderCallback implements Callback<DocumentLocation> {
  public DocumentLocation value;
  public int reads;
  private boolean success;
  private int count;
  private int reason;

  public SimpleFinderCallback() {
    this.success = false;
    this.count = 0;
    this.reason = 0;
    this.reads = 0;
  }

  @Override
  public void success(DocumentLocation value) {
    this.value = value;
    count++;
    success = true;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    count++;
    success = false;
    reason = ex.code;
  }

  public void assertSuccess(LocationType location, String machine, String archiveKey) {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    Assert.assertEquals(location, value.location);
    Assert.assertEquals(machine, value.machine);
    Assert.assertEquals(archiveKey, value.archiveKey);
  }

  public long assertSuccessAndGetId() {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    return value.id;
  }

  public void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
