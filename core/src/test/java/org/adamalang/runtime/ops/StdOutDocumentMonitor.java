/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.ops;

import org.adamalang.runtime.contracts.DocumentMonitor;

import java.util.ArrayList;
import java.util.HashMap;

/** a monitor which dumps fun information out to stdout */
public class StdOutDocumentMonitor implements DocumentMonitor {
  public HashMap<String, TableRegister> stats;

  public StdOutDocumentMonitor() {
    stats = new HashMap<>();
  }

  @Override
  public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
  }

  @Override
  public void goodwillFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
  }

  @Override
  public void pop(final long time, final boolean exception) {
    System.out.println("POP:" + exception);
  }

  @Override
  public void push(final String label) {
    System.out.println("PUSH:" + label);
  }

  public void dump() {
    final var items = new ArrayList<>(stats.values());
    items.sort((a, b) -> {
      final var s0 = Integer.compare(a.total, b.total);
      if (s0 == 0) {
        return Integer.compare(a.effectiveness, b.effectiveness);
      }
      return s0;
    });
    System.out.println("table,column,calls,total,effectiveness,%");
    for (final TableRegister tr : items) {
      System.out.println(tr.tableName + "," + tr.colummName + "," + tr.calls + "," + tr.total + "," + tr.effectiveness + "," + tr.effectiveness / (double) tr.total);
    }
  }

  public static class TableRegister {
    public final String colummName;
    public final String tableName;
    public int calls;
    public int effectiveness;
    public int total;

    public TableRegister(final String tableName, final String colummName) {
      this.tableName = tableName;
      this.colummName = colummName;
      total = 0;
      effectiveness = 0;
      calls = 0;
    }
  }
}
