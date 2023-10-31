/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class DeploymentPlanTests {

  @Test
  public void badHash() {
    try {
      DeploymentPlan.hash(null, null);
      Assert.fail();
    } catch (RuntimeException ex) {

    }
  }

  @Test
  public void includes_1() throws Exception {
    new DeploymentPlan(
        "{\"versions\":{\"x\":{\"main\":\"@include x;\",\"includes\":{\"x\":\"\"}}},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});
  }

  @Test
  public void includes_skip() throws Exception {
    new DeploymentPlan(
        "{\"versions\":{\"x\":{\"main\":\"\",\"includes\":123}},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});
  }

  @Test
  public void includes_2() throws Exception {
    new DeploymentPlan(
        "{\"versions\":{\"x\":{\"main\":\"@include x;\",\"includes\":{\"x\":\"@include y;\",\"y\":\"\"}}},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});
  }

  @Test
  public void happy() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});
    Assert.assertEquals("x", plan.pickVersion("1"));
    Assert.assertEquals("x", plan.pickVersion("2"));
    Assert.assertEquals("y", plan.pickVersion("100"));
    Assert.assertEquals("y", plan.pickVersion("101"));
    Assert.assertEquals("z", plan.pickVersion("102"));
    Assert.assertEquals("z", plan.pickVersion("103"));
    Assert.assertEquals("z", plan.pickVersion("104"));
    Assert.assertEquals("z", plan.pickVersion("105"));
    Assert.assertEquals("y", plan.pickVersion("106"));
  }

  @Test
  public void happy2() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":900,\"prefix\":\"y\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});
    Assert.assertEquals("x", plan.pickVersion("1"));
    Assert.assertEquals("x", plan.pickVersion("2"));
    Assert.assertEquals("y", plan.pickVersion("y3"));
  }

  public void parseBadTest(String json, int expectedError) {
    try {
      new DeploymentPlan(json, (t, errorCode) -> {});
      Assert.fail();
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(expectedError, ex.code);
    }
  }

  @Test
  public void stage_no_version() {
    parseBadTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{}]}", 199768);
  }

  @Test
  public void stage_version_doest_exist() {
    parseBadTest(
        "{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"y\"}]}", 120895);
  }

  @Test
  public void stage_extra_field() {
    parseBadTest(
        "{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"z\":true}]}",
        116812);
  }

  @Test
  public void stage_bad_field_keys() {
    parseBadTest(
        "{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"keys\":true}]}",
        199886);
  }

  @Test
  public void stage_bad_element() {
    parseBadTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":[true]}", 176703);
  }

  @Test
  public void plan_bad_type() {
    parseBadTest("{\"versions\":{\"x\":\"\"},\"default\":\"x\",\"plan\":\"z\"}", 126012);
  }

  @Test
  public void no_default() {
    parseBadTest("{\"versions\":{\"x\":\"\"}}", 143948);
  }

  @Test
  public void default_invalid() {
    parseBadTest("{\"versions\":{\"x\":\"\"},\"default\":\"y\"}", 145980);
  }

  @Test
  public void no_versions() {
    parseBadTest("{}", 115788);
  }

  @Test
  public void empty_versions() {
    parseBadTest("{\"versions\":{}}", 115788);
  }

  @Test
  public void bad_versions_type() {
    parseBadTest("{\"versions\":true}", 155711);
  }

  @Test
  public void invalid_field() {
    parseBadTest("{\"x\":{}}", 143430);
  }

  @Test
  public void must_be_obj() {
    parseBadTest("[]", 117818);
  }

  @Test
  public void must_be_json() {
    parseBadTest("x", 146561);
  }
}
