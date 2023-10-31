/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.web.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WebLivingDocumentTests {

  private static WebGet simple(String uri) {
    return new WebGet(new WebContext(NtPrincipal.NO_ONE, "Origin", "1.2.3.4"), uri, new TreeMap<>(), new NtDynamic("{}"));
  }

  private static WebResponse get_now(LivingDocument doc, WebGet get) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<WebResponse> value = new AtomicReference<>();
    doc.__web_get(get, new Callback<WebResponse>() {
      @Override
      public void success(WebResponse response) {
        value.set(response);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    return value.get();
  }

  @Test
  public void big_router() throws Exception {
    final var setup = new RealDocumentSetup("@web get / {\n" + "  return {html:\"root\"};\n" + "}\n" + "\n" + "@web get /fixed {\n" + "  return {html:\"fixed path\"};\n" + "}\n" + "\n" + "@web get /path0/$x:int {\n" + "  return {html:\"path integer:\" + x};\n" + "}\n" + "\n" + "@web get /path1/$x:double {\n" + "  return {html:\"path double:\" + x};\n" + "}\n" + "\n" + "@web get /path2/$x:long {\n" + "  return {html:\"path long without child:\" + x};\n" + "}\n" + "\n" + "@web get /path2/$x:long/child {\n" + "  return {html:\"path long with child: \" + x + \"!\"};\n" + "}\n" + "\n" + "@web get /path3/$a* {\n" + "  return {html:\"tail:\" + a};\n" + "}\n" + "\n" + "@web get /path3/$a:string/child {\n" + "  return {html:\"abort tail and go with direct child:\" + a};\n" + "}");
    {
      WebResponse response = get_now(setup.document.document(), simple("/"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("root", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/fixed"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("fixed path", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path0/42"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("path integer:42", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path1/3.14"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("path double:3.14", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path2/342424222"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("path long without child:342424222", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path2/4242/child"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("path long with child: 4242!", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path3/yo/yo/yo/yo"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("tail:yo/yo/yo/yo", response.body);
    }
    {
      WebResponse response = get_now(setup.document.document(), simple("/path3/something/child"));
      Assert.assertNotNull(response);
      Assert.assertEquals("text/html; charset=utf-8", response.contentType);
      Assert.assertEquals("abort tail and go with direct child:something", response.body);
    }
  }

  @Test
  public void put() throws Exception {
    final var setup = new RealDocumentSetup("public int x; message M { int x; } @web put / (M m) { x = m.x; return {html:\"Got it! \" + m.x}; }");
    WebPut put = new WebPut(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), "/", new TreeMap<>(), new NtDynamic("{}"), "{\"x\":42}");
    setup.document.webPut(put, new Callback<WebResponse>() {
      @Override
      public void success(WebResponse value) {

        Assert.assertEquals("Got it! 42", value.body);
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
  }

  @Test
  public void delete() throws Exception {
    final var setup = new RealDocumentSetup("message M { int x; } @web delete / { return {html:\"Deleted\"}; }");
    WebDelete delete = new WebDelete(new WebContext(NtPrincipal.NO_ONE, "origin", "ip"), "/", new TreeMap<>(), new NtDynamic("{}"));
    setup.document.webDelete(delete, new Callback<>() {
      @Override
      public void success(WebResponse value) {
        Assert.assertEquals("Deleted", value.body);
      }

      @Override
      public void failure(ErrorCodeException ex) {
      }
    });
  }
}
