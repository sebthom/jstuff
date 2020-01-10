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

import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.collection.iterator.Iterators;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MapWithCollections<K, V, C extends Collection<V>> extends MapWith<K, C> {
   private static final long serialVersionUID = 1L;

   protected int initialCapacityOfCollection = 2;
   protected float growthFactorOfCollection = 0.75f;

   public MapWithCollections() {
   }

   public MapWithCollections(final int initialCapacity) {
      super(initialCapacity);
   }

   public MapWithCollections(final int initialCapacity, final int initialCapacityOfCollection) {
      super(initialCapacity);
      this.initialCapacityOfCollection = initialCapacityOfCollection;
   }

   public MapWithCollections(final int initialCapacity, final int initialCapacityOfCollection, final float growthFactorOfCollection) {
      super(initialCapacity);

      this.initialCapacityOfCollection = initialCapacityOfCollection;
      this.growthFactorOfCollection = growthFactorOfCollection;
   }

   public void add(final K key, final V value) {
      getOrCreate(key).add(value);
   }

   public void addAll(final K key, final Collection<V> values) {
      if (values == null)
         return;

      getOrCreate(key).addAll(values);
   }

   public void addAll(final K key, @SuppressWarnings("unchecked") final V... values) {
      if (values == null)
         return;

      CollectionUtils.addAll(getOrCreate(key), values);
   }

   public boolean containsValue(final K key, final V value) {
      final C values = get(key);
      return values != null && values.contains(value);
   }

   /**
    * Checks whether the map contains the value specified.
    * <p>
    * This checks the lists of all keys for the value, and thus could be slow.
    *
    * @param value the value to search for
    * @return true if any of the lists referenced by the map contains the value
    */
   @Override
   public boolean containsValue(final Object value) {
      for (final Entry<K, C> entry : entrySet())
         if (entry.getValue() != null && entry.getValue().contains(value))
            return true;
      return false;
   }

   @SuppressWarnings("unchecked")
   public Iterator<V> iterator(final K key) {
      final C values = get(key);
      return values == null ? (Iterator<V>) Iterators.empty() : values.iterator();
   }

   /**
    * Associates the specified value with the specified key in this map (optional operation).
    * If the map previously contained a mapping for this key, the old value is replaced by the specified value.
    *
    * @param key key with which the specified value is to be associated
    * @param values value to be associated with the specified key.
    * @return previous value associated with specified key, or null if there was no mapping for key.
    *         A null return can also indicate that the map previously associated null with the specified key,
    *         if the implementation supports null values.
    */
   public C put(final K key, @SuppressWarnings("unchecked") final V... values) {
      final C coll = create(key);
      CollectionUtils.addAll(coll, values);
      return put(key, coll);
   }

   public boolean removeValue(final K key, final V value) {
      final C values = get(key);
      return values == null ? false : values.remove(value);
   }

   public int size(final K key) {
      final C values = get(key);
      return values == null ? 0 : values.size();
   }
}
