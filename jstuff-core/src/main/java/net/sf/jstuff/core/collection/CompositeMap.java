/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeMap<K, V> extends Composite.Default<Map<? extends K, ? extends V>> implements Map<K, V> {

   private static final long serialVersionUID = 1L;

   public static <K, V> CompositeMap<K, V> of(final Collection<? extends Map<? extends K, ? extends V>> maps) {
      return new CompositeMap<>(maps);
   }

   @SafeVarargs
   public static <K, V> CompositeMap<K, V> of(final Map<? extends K, ? extends V>... maps) {
      return new CompositeMap<>(maps);
   }

   public CompositeMap() {
   }

   public CompositeMap(final Collection<? extends Map<? extends K, ? extends V>> maps) {
      super(maps);
   }

   @SafeVarargs
   public CompositeMap(final Map<? extends K, ? extends V>... maps) {
      super(maps);
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(final Object key) {
      for (final Map<? extends K, ? extends V> m : components)
         if (m.containsKey(key))
            return true;
      return false;
   }

   @Override
   public boolean containsValue(final Object value) {
      for (final Map<? extends K, ? extends V> m : components)
         if (m.containsValue(value))
            return true;
      return false;
   }

   @Override
   public CompositeSet<Entry<K, V>> entrySet() {
      final CompositeSet<Entry<K, V>> entries = new CompositeSet<>();
      for (final Map<? extends K, ? extends V> m : components) {
         final Collection<? extends Entry<K, V>> set = Types.cast(m.entrySet());
         entries.addComponent(set);
      }
      return entries;
   }

   @Override
   public V get(final Object key) {
      for (final Map<? extends K, ? extends V> m : components)
         if (m.containsKey(key))
            return m.get(key);
      return null;
   }

   @Override
   public boolean isEmpty() {
      for (final Map<? extends K, ? extends V> m : components)
         if (!m.isEmpty())
            return false;
      return true;
   }

   @Override
   public Set<K> keySet() {
      final CompositeSet<K> keys = new CompositeSet<>();
      for (final Map<? extends K, ? extends V> m : components) {
         keys.addComponent(m.keySet());
      }
      return keys;
   }

   @Override
   public V put(final Object key, final Object value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> map) {
      throw new UnsupportedOperationException();
   }

   @Override
   public V remove(final Object key) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int size() {
      return keySet().size();
   }

   @Override
   public Collection<V> values() {
      return new AbstractList<>() {
         @Override
         public V get(final int index) {
            int i = 0;
            for (final Iterator<K> it = keySet().iterator(); it.hasNext(); i++) {
               final K key = it.next();
               if (i == index)
                  return CompositeMap.this.get(key);
            }
            throw new IndexOutOfBoundsException("Index: " + index);
         }

         @Override
         public int size() {
            return CompositeMap.this.size();
         }
      };
   }
}
