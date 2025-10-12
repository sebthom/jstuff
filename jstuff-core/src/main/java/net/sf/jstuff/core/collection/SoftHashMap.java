/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> {
   private final class SoftEntry extends SoftReference<V> {
      private final K key;

      SoftEntry(final K key, final V value) {
         super(value, staleEntries);
         this.key = key;
      }
   }

   private final Map<K, SoftEntry> map = new HashMap<>();
   private final ReferenceQueue<? super V> staleEntries = new ReferenceQueue<>();

   @Override
   public void clear() {
      purgeStaleEntries();
      map.clear();
   }

   @Override
   public Set<Map.Entry<K, V>> entrySet() {
      if (map.isEmpty())
         return Collections.<K, V>emptyMap().entrySet(); // CHECKSTYLE:IGNORE .*

      purgeStaleEntries();
      final var snapshot = new HashMap<K, V>();
      for (final SoftEntry entry : map.values()) {
         final V value = entry.get();
         if (value != null) {
            snapshot.put(entry.key, value);
         }
      }
      return snapshot.entrySet();
   }

   @Override
   public @Nullable V get(final @Nullable Object key) {
      purgeStaleEntries();
      final SoftEntry e = map.get(key);
      if (e == null)
         return null;
      final V value = e.get();
      if (value == null) {
         map.remove(e.key);
         return null;
      }
      return value;
   }

   @SuppressWarnings("unchecked")
   private void purgeStaleEntries() {
      SoftEntry e = null;
      while ((e = (SoftEntry) staleEntries.poll()) != null) {
         map.remove(e.key);
      }
   }

   @Override
   public @Nullable V put(final K k, final V v) {
      purgeStaleEntries();
      final SoftEntry e = map.put(k, new SoftEntry(k, v));
      return e == null ? null : e.get();
   }

   @Override
   public @Nullable V remove(final @Nullable Object k) {
      purgeStaleEntries();
      final SoftEntry e = map.remove(k);
      return e == null ? null : e.get();
   }

   @Override
   public int size() {
      purgeStaleEntries();
      return map.size();
   }
}
