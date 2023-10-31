/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Consumer;

/** Ensures that clients are available from gossip and the hosts databases */
public class TargetsQuorum {
  private final LocalRegionClientMetrics metrics;
  private final Consumer<Collection<String>> destination;
  private final TreeSet<String> fromDatabase;
  private final TreeSet<String> fromGossip;
  private long created;

  public TargetsQuorum(LocalRegionClientMetrics metrics, Consumer<Collection<String>> destination) {
    this.metrics = metrics;
    this.destination = destination;
    this.fromGossip = new TreeSet<>();
    this.fromDatabase = new TreeSet<>();
    this.created = System.currentTimeMillis();
  }

  public void deliverDatabase(Collection<String> targets) {
    fromDatabase.clear();
    fromDatabase.addAll(targets);
    reconcileAndShip();
  }

  public void deliverGossip(Collection<String> targets) {
    fromGossip.clear();
    fromGossip.addAll(targets);
    reconcileAndShip();
  }

  public void reconcileAndShip() {
    TreeSet<String> intersection = new TreeSet<>();
    for (String x : fromDatabase) {
      if (fromGossip.contains(x)) {
        intersection.add(x);
      }
    }
    metrics.client_host_set_database_size.set(fromDatabase.size());
    metrics.client_host_set_gossip_size.set(fromGossip.size());
    if (intersection.size() * 2 < fromDatabase.size()) {
      metrics.client_host_set_invalid.set((System.currentTimeMillis() - created) > 30000 ? 1 : 0);
      destination.accept(fromDatabase);
    } else {
      metrics.client_host_set_invalid.set(0);
      destination.accept(intersection);
    }
  }
}
