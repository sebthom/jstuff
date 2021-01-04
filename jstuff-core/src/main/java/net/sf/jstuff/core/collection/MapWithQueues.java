/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithQueues<K, V> extends MapWithCollections<K, V, Queue<V>> {
   private static final long serialVersionUID = 1L;

   public static <K, V> MapWithQueues<K, V> create() {
      return new MapWithQueues<>();
   }

   @Override
   protected Queue<V> create(final K key) {
      return new ConcurrentLinkedQueue<>();
   }

   @Override
   protected Queue<V> createNullSafe(final K key) {
      return EmptyQueue.get();
   }
}
