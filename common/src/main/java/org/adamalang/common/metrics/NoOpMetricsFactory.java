/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.metrics;

public class NoOpMetricsFactory implements MetricsFactory {
  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String name) {
    return new RequestResponseMonitor() {
      @Override
      public RequestResponseMonitorInstance start() {
        return new RequestResponseMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void extra() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public StreamMonitor makeStreamMonitor(String name) {
    return new StreamMonitor() {
      @Override
      public StreamMonitorInstance start() {
        return new StreamMonitorInstance() {
          @Override
          public void progress() {

          }

          @Override
          public void finish() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    return new CallbackMonitor() {
      @Override
      public CallbackMonitorInstance start() {
        return new CallbackMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public Runnable counter(String name) {
    return () -> {
    };
  }

  @Override
  public Inflight inflight(String name) {
    return new Inflight() {
      @Override
      public void up() {

      }

      @Override
      public void down() {

      }

      @Override
      public void set(int value) {
      }
    };
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    return new ItemActionMonitor() {
      @Override
      public ItemActionMonitorInstance start() {
        return new ItemActionMonitorInstance() {
          @Override
          public void executed() {

          }

          @Override
          public void rejected() {

          }

          @Override
          public void timeout() {

          }
        };
      }
    };
  }

  @Override
  public void page(String name, String title) {
  }

  @Override
  public void section(String title) {
  }
}
