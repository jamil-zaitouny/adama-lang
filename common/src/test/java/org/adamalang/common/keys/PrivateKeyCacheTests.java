/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.keys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PrivateKeyCacheTests {
  @Test
  public void flow() throws Exception {
    ArrayList<NamedRunnable> runnables = new ArrayList<>();
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
    PrivateKey one = keyPairGen.generateKeyPair().getPrivate();
    CountDownLatch latch = new CountDownLatch(4);
    PrivateKeyCache cache = new PrivateKeyCache(new SimpleExecutor() {
      @Override
      public void execute(NamedRunnable command) {
        runnables.add(command);
      }

      @Override
      public Runnable schedule(NamedRunnable command, long milliseconds) {
        return null;
      }

      @Override
      public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
        return null;
      }

      @Override
      public CountDownLatch shutdown() {
        return null;
      }
    }) {
      @Override
      protected PrivateKey find(PrivateKeyCache.SpaceKeyIdPair pair) {
        if (pair.space.equals("one")) {
          return one;
        }
        return null;
      }
    };
    cache.get("one", 1, new Callback<PrivateKey>() {
      @Override
      public void success(PrivateKey value) {
        Assert.assertNotNull(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    cache.get("one", 1, new Callback<PrivateKey>() {
      @Override
      public void success(PrivateKey value) {
        Assert.assertNotNull(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    cache.get("space", 1, new Callback<PrivateKey>() {
      @Override
      public void success(PrivateKey value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(643100, ex.code);
        latch.countDown();
      }
    });
    while (runnables.size() > 0) {
      runnables.remove(0).run();
    }
    cache.get("one", 1, new Callback<PrivateKey>() {
      @Override
      public void success(PrivateKey value) {
        Assert.assertNotNull(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
  }
}
