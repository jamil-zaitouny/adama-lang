package org.adamalang.grpc.client;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.MachineIdentity;
import org.adamalang.grpc.client.contracts.Lifecycle;
import org.adamalang.grpc.client.contracts.QueueAction;
import org.adamalang.grpc.client.routing.RoutingEngine;
import org.adamalang.grpc.proto.InventoryRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;

/** You ask it for clients, and you get clients */
public class InstanceClientFinder {
  private final MachineIdentity identity;
  private final RoutingEngine engine;
  private final ScheduledExecutorService scheduler;
  private final HashMap<String, InstanceClientProxy> clients;
  private final ExceptionLogger logger;

  public InstanceClientFinder(MachineIdentity identity, RoutingEngine engine, ScheduledExecutorService scheduler, ExceptionLogger logger) {
    this.identity = identity;
    this.engine = engine;
    this.scheduler = scheduler;
    this.clients = new HashMap<>();
    this.logger = logger;
  }

  private class InstanceClientProxy implements Lifecycle {
    private InstanceClient client;
    private ArrayList<QueueAction<InstanceClient>> buffer;

    private InstanceClientProxy() {
      client = null;
      buffer = null;
    }

    @Override
    public void connected(InstanceClient client) {
      scheduler.execute(() -> {
        this.client = client;
        if (buffer != null) {
          for (QueueAction<InstanceClient> action : buffer) {
            action.execute(client);
          }
          buffer = null;
        }
      });
    }

    @Override
    public void heartbeat(InstanceClient client, Collection<InventoryRecord> records) {
      engine.integrate(client.target, records);
    }

    public void add(QueueAction<InstanceClient> action) {
      if (client != null) {
        action.execute(client);
      } else {
        if (buffer == null) {
          buffer = new ArrayList<>();
        }
        // TODO: enforce limit
        buffer.add(action);
      }
    }

    @Override
    public void disconnected(InstanceClient client) {
      scheduler.execute(() -> {
        InstanceClientProxy.this.client = null;
      });
    }
  }

  public void prime(Collection<String> targets) {
    for (String target : targets) {
      find(target, null);
    }
  }

  public void find(String target, QueueAction<InstanceClient> action) {
    scheduler.execute(
        () -> {
          // look within the cache
          InstanceClientProxy cached = clients.get(target);
          if (cached == null) {
            // it doesn't exist, so create a proxy to hold the place
            cached = new InstanceClientProxy();

            // create the client and have it feed the proxy
            try {
              new InstanceClient(identity, target, scheduler, cached, logger);
              // record the proxy if the above worked
              clients.put(target, cached);
            } catch (Exception ex) {
              // TODO: figure out why this is the case
              ex.printStackTrace();
            }
          }
          if (action != null) {
            cached.add(action);
          }
        });
  }
}
