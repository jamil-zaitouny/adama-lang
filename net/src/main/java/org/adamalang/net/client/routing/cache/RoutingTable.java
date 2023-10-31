/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.cache;

import java.util.*;

/** the routing table which maps keys to targets for use by clients */
public class RoutingTable {
  /** raw targets held onto for differentiation */
  private final HashMap<String, TreeSet<String>> history;
  /** the routing table of spaces to their tables */
  private final HashMap<String, SpaceState> routing;

  private final Random rng;

  public RoutingTable() {
    this.history = new HashMap<>();
    this.routing = new HashMap<>();
    this.rng = new Random();
  }

  public TreeSet<String> targetsFor(String space) {
    return getOrCreateSpaceState(space).list();
  }

  private SpaceState getOrCreateSpaceState(String space) {
    SpaceState state = routing.get(space);
    if (state == null) {
      state = new SpaceState();
      routing.put(space, state);
    }
    return state;
  }

  public String get(String space, String key) {
    return getOrCreateSpaceState(space).pick(key);
  }

  public String pick(String key) {
    return SpaceState.pick(history.keySet(), key);
  }

  public String random() {
    ArrayList<String> targets = new ArrayList<>(history.keySet());
    if (targets.size() > 0) {
      return targets.get(rng.nextInt(targets.size()));
    }
    return null;
  }

  /** a target has reported their inventory */
  public void integrate(String target, Collection<String> spaces) {
    // there are two ways for the routing table to update
    TreeSet<String> prior = history.get(target);
    if (prior == null) {
      // the state doesn't exist, so we will simply integrate the changes
      history.put(target, new TreeSet<>(spaces));
      for (String space : spaces) {
        getOrCreateSpaceState(space).add(target);
      }
    } else {
      for (String space : spaces) {
        if (!prior.remove(space)) {
          getOrCreateSpaceState(space).add(target);
        }
      }
      for (String space : prior) {
        getOrCreateSpaceState(space).subtract(target);
      }
      history.put(target, new TreeSet<>(spaces));
    }
  }

  /** a target is no longer present */
  public void remove(String target) {
    TreeSet<String> spaces = history.remove(target);
    if (spaces != null) {
      for (String space : spaces) {
        if (getOrCreateSpaceState(space).subtract(target)) {
          routing.remove(space);
        }
      }
    }
  }
}
