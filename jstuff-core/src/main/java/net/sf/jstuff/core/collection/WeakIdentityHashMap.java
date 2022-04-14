/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashMap<K, V> implements Map<K, V> {
   private interface KeyWrapper<K> {
      K get();
   }

   private static final class LookupKeyWrapper implements KeyWrapper<Object> {
      private final int identityHashCode;
      private final Object key;

      LookupKeyWrapper(final Object key) {
         this.key = key;
         identityHashCode = System.identityHashCode(key);
      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         final KeyWrapper<?> ref = (KeyWrapper<?>) obj;
         return get() == ref.get();
      }

      @Override
      public Object get() {
         return key;
      }

      @Override
      public int hashCode() {
         return identityHashCode;
      }
   }

   /**
    * Wrapper used as key in the hash map that overrides equals and hashCode to ensure key comparisons by the underlying WeakHashMap is made based on the
    * object's identity.
    */
   private static final class WeakKeyWrapper<K> extends WeakReference<K> implements KeyWrapper<K> {
      private final int identityHashCode;

      private WeakKeyWrapper(final K key, final ReferenceQueue<K> queue) {
         super(key, queue);
         identityHashCode = System.identityHashCode(key);
      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         final KeyWrapper<?> ref = (KeyWrapper<?>) obj;
         return get() == ref.get();
      }

      @Override
      public int hashCode() {
         return identityHashCode;
      }
   }

   private static final KeyWrapper<Object> NULL_KEY_WRAPPER = new KeyWrapper<>() {
      private final int identityHashCode = System.identityHashCode(null);

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         final KeyWrapper<?> ref = (KeyWrapper<?>) obj;
         return get() == ref.get();
      }

      @Override
      public Object get() {
         return null;
      }

      @Override
      public int hashCode() {
         return identityHashCode;
      }
   };

   public static <K, V> WeakIdentityHashMap<K, V> create() {
      return new WeakIdentityHashMap<>();
   }

   public static <K, V> WeakIdentityHashMap<K, V> create(final int initialCapacity) {
      return new WeakIdentityHashMap<>(initialCapacity);
   }

   public static <K, V> WeakIdentityHashMap<K, V> create(final int initialCapacity, final float growthFactor) {
      return new WeakIdentityHashMap<>(initialCapacity, growthFactor);
   }

   private final ReferenceQueue<K> garbageCollectedRefs = new ReferenceQueue<>();
   private final Map<KeyWrapper<K>, V> map;

   public WeakIdentityHashMap() {
      map = new HashMap<>();
   }

   /**
    * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
    */
   public WeakIdentityHashMap(final int initialCapacity) {
      map = new HashMap<>(initialCapacity);
   }

   /**
    * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
    * @param growthFactor The growth factor of the backing {@link WeakHashMap}
    */
   public WeakIdentityHashMap(final int initialCapacity, final float growthFactor) {
      map = new HashMap<>(initialCapacity, growthFactor);
   }

   @Override
   public void clear() {
      map.clear();
      expungeStaleEntries();
   }

   @Override
   public boolean containsKey(final Object key) {
      expungeStaleEntries();
      return map.containsKey(new LookupKeyWrapper(key));
   }

   @Override
   public boolean containsValue(final Object value) {
      expungeStaleEntries();
      return map.containsValue(value);
   }

   /**
    * <b>Important:</b> The returned set is unmodifiable and does not reflect later changes to the map.
    */
   @Override
   public Set<Map.Entry<K, V>> entrySet() {
      expungeStaleEntries();
      final Set<Map.Entry<K, V>> entrySet = new HashSet<>();
      for (final Map.Entry<KeyWrapper<K>, V> ref : map.entrySet()) {
         final K key = ref.getKey().get();
         final V value = ref.getValue();
         final Map.Entry<K, V> entry = new Map.Entry<>() {
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
               throw new UnsupportedOperationException();
            }
         };
         entrySet.add(entry);
      }
      return Collections.unmodifiableSet(entrySet);
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj == this)
         return true;
      if (!(obj instanceof Map))
         return false;
      final Map<?, ?> otherMap = (Map<?, ?>) obj;
      if (otherMap.size() != size())
         return false;
      for (final Entry<K, V> entry : entrySet()) {
         final K key = entry.getKey();
         if (!otherMap.containsKey(key) || !Objects.equals(entry.getValue(), otherMap.get(key)))
            return false;
      }
      return true;
   }

   @SuppressWarnings("unchecked")
   private void expungeStaleEntries() {
      WeakKeyWrapper<K> weakKey;
      while ((weakKey = (WeakKeyWrapper<K>) garbageCollectedRefs.poll()) != null) {
         map.remove(weakKey);
      }
   }

   @Override
   public V get(final Object key) {
      expungeStaleEntries();
      return map.get(new LookupKeyWrapper(key));
   }

   @Override
   public int hashCode() {
      expungeStaleEntries();
      return map.hashCode();
   }

   @Override
   public boolean isEmpty() {
      expungeStaleEntries();
      return map.isEmpty();
   }

   /**
    * <b>Important:</b> The returned set is unmodifiable and does not reflect later changes to the map.
    */
   @Override
   public Set<K> keySet() {
      expungeStaleEntries();
      final Set<K> keySet = new IdentityHashSet<>();
      for (final KeyWrapper<K> ref : map.keySet()) {
         keySet.add(ref.get());
      }
      return Collections.unmodifiableSet(keySet);
   }

   @Override
   @SuppressWarnings("unchecked")
   public V put(final K key, final V value) {
      expungeStaleEntries();
      if (key == null)
         return map.put((KeyWrapper<K>) NULL_KEY_WRAPPER, value);
      return map.put(new WeakKeyWrapper<>(key, garbageCollectedRefs), value);
   }

   @Override
   public void putAll(final Map<? extends K, ? extends V> m) {
      expungeStaleEntries();
      for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
         put(e.getKey(), e.getValue());
      }
   }

   @Override
   public V remove(final Object key) {
      expungeStaleEntries();
      return map.remove(new LookupKeyWrapper(key));
   }

   boolean retainAll(final Collection<?> keysToKeep) {
      expungeStaleEntries();
      return map.keySet().retainAll(keysToKeep);
   }

   @Override
   public int size() {
      expungeStaleEntries();
      return map.size();
   }

   @Override
   public Collection<V> values() {
      expungeStaleEntries();
      return map.values();
   }
}
