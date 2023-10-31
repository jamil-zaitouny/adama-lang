/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.function.Supplier;

/** a reactive lazy formula which is computed on demand */
public class RxLazy<Ty> extends RxBase implements RxChild {
  private final Supplier<Ty> formula;
  protected Ty cached;
  private int generation;
  private boolean invalid;

  public RxLazy(final RxParent parent, final Supplier<Ty> formula) {
    super(parent);
    this.formula = formula;
    this.cached = null;
    this.invalid = true;
    this.generation = 1;
    if (parent instanceof RxRecordBase) {
      this.generation = ((RxRecordBase) parent).__id();
    }
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    reader.skipValue();
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    reader.skipValue();
  }

  @Override
  public void __revert() {
  }

  @Override
  public boolean __raiseInvalid() {
    invalid = true;
    __invalidateSubscribers();
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public Ty get() {
    ensureCacheValid();
    return cached;
  }

  private void ensureCacheValid() {
    if (checkInvalidAndLower() || cached == null) {
      cached = formula.get();
      generation *= 65521;
      generation ++;
    }
  }

  protected boolean checkInvalidAndLower() {
    if (invalid) {
      invalid = false;
      return true;
    }
    return false;
  }

  public int getGeneration() {
    ensureCacheValid();
    return generation;
  }
}
