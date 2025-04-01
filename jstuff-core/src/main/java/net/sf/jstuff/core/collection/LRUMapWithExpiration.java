/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.time.Duration;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class LRUMapWithExpiration<K, V> implements Map<K, V> {

   private static final class CacheEntry<V> {
      final V value;
      final long expiresAt;

      CacheEntry(final V value, final long maxEntryAgeMS) {
         this.value = value;
         expiresAt = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(maxEntryAgeMS);
      }

      boolean isExpired() {
         return System.nanoTime() > expiresAt;
      }
   }

   private final long maxEntryAgeMS;
   private final LRUMap<K, CacheEntry<V>> entriesOrderedByAccess;

   public LRUMapWithExpiration(final int capacity, final Duration maxEntryAge) {
      entriesOrderedByAccess = new LRUMap<>(capacity);
      maxEntryAgeMS = maxEntryAge.toMillis();
   }

   @Override
   public void clear() {
      entriesOrderedByAccess.clear();
   }

   @Override
   public boolean containsKey(@Nullable final Object key) {
      return entriesOrderedByAccess.containsKey(key);
   }

   @Override
   public boolean containsValue(@Nullable final Object value) {
      return entriesOrderedByAccess.containsValue(value);
   }

   /**
    * Removes expired entries
    */
   public int compact() {
      if (entriesOrderedByAccess.isEmpty())
         return 0;

      int count = 0;
      for (final var it = entriesOrderedByAccess.values().iterator(); it.hasNext();) {
         if (it.next().isExpired()) {
            it.remove();
         }
         count++;
      }
      return count;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new AbstractSet<>() {
         @Override
         public void clear() {
            LRUMapWithExpiration.this.clear();
         }

         @Override
         public Iterator<Entry<K, V>> iterator() {
            return new Iterator<>() {
               private final Iterator<Map.Entry<K, CacheEntry<V>>> iterator = entriesOrderedByAccess.entrySet().iterator();
               private Map.Entry<K, CacheEntry<V>> nextEntry = lateNonNull();

               @Override
               public boolean hasNext() {
                  while (iterator.hasNext()) {
                     nextEntry = iterator.next();
                     if (!nextEntry.getValue().isExpired())
                        return true;
                     iterator.remove(); // remove expired entry
                  }
                  return false;
               }

               @Override
               public Entry<K, V> next() {
                  return new Entry<>() {
                     private final K key = nextEntry.getKey();
                     private final V value = nextEntry.getValue().value;

                     @Override
                     public K getKey() {
                        return key;
                     }

                     @Override
                     public V getValue() {
                        return value;
                     }

                     @Override
                     public V setValue(final V value) {
                        entriesOrderedByAccess.put(key, new CacheEntry<>(value, maxEntryAgeMS));
                        return this.value;
                     }
                  };
               }
            };
         }

         @Override
         public int size() {
            return LRUMapWithExpiration.this.size();
         }
      };
   }

   /**
    * @return null if the entry does not exist or the entry has a null value
    */
   @Override
   public @Nullable V get(final @Nullable Object key) {
      final var entry = entriesOrderedByAccess.get(key);
      if (entry == null)
         return null;

      // check if entry is expired
      if (entry.isExpired()) {
         entriesOrderedByAccess.remove(key);
         return null;
      }

      return entry.value;
   }

   @Override
   public boolean isEmpty() {
      return entriesOrderedByAccess.isEmpty();
   }

   @Override
   public Set<K> keySet() {
      return entriesOrderedByAccess.keySet();
   }

   @Override
   public @Nullable V put(final K key, final V value) {
      final var entry = entriesOrderedByAccess.put(key, new CacheEntry<>(value, maxEntryAgeMS));
      return entry == null || entry.isExpired() ? null : entry.value;
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
   }

   @Override
   public @Nullable V putIfAbsent(final K key, final V value) {
      final var entry = entriesOrderedByAccess.putIfAbsent(key, new CacheEntry<>(value, maxEntryAgeMS));
      return entry == null || entry.isExpired() ? null : entry.value;
   }

   @Override
   public @Nullable V remove(final @Nullable Object key) {
      final var entry = entriesOrderedByAccess.remove(key);
      return entry == null || entry.isExpired() ? null : entry.value;
   }

   @Override
   public int size() {
      return entriesOrderedByAccess.size();
   }

   @Override
   public Collection<V> values() {
      return new AbstractCollection<>() {
         @Override
         public void clear() {
            LRUMapWithExpiration.this.clear();
         }

         @Override
         public boolean isEmpty() {
            return LRUMapWithExpiration.this.isEmpty();
         }

         @Override
         public Iterator<V> iterator() {
            return new Iterator<>() {
               private final Iterator<Map.Entry<K, CacheEntry<V>>> iterator = entriesOrderedByAccess.entrySet().iterator();
               private Map.Entry<K, CacheEntry<V>> nextEntry = lateNonNull();

               @Override
               public boolean hasNext() {
                  while (iterator.hasNext()) {
                     nextEntry = iterator.next();
                     if (!nextEntry.getValue().isExpired())
                        return true;
                     iterator.remove(); // remove expired entry
                  }
                  return false;
               }

               @Override
               public V next() {
                  return nextEntry.getValue().value;
               }
            };
         }

         @Override
         public int size() {
            return LRUMapWithExpiration.this.size();
         }
      };
   }
}
