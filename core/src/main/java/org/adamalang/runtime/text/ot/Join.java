/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.text.ot;

import java.util.ArrayList;

/** many operands combined into one via concatenation */
public class Join implements Operand {
  public final ArrayList<Operand> children;

  public Join() {
    this.children = new ArrayList<>();
  }

  @Override
  public void transposeRangeIntoJoin(int at, int length, Join join) {
    int curAt = 0;
    for (Operand child : children) {
      int curLen = child.length();
      int iA = Math.max(curAt, at);
      int iB = Math.min(curAt + curLen, at + length);
      if (iA < iB) {
        child.transposeRangeIntoJoin(iA - curAt, iB - iA, join);
      }
      curAt += curLen;
    }
  }

  @Override
  public String get() {
    StringBuilder sb = new StringBuilder();
    for (Operand op : children) {
      sb.append(op.get());
    }
    return sb.toString();
  }

  @Override
  public int length() {
    int _length = 0;
    for (Operand op : children) {
      _length += op.length();
    }
    return _length;
  }
}
