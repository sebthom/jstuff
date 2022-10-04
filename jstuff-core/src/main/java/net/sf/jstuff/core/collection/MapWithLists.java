/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithLists<K, V> extends MapWithCollections<K, V, List<V>> {
   private static final long serialVersionUID = 1L;

   public static <K, V> MapWithLists<K, V> create() {
      return new MapWithLists<>();
   }

   public static <K, V> MapWithLists<K, V> create(final int initialCapacity) {
      return new MapWithLists<>(initialCapacity);
   }

   public MapWithLists() {
   }

   public MapWithLists(final int initialCapacity) {
      super(initialCapacity);
   }

   public MapWithLists(final int initialCapacity, final int initialCapacityOfList) {
      super(initialCapacity, initialCapacityOfList);
   }

   @Override
   protected List<V> create(final K key) {
      return new ArrayList<>(initialCapacityOfCollection);
   }

   @Override
   public List<V> createNullSafe(final @Nullable Object key) {
      return Collections.emptyList();
   }
}
