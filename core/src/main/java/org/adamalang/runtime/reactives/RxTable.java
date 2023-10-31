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
import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.contracts.WhereClause;
import org.adamalang.runtime.index.ReactiveIndex;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.lists.SelectorRxObjectList;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/** a reactive table */
public class RxTable<Ty extends RxRecordBase<Ty>> extends RxBase implements Iterable<Ty>, RxParent, RxChild, RxKillable {
  public final LivingDocument document;
  public final Function<RxParent, Ty> maker;
  public final String className;
  private final LinkedHashMap<Integer, Ty> createdObjects;
  private final ReactiveIndex<Ty>[] indices;
  private final LinkedHashMap<Integer, Ty> itemsByKey;
  private final TreeSet<Ty> unknowns;

  public void debug(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("created");
    writer.writeInteger(createdObjects.size());
    writer.writeObjectFieldIntro("items");
    writer.writeInteger(itemsByKey.size());
    if (indices != null) {
      writer.writeObjectFieldIntro("idx");
      writer.writeInteger(indices.length);
    }
    if (unknowns != null) {
      writer.writeObjectFieldIntro("unknowns");
      writer.writeInteger(unknowns.size());
    }
    writer.endObject();
  }

  @SuppressWarnings("unchecked")
  public RxTable(final LivingDocument document, final RxParent owner, final String className, final Function<RxParent, Ty> maker, final int indicies) {
    super(owner);
    this.document = document;
    this.className = className;
    this.maker = maker;
    if (indicies == 0) {
      this.indices = null;
      this.unknowns = null;
    } else {
      this.indices = new ReactiveIndex[indicies];
      this.unknowns = new TreeSet<>();
      for (var k = 0; k < indicies; k++) {
        this.indices[k] = new ReactiveIndex<>(unknowns);
      }
    }
    // check if we have rows; make sure we link into the JSON tree
    this.itemsByKey = new LinkedHashMap<>();
    this.createdObjects = new LinkedHashMap<>();
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public void __kill() {
    for (Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      entry.getValue().__kill();
    }
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro(name);
      reverseDelta.beginObject();
      final var keysToKill = new ArrayList<Integer>();
      for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
        final int key = entry.getKey();
        final var row = entry.getValue();
        if (row.__isDying()) {
          if (!createdObjects.containsKey(key)) {
            forwardDelta.writeObjectFieldIntro(key);
            forwardDelta.writeNull();
            reverseDelta.writeObjectFieldIntro(key);
            row.__dump(reverseDelta);
          }
          row.__kill();
          keysToKill.add(key);
        } else if (row.__isDirty()) {
          if (createdObjects.containsKey(key)) {
            forwardDelta.writeObjectFieldIntro(key);
            row.__dump(forwardDelta);
            reverseDelta.writeObjectFieldIntro(key);
            reverseDelta.writeNull();
            JsonStreamWriter redundantWrite = new JsonStreamWriter();
            row.__commit(key + "", redundantWrite, redundantWrite);
          } else {
            row.__commit(key + "", forwardDelta, reverseDelta);
          }
        }
      }
      forwardDelta.endObject();
      reverseDelta.endObject();
      createdObjects.clear();
      // murder the keys
      for (final Integer keyToKill : keysToKill) {
        itemsByKey.remove(keyToKill);
      }
      __lowerDirtyCommit();
    }
    if (unknowns != null) {
      for (final Ty item : unknowns) {
        item.__reindex();
      }
      unknowns.clear();
    }
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
    writer.beginObject();
    for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey());
      entry.getValue().__dump(writer);
    }
    writer.endObject();
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var f2 = reader.fieldName();
        if (reader.testLackOfNull()) {
          final var key = Integer.parseInt(f2);
          final var tyPrior = itemsByKey.get(key);
          if (tyPrior == null) {
            final var tyObj = maker.apply(this);
            tyObj.__setId(key, true);
            tyObj.__insert(reader);
            itemsByKey.put(key, tyObj);
            if (unknowns != null) {
              tyObj.__reindex();
            }
            tyObj.__subscribe(this);
          } else {
            // it exists, so it is already subscribed
            tyPrior.__insert(reader);
            if (unknowns != null && !unknowns.contains(tyPrior)) {
              tyPrior.__deindex();
              unknowns.add(tyPrior);
            }
          }
        } else {
          final var key = Integer.parseInt(f2);
          final var tyObject = itemsByKey.remove(key);
          if (tyObject != null) {
            tyObject.__delete();
            tyObject.__deindex();
          }
        }
      }
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var f2 = reader.fieldName();
        final var key = Integer.parseInt(f2);
        if (reader.testLackOfNull()) {
          var tyPrior = itemsByKey.get(key);
          if (tyPrior == null) {
            tyPrior = make(key);
          }
          tyPrior.__patch(reader);
        } else {
          var tyPrior = itemsByKey.get(key);
          if (tyPrior != null) {
            tyPrior.__delete();
          }
        }
      }
    }
  }

  @Override
  public void __revert() {
    if (__isDirty()) {
      // if we are commiting
      for (final Integer killKey : createdObjects.keySet()) {
        final var item = itemsByKey.remove(killKey);
        if (item != null) {
          item.__delete();
          item.__deindex();
        }
      }
      createdObjects.clear();
      for (final Map.Entry<Integer, Ty> entry : itemsByKey.entrySet()) {
        entry.getValue().__revert();
      }
      __lowerDirtyRevert();
    }
  }

  @Override
  public long __memory() {
    long sum = super.__memory() + 64 + className.length() * 2L;
    if (indices != null) {
      for (ReactiveIndex<Ty> idx : indices) {
        sum += idx.memory();
      }
    }
    for (Ty value : itemsByKey.values()) {
      sum += value.__memory() + 20;
    }
    if (unknowns != null) {
      sum += unknowns.size() * 8L;
    }
    return sum;
  }

  public Ty make(int key) {
    final var result = maker.apply(this);
    result.__setId(key, false);
    result.__subscribe(this);
    result.__raiseDirty();
    if (unknowns != null) {
      unknowns.add(result);
    }
    this.createdObjects.put(key, result);
    this.itemsByKey.put(key, result);
    __raiseDirty();
    return result;
  }

  @Override
  public boolean __raiseInvalid() {
    __raiseDirty();
    return true;
  }

  public Ty getById(final int id) {
    final var obj = itemsByKey.get(id);
    return obj;
  }

  public ReactiveIndex<Ty> getIndex(final short column) {
    return indices[column];
  }

  @Override
  public Iterator<Ty> iterator() {
    return itemsByKey.values().iterator();
  }

  public Ty make() {
    return make(document.__genNextAutoKey());
  }

  public Iterable<Ty> scan(final WhereClause<Ty> filter) {
    if (filter == null) {
      return this;
    }
    final var prior = new AtomicReference<TreeSet<Ty>>(null);
    filter.scopeByIndicies((column, value, mode) -> {
      final var specific = indices[column].of(value, mode);
      if (specific == null) { // no index available
        prior.set(new TreeSet<>());
        return;
      }
      if (prior.get() == null) {
        // just use the index
        prior.set(specific);
      } else {
        final var common = new TreeSet<Ty>();
        if (specific != null) {
          for (final Ty item : specific) {
            if (prior.get().contains(item)) {
              common.add(item);
            }
          }
        }
        prior.set(common);
      }
    });
    if (prior.get() == null) {
      return this;
    }
    final var clone = new TreeSet<>(prior.get());
    clone.addAll(unknowns);
    return clone;
  }

  public int size() {
    if (__isDirty()) {
      return iterate(true).size();
    } else {
      return itemsByKey.size();
    }
  }

  public NtList<Ty> iterate(final boolean done) {
    return new SelectorRxObjectList<>(this);
  }

}
