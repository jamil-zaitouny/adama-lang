/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.regex.Pattern;

/** test if a host an IP address */
public class IsIP {
  public static boolean test(String host) {
    return Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+").matcher(host).matches();
  }
}
