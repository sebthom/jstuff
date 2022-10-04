/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashSet<E> extends AbstractSet<E> implements Cloneable {
   public static <E> WeakIdentityHashSet<E> create() {
      return new WeakIdentityHashSet<>();
   }

   public static <E> WeakIdentityHashSet<E> create(final int initialCapacity) {
      return new WeakIdentityHashSet<>(initialCapacity);
   }

   private final WeakIdentityHashMap<E, Boolean> map;

   public WeakIdentityHashSet() {
      this(16);
   }

   public WeakIdentityHashSet(final int initialCapacity) {
      map = new WeakIdentityHashMap<>(initialCapacity);
   }

   @Override
   public boolean add(final E e) {
      return map.put(e, Boolean.TRUE) == null;
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public WeakIdentityHashSet<E> clone() throws CloneNotSupportedException {
      final WeakIdentityHashSet<E> copy = new WeakIdentityHashSet<>(size());
      copy.addAll(this);
      return copy;
   }

   @Override
   public boolean contains(final @Nullable Object o) {
      return map.containsKey(o);
   }

   @Override
   public boolean containsAll(final Collection<?> c) {
      return map.keySet().containsAll(c);
   }

   @Override
   public boolean equals(final @Nullable Object o) {
      if (o == null)
         return false;
      if (o == this)
         return true;
      if (!(o instanceof Set))
         return false;

      final Set<?> otherSet = (Set<?>) o;
      if (otherSet.size() != size())
         return false;

      for (final E key : map.keySet())
         if (!otherSet.contains(key))
            return false;
      return true;
   }

   @Override
   public int hashCode() {
      return map.keySet().hashCode();
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Iterator<E> iterator() {
      return map.keySet().iterator();
   }

   @Override
   public boolean remove(final @Nullable Object o) {
      return map.remove(o) != null;
   }

   @Override
   public boolean removeAll(final Collection<?> itemsToRemove) {
      boolean changed = false;
      for (final Object k : itemsToRemove)
         if (map.remove(k) != null) {
            changed = true;
         }
      return changed;
   }

   @Override
   public boolean retainAll(final Collection<?> itemsToKeep) {
      return map.retainAll(itemsToKeep);
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Object[] toArray() {
      return map.keySet().toArray();
   }

   @Override
   public <T> T[] toArray(final T[] a) {
      return map.keySet().toArray(a);
   }

   @Override
   public String toString() {
      return map.keySet().toString();
   }
}
