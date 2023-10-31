/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

public class ConfigObjectTests {
  @Test
  public void string() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    Assert.assertNull(config.strOf("key", null));
    Assert.assertEquals("123", config.strOf("key", "123"));
    root.put("key", "42");
    Assert.assertEquals("42", config.strOf("key", "123"));
    root.putNull("key");
    Assert.assertEquals("123", config.strOf("key", "123"));
    root.remove("key");
    Assert.assertEquals("123", config.strOf("key", "123"));
  }

  @Test
  public void stringsOf() {
    ObjectNode root = Json.newJsonObject();
    root.putArray("z").add("1").add("2").add("3");
    ConfigObject config = new ConfigObject(root);
    Assert.assertEquals(2, config.stringsOf("key", new String[]{"x", "y"}).length);
    Assert.assertEquals(3, config.stringsOf("z", new String[]{"x", "y"}).length);
  }

  @Test
  public void strings() {
    try {
      ConfigObject config = new ConfigObject(Json.newJsonObject());
      config.stringsOf("x", "nope");
      Assert.fail();
    } catch (NullPointerException npe) {
    }
    try {
      ConfigObject config = new ConfigObject(Json.parseJsonObject("{\"x\":{}}"));
      config.stringsOf("x", "nope");
      Assert.fail();
    } catch (NullPointerException npe) {
    }
    {
      ConfigObject config = new ConfigObject(Json.parseJsonObject("{\"x\":[]}"));
      Assert.assertEquals(0, config.stringsOf("x", "nope").length);
    }
    {
      ConfigObject config = new ConfigObject(Json.parseJsonObject("{\"x\":[\"z\"]}"));
      String[] list = config.stringsOf("x", "nope");
      Assert.assertEquals(1, list.length);
      Assert.assertEquals("z", list[0]);
    }
    {
      ConfigObject config = new ConfigObject(Json.parseJsonObject("{\"x\":[\"z\",\"1\"]}"));
      String[] list = config.stringsOf("x", "nope");
      Assert.assertEquals(2, list.length);
      Assert.assertEquals("z", list[0]);
      Assert.assertEquals("1", list[1]);
    }
  }

  @Test
  public void integer() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    Assert.assertEquals(42, config.intOf("key", 42));
    root.put("key", 123);
    Assert.assertEquals(123, config.intOf("key", 42));
    root.putNull("key");
    Assert.assertEquals(42, config.intOf("key", 42));
    root.remove("key");
    Assert.assertEquals(42, config.intOf("key", 42));
    root.put("key", 123);
    Assert.assertEquals(123, config.intOf("key", 42));
  }

  @Test
  public void child() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    ConfigObject child1 = config.child("key");
    ConfigObject child2 = config.child("key");
    Assert.assertSame(child1.node, child2.node);
    Assert.assertEquals(42, child1.intOf("key", 42));
    child1.node.put("key", 123);
    Assert.assertEquals(123, child2.intOf("key", 42));
  }

  @Test
  public void defaultStrMustExist() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    try {
      config.strOfButCrash("key", "NOOOOO");
      Assert.fail();
    } catch (NullPointerException npe) {
      Assert.assertTrue(npe.getMessage().contains("NOOO"));
    }
    root.put("key", "yay");
    Assert.assertEquals("yay", config.strOfButCrash("key", "nope"));
  }

  @Test
  public void search() {
    ObjectNode root = Json.newJsonObject();
    ConfigObject config = new ConfigObject(root);
    try {
      config.childSearchMustExist("NOOO", "x", "y");
      Assert.fail();
    } catch (NullPointerException npe) {
      Assert.assertTrue(npe.getMessage().contains("NOOO"));
    }
    root.putObject("y").put("key", 123);
    Assert.assertEquals(123, config.childSearchMustExist("NOOO", "x", "y").intOf("key", 42));
    root.putObject("x").put("key", 111);
    Assert.assertEquals(111, config.childSearchMustExist("NOOO", "x", "y").intOf("key", 42));
  }
}
