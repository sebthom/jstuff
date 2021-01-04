/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithMaps<K, K2, V> extends MapWith<K, Map<K2, V>> {
   private static final long serialVersionUID = 1L;

   public static <K, K2, V> MapWithMaps<K, K2, V> create() {
      return new MapWithMaps<>();
   }

   public MapWithMaps() {
   }

   public MapWithMaps(final int initialCapacity) {
      super(initialCapacity);
   }

   @Override
   protected Map<K2, V> create(final K key) {
      return new HashMap<>();
   }

   @Override
   protected Map<K2, V> createNullSafe(final K key) {
      return Collections.emptyMap();
   }
}
