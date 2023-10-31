/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Metering;
import org.adamalang.mysql.model.Sentinel;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;

// Collect metering records from all hosts via gossip and sync them to the metering table.
public class MeteringAggregator {
  // TODO: turn Client into the multi-region client
  public static void kickOff(OverlordMetrics metrics, LocalRegionClient client, DataBase dataBase, ConcurrentCachedHttpHandler handler) {
    SimpleExecutor executor = SimpleExecutor.create("metering-aggregator");
    FixedHtmlStringLoggerTable table = new FixedHtmlStringLoggerTable(32, "target", "batch", "time");
    executor.schedule(new NamedRunnable("metering-fetch") {
      @Override
      public void execute() throws Exception {
        NamedRunnable self = this;
        client.randomMeteringExchange(new MeteringStream() {
          private boolean gotFinished = false;

          @Override
          public void handle(String target, String batch, Runnable after) {
            metrics.metering_fetch_found.run();
            executor.execute(new NamedRunnable("handle-metering-batch") {
              @Override
              public void execute() throws Exception {
                long now = System.currentTimeMillis();
                if (!batch.contains("\"spaces\":{}")) {
                  Metering.recordBatch(dataBase, target, batch, now);
                  table.row(target, batch, Long.toString(now));
                  metrics.metering_fetch_saved.run();
                } else {
                  // don't bother saving an empty batch
                  metrics.metering_fetch_empty.run();
                }
                after.run();
              }
            });
          }

          @Override
          public void failure(int code) {
            metrics.metering_fetch_failed.run();
            finished();
          }

          @Override
          public void finished() {
            executor.execute(new NamedRunnable("got-finished") {
              @Override
              public void execute() throws Exception {
                Sentinel.ping(dataBase, "metering", System.currentTimeMillis());
                if (!gotFinished) {
                  gotFinished = true;
                  metrics.metering_fetch_finished.run();
                  handler.put("/metering", table.toHtml("Recent Metering Data"));
                  executor.schedule(self, 10000);
                }
              }
            });
          }
        });
      }
    }, 1000 * 5);
  }
}
