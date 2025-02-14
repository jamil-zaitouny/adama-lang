/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.text.search;

import java.util.PrimitiveIterator;
import java.util.TreeMap;
import java.util.TreeSet;

/** this is a simple trie for mapping code points that are compressed via a hash into keys */
public class CompressedTrieIndex {
  private final TreeMap<Integer, CompressedTrieIndex> children;
  private TreeSet<Integer> keys;

  public CompressedTrieIndex() {
    this.children = new TreeMap<>();
    this.keys = null;
  }

  private CompressedTrieIndex next(int k) {
    CompressedTrieIndex val = children.get(k);
    if (val == null) {
      val = new CompressedTrieIndex();
      children.put(k, val);
    }
    return val;
  }

  private CompressedTrieIndex of(String word) {
    CompressedTrieIndex root = this;
    PrimitiveIterator.OfInt it = word.codePoints().iterator();
    while (it.hasNext()) {
      int val = it.nextInt();
      if (it.hasNext()) {
        val += 32831 * it.nextInt();
      }
      if (it.hasNext()) {
        val += 52347 * it.nextInt();
      }
      root = next(val);
    }
    return root;
  }

  public void map(String word, int key) {
    CompressedTrieIndex index = of(word);
    if (index.keys == null) {
      index.keys = new TreeSet<>();
    }
    index.keys.add(key);
  }

  public void unmap(String word, int key) {
    CompressedTrieIndex index = of(word);
    if (index.keys != null) {
      index.keys.remove(key);
      if (index.keys.size() == 0) {
        index.keys = null;
        // TODO: need to clean up the parents
      }
    }
  }

  public TreeSet<Integer> keysOf(String word) {
    CompressedTrieIndex index = of(word);
    return of(word).keys;
  }
}
