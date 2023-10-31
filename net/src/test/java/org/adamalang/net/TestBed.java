/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.net.NetBase;
import org.adamalang.common.net.NetMetrics;
import org.adamalang.common.net.ServerHandle;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.routing.finder.MockFinderService;
import org.adamalang.runtime.data.BoundLocalFinderService;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.client.contracts.RoutingTarget;
import org.adamalang.net.mocks.NaughyHandler;
import org.adamalang.net.server.Handler;
import org.adamalang.net.server.LocalCapacityRequestor;
import org.adamalang.net.server.ServerMetrics;
import org.adamalang.net.server.ServerNexus;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeteringBatchReady;
import org.adamalang.runtime.sys.metering.MeteringPubSub;
import org.junit.Assert;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBed implements AutoCloseable {
  public final NetBase base;
  public final int port;
  public final MachineIdentity identity;
  public final SimpleExecutor clientExecutor;
  public final MeteringPubSub meteringPubSub;
  public final ServerNexus nexus;
  public final AtomicInteger deploymentScans;
  public final CoreService coreService;
  private ServerHandle handle;
  private CountDownLatch serverExit;
  public final DiskMeteringBatchMaker batchMaker;
  private final File billingRoot;
  public final ClientConfig clientConfig;
  public final MockFinderService finderService;

  public TestBed(int port, String code) throws Exception {
    DeploymentFactory.compile("<direct>", "X", code, new HashMap<>(), Deliverer.FAILURE, new TreeMap<>());
    this.base = new NetBase(new NetMetrics(new NoOpMetricsFactory()), MachineIdentity.fromFile(prefixForLocalhost()), 1, 2);
    this.port = port;
    clientExecutor = SimpleExecutor.create("client-executor");
    deploymentScans = new AtomicInteger(0);
    JsonStreamWriter planWriter = new JsonStreamWriter();
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("versions");
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("x");
    planWriter.writeString(code);
    planWriter.endObject();
    planWriter.writeObjectFieldIntro("default");
    planWriter.writeString("x");
    planWriter.endObject();
    DeploymentPlan plan = new DeploymentPlan(planWriter.toString(), (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
    base.deploy("space", plan, new TreeMap<>());

    ExecutorService inMemoryThread = Executors.newSingleThreadScheduledExecutor();
    this.meteringPubSub = new MeteringPubSub(TimeSource.REAL_TIME, base);

    this.coreService =
        new CoreService(
            new CoreMetrics(new NoOpMetricsFactory()),
            base, //
            meteringPubSub.publisher(), //
            new InMemoryDataService(inMemoryThread, TimeSource.REAL_TIME), //
            TimeSource.REAL_TIME,
            2);

    this.identity = this.base.identity;

    billingRoot = new File(File.createTempFile("ADAMATEST_",  "x23").getParentFile(), "Billing-" + System.currentTimeMillis());
    billingRoot.mkdir();

    this.batchMaker = new DiskMeteringBatchMaker(TimeSource.REAL_TIME, clientExecutor, billingRoot, 1800000L, new MeteringBatchReady() {
      @Override
      public void init(DiskMeteringBatchMaker me) {

      }

      @Override
      public void ready(String batchId) {
        System.out.println("Metering Batch Ready:" + batchId);
      }
    });
    finderService = new MockFinderService("the-machine");
    BoundLocalFinderService finder = new BoundLocalFinderService(finderService, "the-region", "the-machine");
    this.nexus = new ServerNexus(this.base, identity, coreService, new ServerMetrics(new NoOpMetricsFactory()), base, finder, new LocalCapacityRequestor() {
      @Override
      public void requestCodeDeployment(String space, Callback<Void> callback) {
        if (deploymentScans.incrementAndGet() == 3) {
          callback.failure(new ErrorCodeException(-13));
          return;
        }
        callback.success(null);
      }
    }, meteringPubSub, batchMaker, port, 2);
    this.handle = null;
    this.serverExit = null;
    this.clientConfig = new TestClientConfig();
  }

  public InstanceClient makeClient() throws Exception {
    return makeClient(null);
  }

  public InstanceClient makeClient(HeatMonitor monitor) throws Exception {
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
    return new InstanceClient(base, clientConfig, metrics, monitor, new RoutingTarget() {
      @Override
      public void integrate(String target, Collection<String> spaces) {

      }
    },"127.0.0.1:" + port, clientExecutor);
  }

  public static String prefixForLocalhost() {
    for (String search : new String[] {"./", "../", "./grpc/"}) {
      String candidate = search + "localhost.identity";
      File file = new File(candidate);
      if (file.exists()) {
        return candidate;
      }
    }
    throw new NullPointerException("could not find identity.localhost");
  }

  public void startServer() throws Exception {
    if (handle == null) {
      handle = this.base.serve(port, (upstream) -> new Handler(nexus, upstream));
      serverExit = new CountDownLatch(1);
      CountDownLatch waitUntilThreadUp = new CountDownLatch(1);
      new Thread(() -> {
        waitUntilThreadUp.countDown();
        handle.waitForEnd();
        serverExit.countDown();
      }).start();
      Assert.assertTrue(waitUntilThreadUp.await(1000, TimeUnit.MILLISECONDS));
    } else {
      Assert.fail();
    }
  }

  public void startManual(org.adamalang.common.net.Handler handler) throws Exception {
    if (handle == null) {
      handle = this.base.serve(port, handler);
      serverExit = new CountDownLatch(1);
      CountDownLatch waitUntilThreadUp = new CountDownLatch(1);
      new Thread(() -> {
        waitUntilThreadUp.countDown();
        handle.waitForEnd();
        serverExit.countDown();
      }).start();
      Assert.assertTrue(waitUntilThreadUp.await(1000, TimeUnit.MILLISECONDS));
    } else {
      Assert.fail();
    }
  }

  public NaughyHandler.NaughtyBits naughty() {
    return new NaughyHandler.NaughtyBits(this);
  }

  public void stopServer() throws Exception {
    if (handle != null) {
      handle.kill();
      Assert.assertTrue(serverExit.await(5000, TimeUnit.MILLISECONDS));
      handle = null;
      serverExit = null;
    } else {
      Assert.fail();
    }
  }

  @Override
  public void close() throws Exception {
    for (File file : billingRoot.listFiles()) {
      file.delete();
    }
    billingRoot.delete();
    if (handle != null) {
      stopServer();
    }
    base.shutdown();
    clientExecutor.shutdown();
  }
}
