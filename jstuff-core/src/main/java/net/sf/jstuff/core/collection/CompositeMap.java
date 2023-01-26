/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

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
   public static <K, V> CompositeMap<K, V> of(final @NonNull Map<? extends K, ? extends V>... maps) {
      return new CompositeMap<>(maps);
   }

   public CompositeMap() {
   }

   public CompositeMap(final Collection<? extends Map<? extends K, ? extends V>> maps) {
      super(maps);
   }

   @SafeVarargs
   public CompositeMap(final @NonNull Map<? extends K, ? extends V>... maps) {
      super(maps);
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsKey(final @Nullable Object key) {
      for (final Map<? extends K, ? extends V> m : components)
         if (m.containsKey(key))
            return true;
      return false;
   }

   @Override
   public boolean containsValue(final @Nullable Object value) {
      for (final Map<? extends K, ? extends V> m : components)
         if (m.containsValue(value))
            return true;
      return false;
   }

   @Override
   public CompositeSet<Entry<K, V>> entrySet() {
      final var entries = new CompositeSet<Entry<K, V>>();
      for (final Map<? extends K, ? extends V> m : components) {
         final @NonNull Collection<? extends Entry<K, V>> set = Types.cast(m.entrySet());
         entries.getComponents().add(set);
      }
      return entries;
   }

   @Override
   @Nullable
   public V get(final @Nullable Object key) {
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
      final var keys = new CompositeSet<K>();
      for (final Map<? extends K, ? extends V> m : components) {
         keys.getComponents().add(m.keySet());
      }
      return keys;
   }

   @Nullable
   @Override
   public V put(final K key, final V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> map) {
      throw new UnsupportedOperationException();
   }

   @Nullable
   @Override
   public V remove(final @Nullable Object key) {
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
                  return asNonNullUnsafe(CompositeMap.this.get(key));
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
