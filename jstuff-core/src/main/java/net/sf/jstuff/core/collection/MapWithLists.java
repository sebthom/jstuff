/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
   public List<V> createNullSafe(final Object key) {
      return Collections.emptyList();
   }
}
