/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.model;

public class Validator {
  public final String service;
  public final String shortServiceName;
  public final String[] args;

  public Validator(String service, String[] args) {
    this.service = service;
    this.args = args;
    int lastDotService = service.lastIndexOf('.');
    shortServiceName = service.substring(lastDotService + 1);
  }
}
