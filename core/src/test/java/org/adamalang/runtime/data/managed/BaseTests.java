/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data.managed;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.data.mocks.MockArchiveDataSource;
import org.adamalang.runtime.data.mocks.MockFinderService;
import org.adamalang.runtime.data.mocks.MockPostDocumentDelete;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BaseTests {
  @FunctionalInterface
  public static interface ThrowConsumer<T> {
    public void run(T item) throws Exception;
  }

  @Test
  public void coverage() throws Exception {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      MockArchiveDataSource data = new MockArchiveDataSource(new InMemoryDataService(executor, TimeSource.REAL_TIME));
      flow((base) -> {
        CountDownLatch latch = new CountDownLatch(2);
        base.on(new Key("space", "key"), (machine) -> {
          latch.countDown();
        });
        base.on(new Key("space", "key"), (machine) -> {
          latch.countDown();
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      }, data);
    } finally {
      executor.shutdown();
    }
  }

  public static void flow(ThrowConsumer<Base> body, ArchivingDataService data) throws Exception {
    MockFinderService mockFinder = new MockFinderService("test-machine");
    mockFinder.bindLocal(new Key("space", "key"));
    MockPostDocumentDelete delete = new MockPostDocumentDelete();
    SimpleExecutor bexecutor = SimpleExecutor.create("executor");
    Base base = new Base(mockFinder, data, delete, "test-region", "test-machine", bexecutor, 1000);
    try {
      body.run(base);
    } finally {
      bexecutor.shutdown().await(1000, TimeUnit.MILLISECONDS);
    }
    while (base.reportFailureGetRetryBackoff() < 2000) {
    }
    for (int k = 0; k < 1000; k++) {
      base.reportSuccess();
    }
  }
}
