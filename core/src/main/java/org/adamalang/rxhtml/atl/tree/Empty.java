/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.rxhtml.atl.Context;

import java.util.Collections;
import java.util.Map;

public class Empty implements Tree {

  @Override
  public Map<String, String> variables() {
    return Collections.emptyMap();
  }

  @Override
  public String debug() {
    return "EMPTY";
  }

  @Override
  public String js(Context context, String env) {
    return "\"\"";
  }
}
