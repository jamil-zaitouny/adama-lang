/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class DataBaseConfigTests {

  @Test
  public void missing_role() {
    try {
      new DataBaseConfig("{}", "x");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("role"));
    }
  }

  @Test
  public void missing_jdbc() {
    try {
      new DataBaseConfig("{\"x\":{}}", "x");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("jdbc_url"));
    }
  }

  @Test
  public void missing_user() {
    try {
      new DataBaseConfig("{\"x\":{\"jdbc_url\":\"1\"}}", "x");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("user"));
    }
  }

  @Test
  public void missing_password() {
    try {
      new DataBaseConfig("{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\"}}", "x");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("password"));
    }
  }

  @Test
  public void missing_database_name() {
    try {
      new DataBaseConfig("{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\",\"password\":\"3\"}}", "x");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof NullPointerException);
      Assert.assertTrue(ex.getMessage().contains("database_name"));
    }
  }

  @Test
  public void ok() throws Exception {
    DataBaseConfig c =
        new DataBaseConfig(
            "{\"x\":{\"jdbc_url\":\"1\",\"user\":\"2\",\"password\":\"3\",\"database_name\":\"4\"}}",
            "x");
    Assert.assertEquals("1", c.jdbcUrl);
    Assert.assertEquals("2", c.user);
    Assert.assertEquals("3", c.password);
    Assert.assertEquals("4", c.databaseName);
  }

  @Test
  public void localIntegration() throws Exception {
    DataBaseConfig dataBaseConfig = getLocalIntegrationConfig();
    dataBaseConfig.createComboPooledDataSource().close();
  }

  public static DataBaseConfig getLocalIntegrationConfig() throws Exception {
    return new DataBaseConfig(Files.readString(new File("test.mysql.json").toPath()), "any");
  }
}
