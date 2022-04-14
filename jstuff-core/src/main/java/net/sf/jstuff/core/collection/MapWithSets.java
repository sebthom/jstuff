/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithSets<K, V> extends MapWithCollections<K, V, Set<V>> {
   private static final long serialVersionUID = 1L;

   public static <K, V> MapWithSets<K, V> create() {
      return new MapWithSets<>();
   }

   public static <K, V> MapWithSets<K, V> create(final int initialCapacity, final int initialSetCapacity) {
      return new MapWithSets<>(initialCapacity, initialSetCapacity);
   }

   public MapWithSets() {
   }

   public MapWithSets(final int initialCapacity) {
      super(initialCapacity);
   }

   public MapWithSets(final int initialCapacity, final int initialCapacityOfSet) {
      super(initialCapacity, initialCapacityOfSet);
   }

   @Override
   protected Set<V> create(final K key) {
      return new HashSet<>(initialCapacityOfCollection, growthFactorOfCollection);
   }

   @Override
   protected Set<V> createNullSafe(final K key) {
      return Collections.emptySet();
   }
}
