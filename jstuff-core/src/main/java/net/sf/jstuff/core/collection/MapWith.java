/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MapWith<K, V> implements Map<K, V>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Map<K, V> map;

   protected MapWith() {
      map = createBackingMap(16);
   }

   protected MapWith(final int initialCapacity) {
      map = createBackingMap(initialCapacity);
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean containsKey(final @Nullable Object key) {
      return map.containsKey(key);
   }

   @Override
   public boolean containsValue(final @Nullable Object value) {
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
   public Set<Map.Entry<K, V>> entrySet() {
      return map.entrySet();
   }

   @Override
   public @Nullable V get(final @Nullable Object key) {
      return map.get(key);
   }

   public final V getNullSafe(final K key) {
      final var v = get(key);
      if (v != null)
         return v;
      return createNullSafe(key);
   }

   public final V getOrCreate(final K key) {
      final var v = get(key);
      if (v != null)
         return v;

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
   public @Nullable V put(final K key, final V value) {
      return map.put(key, value);
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> otherMap) {
      map.putAll(otherMap);
   }

   @Override
   public @Nullable V putIfAbsent(final K key, final V value) {
      if (!containsKey(key))
         return map.put(key, value);
      return map.get(key);
   }

   @Override
   public @Nullable V remove(final @Nullable Object key) {
      return map.remove(key);
   }

   @Override
   public boolean remove(final @Nullable Object key, final @Nullable Object value) {
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
