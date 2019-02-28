/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithMaps<K, K2, V> extends MapWith<K, Map<K2, V>> {
   private static final long serialVersionUID = 1L;

   public static <K, K2, V> MapWithMaps<K, K2, V> create() {
      return new MapWithMaps<K, K2, V>();
   }

   public MapWithMaps() {
      super();
   }

   public MapWithMaps(final int initialCapacity) {
      super(initialCapacity);
   }

   @Override
   protected Map<K2, V> create(final K key) {
      return Maps.newHashMap();
   }

   @Override
   protected Map<K2, V> createNullSafe(final K key) {
      return Collections.emptyMap();
   }
}
