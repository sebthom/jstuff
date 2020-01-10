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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MapWith<K, V> implements Map<K, V>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Map<K, V> map;

   public MapWith() {
      map = createBackingMap(16);
   }

   public MapWith(final int initialCapacity) {
      map = createBackingMap(initialCapacity);
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean containsKey(final Object key) {
      return map.containsKey(key);
   }

   @Override
   public boolean containsValue(final Object value) {
      return map.containsValue(value);
   }

   protected abstract V create(K key);

   protected Map<K, V> createBackingMap(final int initialCapacity) {
      return Maps.newHashMap(initialCapacity);
   }

   protected V createNullSafe(final K key) {
      return create(key);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      return map.entrySet();
   }

   @Override
   public V get(final Object key) {
      return map.get(key);
   }

   public final V getNullSafe(final K key) {
      if (containsKey(key))
         return get(key);
      return createNullSafe(key);
   }

   public final V getOrCreate(final K key) {
      if (containsKey(key))
         return get(key);
      final V value = create(key);
      put(key, value);
      return value;
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return map.keySet();
   }

   @Override
   public V put(final K key, final V value) {
      return map.put(key, value);
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> otherMap) {
      map.putAll(otherMap);
   }

   /**
    * @since 1.8
    */
   @Override
   public V putIfAbsent(final K key, final V value) {
      if (!containsKey(key))
         return map.put(key, value);
      return map.get(key);
   }

   @Override
   public V remove(final Object key) {
      return map.remove(key);
   }

   /**
    * Removes the entry for the specified key only if it is currently mapped to the specified value.
    *
    * @since 1.8
    */
   @Override
   public boolean remove(final Object key, final Object value) {
      if (!containsKey(key))
         return false;

      if (Objects.equals(get(key), value)) {
         remove(key);
         return true;
      }
      return false;
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Collection<V> values() {
      return map.values();
   }
}
