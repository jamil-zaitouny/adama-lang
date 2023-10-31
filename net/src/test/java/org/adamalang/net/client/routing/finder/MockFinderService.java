/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.BackupResult;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockFinderService implements FinderService {
  private final HashMap<Key, Result> map;

  public MockFinderService() {
    this.map = new HashMap<>();
  }

  private Runnable slowFind;
  private CountDownLatch slowFindLatch;

  public Runnable latchOnSlowFind() {
    slowFindLatch = new CountDownLatch(1);
    return () -> {
      try {
        Assert.assertTrue(slowFindLatch.await(10000, TimeUnit.MILLISECONDS));
        slowFind.run();
        slowFindLatch = null;
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    };
  }

  private boolean willFailCantFindKeyRetry = true;

  @Override
  public void find(Key key, Callback<Result> callback) {
    if (key.key.contains("cant-find")) {
      callback.failure(new ErrorCodeException(-999));
      return;
    }

    if (key.key.contains("retry-key") && willFailCantFindKeyRetry) {
      willFailCantFindKeyRetry = false;
      callback.failure(new ErrorCodeException(-1999));
      return;
    }
    Result result = map.get(key);
    Runnable action = () -> {
      if (result != null) {
        callback.success(result);
      } else {
        callback.failure(new ErrorCodeException(ErrorCodes.UNIVERSAL_LOOKUP_FAILED));
      }
    };

    if (key.key.contains("slow-find") && slowFindLatch != null) {
      slowFind = action;
      slowFindLatch.countDown();
    } else {
      action.run();
    }
  }

  @Override
  public void bind(Key key, String machine, Callback<Void> callback) {
    if (key.key.contains("cant-bind")) {
      callback.failure(new ErrorCodeException(-1234));
      return;
    }
    Result result = map.get(key);
    if (result != null) {
      if (result.location == Location.Archive) {
        map.put(key, new Result(1, Location.Machine, "test-region", machine, result.archiveKey, false));
        callback.success(null);
      } else if (machine.equals(result.machine) && result.location == Location.Machine) {
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-1));
        return;
      }
    } else {
      map.put(key, new Result(1, Location.Machine, "test-region", machine, "", false));
      callback.success(null);
    }
  }

  public void bindLocal(Key key) {
    bind(key, "test-machine", Callback.DONT_CARE_VOID);
  }

  public void bindArchive(Key key, String archiveKey) {
    map.put(key, new Result(1, Location.Archive, "", "", archiveKey, false));
  }

  public void bindOtherMachine(Key key) {
    map.put(key, new Result(1, Location.Machine, "test-region", "other-machine", "", false));
  }

  public void bindOtherRegion(Key key) {
    map.put(key, new Result(1, Location.Machine, "other-region", "other-machine", "", false));
  }

  @Override
  public void free(Key key, String machineOn, Callback<Void> callback) {
    if (key.key.equals("retry-key")) {
      if (!failedRetryKeyInFree) {
        failedRetryKeyInFree = true;
        callback.failure(new ErrorCodeException(-6969));
        return;
      }
    }

    Result result = map.get(key);
    if (result != null) {
      if (machineOn.equals(result.machine) && result.location == Location.Machine) {
        map.put(key, new Result(1, Location.Archive, "", "", result.archiveKey, false));
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-2));
        return;
      }
    } else {
      callback.failure(new ErrorCodeException(-3));
    }
  }
  private boolean failedRetryKeyInBackup = false;
  private boolean failedRetryKeyInFree = false;

  @Override
  public void backup(Key key, BackupResult backupResult, String machineOn, Callback<Void> callback) {
    if (key.key.equals("retry-key")) {
      if (!failedRetryKeyInBackup) {
        failedRetryKeyInBackup = true;
        callback.failure(new ErrorCodeException(-6969));
        return;
      }
    }
    Result result = map.get(key);
    if (result != null) {
      if (machineOn.equals(result.machine) && result.location == Location.Machine) {
        map.put(key, new Result(1, result.location, result.region, result.machine, backupResult.archiveKey, false));
        callback.success(null);
      } else {
        callback.failure(new ErrorCodeException(-4));
        return;
      }
    } else {
      callback.failure(new ErrorCodeException(-5));
    }
  }

  @Override
  public void markDelete(Key key, String machineOn, Callback<Void> callback) {
    if ("cant-mark-delete".equals(key.key)) {
      callback.failure(new ErrorCodeException(-12345));
      return;
    }
    callback.success(null);
  }

  @Override
  public void commitDelete(Key key, String machineOn, Callback<Void> callback) {
    if ("cant-delete".equals(key.key)) {
      callback.failure(new ErrorCodeException(-123456));
      return;
    }
    map.remove(key);
    callback.success(null);
  }

  @Override
  public void list(String machine, Callback<List<Key>> callback) {
    callback.failure(new ErrorCodeException(-123));
  }

  @Override
  public void listDeleted(String machine, Callback<List<Key>> callback) {
    callback.failure(new ErrorCodeException(-123));
  }
}
