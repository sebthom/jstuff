/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapBackedSet<E> implements Set<E>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <E> MapBackedSet<E> create(final Map<E, Boolean> emptyMap) {
      return new MapBackedSet<>(emptyMap);
   }

   private final Map<E, Boolean> map;
   private transient Set<E> keys;

   public MapBackedSet(final Map<E, Boolean> emptyMap) {
      Args.notNull("map", emptyMap);
      if (!emptyMap.isEmpty())
         throw new IllegalArgumentException("Argument [map] is not an empty map");

      this.map = emptyMap;
      keys = emptyMap.keySet();
   }

   @Override
   public boolean add(final E e) {
      return map.put(e, Boolean.TRUE) == null;
   }

   @Override
   public boolean addAll(final Collection<? extends E> c) {
      boolean modified = false;
      for (final E e : c)
         if (add(e)) {
            modified = true;
         }
      return modified;
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean contains(final @Nullable Object o) {
      return map.containsKey(o);
   }

   @Override
   public boolean containsAll(final Collection<?> c) {
      return keys.containsAll(c);
   }

   @Override
   public boolean equals(final @Nullable Object o) {
      return o == this || keys.equals(o);
   }

   @Override
   public void forEach(final Consumer<? super E> action) {
      keys.forEach(action);
   }

   @Override
   public int hashCode() {
      return keys.hashCode();
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Iterator<E> iterator() {
      return keys.iterator();
   }

   @Override
   public Stream<E> parallelStream() {
      return keys.parallelStream();
   }

   private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      keys = map.keySet();
   }

   @Override
   public boolean remove(final @Nullable Object o) {
      return map.remove(o) != null;
   }

   @Override
   public boolean removeAll(final Collection<?> c) {
      return keys.removeAll(c);
   }

   @Override
   public boolean removeIf(final Predicate<? super E> filter) {
      return keys.removeIf(filter);
   }

   @Override
   public boolean retainAll(final Collection<?> c) {
      return keys.retainAll(c);
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Spliterator<E> spliterator() {
      return keys.spliterator();
   }

   @Override
   public Stream<E> stream() {
      return keys.stream();
   }

   @Override
   public Object[] toArray() {
      return keys.toArray();
   }

   @Override
   public <T> T[] toArray(final T[] a) {
      return keys.toArray(a);
   }

   @Override
   public String toString() {
      return keys.toString();
   }
}
