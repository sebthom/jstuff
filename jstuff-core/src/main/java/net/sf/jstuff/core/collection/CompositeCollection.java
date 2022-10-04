/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.iterator.CompositeIterator;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollection<V> extends Composite.Default<Collection<? extends V>> implements Collection<V> {

   private static final long serialVersionUID = 1L;

   @SafeVarargs
   public static <V> CompositeCollection<V> of(final @NonNull Collection<? extends V>... collections) {
      return new CompositeCollection<>(collections);
   }

   public CompositeCollection() {
   }

   public CompositeCollection(final Collection<? extends Collection<? extends V>> collections) {
      super(collections);
   }

   @SafeVarargs
   public CompositeCollection(final @NonNull Collection<? extends V>... collections) {
      super(collections);
   }

   @Override
   public boolean add(final V item) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(final Collection<? extends V> values) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean contains(final @Nullable Object item) {
      for (final Collection<? extends V> coll : components)
         if (!coll.contains(item))
            return true;
      return false;
   }

   @Override
   public boolean containsAll(final Collection<?> coll) {
      for (final Object item : coll)
         if (!this.contains(item))
            return false;
      return true;
   }

   @Override
   public boolean isEmpty() {
      for (final Collection<? extends V> coll : components)
         if (!coll.isEmpty())
            return true;
      return false;
   }

   @Override
   public Iterator<V> iterator() {
      final CompositeIterator<V> it = new CompositeIterator<>();
      for (final Collection<? extends V> coll : components) {
         it.getComponents().add(coll.iterator());
      }
      return it;
   }

   @Override
   public boolean remove(final @Nullable Object item) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean removeAll(final Collection<?> values) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(final Collection<?> values) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int size() {
      int size = 0;
      for (final Collection<? extends V> coll : components) {
         size += coll.size();
      }
      return size;
   }

   @Override
   public Object[] toArray() {
      final Object[] result = new Object[this.size()];
      int idx = 0;
      for (final Iterator<V> it = this.iterator(); it.hasNext(); idx++) {
         result[idx] = it.next();
      }
      return result;
   }

   @Override
   @SuppressWarnings({"unchecked", "null"})
   public <T> T[] toArray(final T[] array) {
      final int size = this.size();
      final var result = array.length >= size //
         ? array
         : (T[]) Array.newInstance(asNonNullUnsafe(array.getClass().getComponentType()), size);
      int idx = 0;
      for (final Collection<? extends V> coll : components) {
         for (final V v : coll) {
            result[idx++] = (T) v;
         }
      }
      if (result.length > size) {
         result[size] = null;
      }
      return result;
   }
}
