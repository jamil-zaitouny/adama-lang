/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.*;
import org.adamalang.common.capacity.BinaryEventOrGate;
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;
import org.adamalang.common.capacity.RepeatingSignal;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.Undeploy;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.ServiceShield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Sketch of the capacity agent */
public class CapacityAgent implements HeatMonitor  {
  private final Logger LOG = LoggerFactory.getLogger(CapacityAgent.class);
  private final CapacityMetrics metrics;
  private final CapacityOverseer overseer;
  private final CoreService service;
  private final Undeploy undeploy;
  private final ServiceHeatEstimator estimator;
  private final LoadMonitor resources;
  private final String region;
  private final String machine;

  private final BinaryEventOrGate add_capacity;
  private final BinaryEventOrGate rebalance;
  private final BinaryEventOrGate rejectNew;
  private final BinaryEventOrGate rejectExisting;
  private final BinaryEventOrGate rejectMessages;

  public CapacityAgent(CapacityMetrics metrics, CapacityOverseer overseer, CoreService service, Undeploy undeploy, ServiceHeatEstimator estimator, SimpleExecutor executor, AtomicBoolean alive, ServiceShield shield, String region, String machine) {
    this.metrics = metrics;
    this.overseer = overseer;
    this.service = service;
    this.undeploy = undeploy;
    this.estimator = estimator;
    this.resources = new LoadMonitor(executor, alive);
    this.region = region;
    this.machine = machine;
    executor.schedule(new NamedRunnable("capacity-offload") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          offloadLowSpacesWhileInExecutor();
          executor.schedule(this, (int) (90000 + 90000 * Math.random()));
        }
      }
    }, 120000); // every two minutes
    this.add_capacity = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 120000, (b) -> {
      executor.execute(new NamedRunnable("capacity-add-capacity") {
        @Override
        public void execute() throws Exception {
          addCapacity();
        }
      });
    }));
    this.rebalance = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 240000, (b) -> {
      executor.execute(new NamedRunnable("capacity-add-rebalance") {
        @Override
        public void execute() throws Exception {
          rebalance();
        }
      });
    }));
    this.rejectNew = new BinaryEventOrGate((b) -> {
      LOG.error(b ? "rejecting new documents" : "accepting new documents");
      metrics.shield_active_new_documents.set(b ? 1 : 0);
      shield.canConnectNew.set(!b);
    });
    this.rejectExisting = new BinaryEventOrGate((b) -> {
      LOG.error(b ? "rejecting traffic to existing documents" : "allowing traffic to existing documents");
      metrics.shield_active_existing_connections.set(b ? 1 : 0);
      shield.canConnectExisting.set(!b);
    });
    this.rejectMessages = new BinaryEventOrGate((b) -> {
      LOG.error(b ? "rejecting messages" : "allowing messages");
      metrics.shield_active_messages.set(b ? 1 : 0);
      shield.canSendMessageExisting.set(!b);
    });

    {
      resources.cpu(new LoadEvent(0.75, add_capacity::a));
      resources.cpu(new LoadEvent(0.85, rebalance::a));
      resources.cpu(new LoadEvent(0.90, rejectNew::a));
      resources.cpu(new LoadEvent(0.95, rejectExisting::a));
      resources.cpu(new LoadEvent(0.98, rejectMessages::a));
    }
    {
      resources.memory(new LoadEvent(0.80, add_capacity::b));
      resources.memory(new LoadEvent(0.85, rebalance::b));
      resources.memory(new LoadEvent(0.90, rejectNew::b));
      resources.memory(new LoadEvent(0.92, rejectExisting::b));
      resources.memory(new LoadEvent(0.95, rejectMessages::b));
    }
  }

  public void offloadLowSpacesWhileInExecutor() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          if (instance.override) {
            continue;
          }
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.empty) {
            undeploy.undeploy(instance.space);
            overseer.remove(instance.space, region, machine, Callback.DONT_CARE_VOID);
            return;
          }
          if (heat.low) {
            // Don't remove capacity from the prime host
            overseer.pickStableHostForSpace(instance.space, region, new Callback<String>() {
              @Override
              public void success(String stableHost) {
                if (!machine.equals(stableHost)) {
                  LOG.error("shed-traffic:" + instance.space);
                  service.shed((key) -> key.space.equals(instance.space));
                }
              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-offload-low-spaces-while-in-executor", ex);
      }
    });
  }

  public void addCapacity() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.hot) {
            overseer.pickNewHostForSpace(instance.space, region, new Callback<String>() {
              String space = instance.space;
              @Override
              public void success(String newHost) {
                overseer.add(instance.space, region, newHost, Callback.DONT_CARE_VOID);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOG.error("failed-to-find-new-capacity:" + space, ex);
              }
            });
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-rebalance-capacity", ex);
      }
    });
  }

  private String hash(Key key, String machine) { // This is a fairly dumb approach, we should sync this up with how to pick a new host
    MessageDigest digest = Hashing.md5();
    digest.update(key.key.getBytes(StandardCharsets.UTF_8));
    digest.update(machine.getBytes(StandardCharsets.UTF_8));
    return Hashing.finishAndEncode(digest);
  }

  public void rebalance() {
    overseer.listAllOnMachine(region, machine, new Callback<List<CapacityInstance>>() {
      @Override
      public void success(List<CapacityInstance> instances) {
        for (CapacityInstance instance : instances) {
          ServiceHeatEstimator.Heat heat = estimator.of(instance.space);
          if (heat.hot) {
            overseer.listWithinRegion(instance.space, instance.region, new Callback<List<CapacityInstance>>() {
              String space = instance.space;
              @Override
              public void success(List<CapacityInstance> instances) {
                service.shed((key) -> {
                  // TODO: unify with the client on how it picks clients
                  /*
                  if (space.equals(key.space)) {
                    String toBeat = hash(key, machine);
                    for (CapacityInstance instance : instances) {
                      if (!machine.equals(instance.machine)) {
                        if (hash(key, instance.machine).compareTo(toBeat) > 0) {
                          return true;
                        }
                      }
                    }
                  }
                  */
                  return false;
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOG.error("failed-rebalance-capacity-listing-region", ex);
              }
            });
          }
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LOG.error("failed-rebalance-capacity", ex);
      }
    });
  }

  @Override
  public void heat(String target, double cpu, double memory) {
    metrics.shield_heat.run();
  }
}
