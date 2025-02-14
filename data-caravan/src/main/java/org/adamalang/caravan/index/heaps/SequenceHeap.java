/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;
import org.adamalang.caravan.index.Report;

import java.util.Arrays;

/** A sequence heap allows multiple heaps to come together into one; this does the translation of the returned region to the children heaps and vice-versa. */
public class SequenceHeap implements Heap {
  private final Heap[] heaps;
  private final long max;

  public SequenceHeap(Heap... heaps) {
    this.heaps = heaps;
    {
      long _max = 0;
      for (Heap heap : heaps) {
        _max += heap.max();
      }
      this.max = _max;
    }
  }

  @Override
  public void report(Report report) {
    for (Heap heap : heaps) {
      heap.report(report);
    }
  }

  @Override
  public long available() {
    long ret = 0;
    for (Heap heap : heaps) {
      ret += heap.available();
    }
    return ret;
  }

  @Override
  public long max() {
    return max;
  }

  @Override
  public Region ask(int size) {
    long newOffset = 0L;
    for (Heap heap : heaps) {
      Region got = heap.ask(size);
      if (got != null) {
        return new Region(newOffset + got.position, got.size);
      }
      newOffset += heap.max();
    }
    return null;
  }

  @Override
  public void free(Region region) {
    long withinOffset = region.position;
    for (Heap heap : heaps) {
      if (withinOffset < heap.max()) {
        heap.free(new Region(withinOffset, region.size));
        return;
      } else {
        withinOffset -= heap.max();
      }
    }
  }

  @Override
  public void snapshot(ByteBuf buf) {
    for (Heap heap : heaps) {
      heap.snapshot(buf);
    }
  }

  @Override
  public void load(ByteBuf buf) {
    for (Heap heap : heaps) {
      heap.load(buf);
    }
  }

  @Override
  public String toString() {
    return "Seq{" + Arrays.toString(heaps) + "}";
  }
}
