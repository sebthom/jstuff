/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface MapExt<K, V> extends Map<K, V> {

   default V getOrThrow(final K key) throws NoSuchElementException {
      return Maps.getOrThrow(this, key);
   }

   default boolean isNotEmpty() {
      return !isEmpty();
   }

   default List<K> keyList() {
      return Maps.keysAsArrayList(this);
   }

   default Map<K, V> putAllIfAbsent(final Map<? extends K, ? extends V> entriesToAdd) {
      return Maps.putAllIfAbsent(this, entriesToAdd);
   }
}
