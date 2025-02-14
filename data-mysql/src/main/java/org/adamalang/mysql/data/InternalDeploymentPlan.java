/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.data;

/** the raw data stored within the spaces table for a deployment */
public class InternalDeploymentPlan {
  public final String plan;
  public final String hash;

  public InternalDeploymentPlan(String plan, String hash) {
    this.plan = plan;
    this.hash = hash;
  }
}
