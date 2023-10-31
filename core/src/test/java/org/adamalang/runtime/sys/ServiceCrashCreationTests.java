/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class ServiceCrashCreationTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG_VAR1 =
      "@static { create { while(true) {} return true; } invent { while(true) {} return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR2 =
      "@static { create { for(;;) {} return true; } invent { for(;;) {} return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR3 =
      "@static { create { do {} while (true); return true; } invent { do {} while (true); return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String SIMPLE_CODE_MSG_VAR4 =
      "@static { create { for(;1 < 2;) {} return true; } invent { for(;1 < 2;) {} return true; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";
  private static final String REJECT =
      "@static { create { return false; } invent { return false; } } public int x; @connected { x = 42; return @who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void cant_create_reject() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(REJECT, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_failure(134259);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent_reject() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(REJECT, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_failure(625676);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create1() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR1, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create2() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR2, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create3() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR3, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_create4() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR4, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, created);
      created.await_failure(180858);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent1() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR1, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent2() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR2, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent3() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR3, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void cant_invent4() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG_VAR4, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, time, 3);
    try {
      MockStreamback streamback = new MockStreamback();
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", null, streamback);
      streamback.await_failure(146558);
    } finally {
      service.shutdown();
    }
  }
}
