/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.impl.GlobalBillingDocumentFinder;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.roles.*;
import org.adamalang.runtime.data.ColdAssetSystem;
import org.adamalang.web.contracts.HttpHandler;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Overlord {
  public static HttpHandler execute(ConcurrentCachedHttpHandler handler, boolean isGlobalOverlord, LocalRegionClient localClient, MultiRegionClient client, Engine engine, MetricsFactory metricsFactory, File targetsDestination, DataBase dataBase, ColdAssetSystem lister, Cloud cloud, AtomicBoolean alive) throws Exception {
    // the overlord has metrics
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);

    // start producing the prometheus targets.json from the gossip engine
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination, handler);

    if (isGlobalOverlord) {
      // delete things
      GlobalSpaceDeleteBot.kickOff(metrics, dataBase, client, alive);

      // kick off the garbage collector to clean up documents
      GlobalGarbageCollector.kickOff(metrics, dataBase, lister, cloud, alive);

      // start doing the accounting work
      // GlobalHourlyAccountant.kickOff(metrics, client, dataBase, handler);

      GlobalStorageReporter.kickOff(metrics, client, dataBase, new GlobalBillingDocumentFinder(dataBase));
    }

    // detect dead things
    DeadDetector.kickOff(metrics, dataBase, alive);

    // do a round to reconcile storage
    ReconcileDirectoryVersusStorage.kickOff(dataBase, metrics, cloud);

    // start aggregating bills from hosts and write them to database
    // MeteringAggregator.kickOff(metrics, localClient, dataBase, handler);

    // make a table of a dump of all gossip
    GossipDumper.kickOff(metrics, engine, handler);

    // build the index
    StringBuilder indexHtmlBuilder = new StringBuilder();
    indexHtmlBuilder.append("<html><head><title>OVERLORD</title></head><body>\n");
    indexHtmlBuilder.append("<a href=\"/heat\">Heat Table</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/targets\">Targets</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/metering\">Recent Metering Data</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/gossip\">Gossip Dump</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/accountant\">Accountant Log</a><br />\n");
    indexHtmlBuilder.append("</body></html>");
    String indexHtml = indexHtmlBuilder.toString();
    handler.put("/", indexHtml);
    return handler;
  }
}
