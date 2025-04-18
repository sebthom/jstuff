/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Iterators {

   @SafeVarargs
   public static <T> ArrayIterator<T> array(final T... array) {
      return new ArrayIterator<>(array);
   }

   public static <V> CompositeIterator<V> composite(final Collection<? extends @Nullable Iterator<? extends V>> iterators) {
      return new CompositeIterator<>(iterators);
   }

   @SafeVarargs
   public static <V> CompositeIterator<V> composite(final @NonNullByDefault({}) Iterator<? extends V>... iterators) {
      return new CompositeIterator<>(iterators);
   }

   public static boolean contains(final Iterator<?> iterator, final Object searchFor) {
      while (iterator.hasNext()) {
         final Object elem = iterator.next();
         if (Objects.equals(elem, searchFor))
            return true;
      }
      return false;
   }

   public static boolean containsIdentical(final Iterator<?> iterator, final Object searchFor) {
      while (iterator.hasNext())
         if (searchFor == iterator.next())
            return true;
      return false;
   }

   public static <T> Iterator<T> cycling(final Collection<T> items) {
      return new Iterator<>() {
         Iterator<T> it = items.iterator();

         @Override
         public boolean hasNext() {
            if (it.hasNext())
               return true;
            it = items.iterator();
            return it.hasNext();
         }

         @Override
         public T next() {
            return it.next();
         }
      };
   }

   @SafeVarargs
   public static <T> Iterator<T> cycling(final T... items) {
      final int size = items.length;

      if (size == 0)
         return empty();

      return new Iterator<>() {
         int i = 0;

         @Override
         public boolean hasNext() {
            return true;
         }

         @Override
         public T next() {
            return items[i++ % size];
         }
      };
   }

   @SuppressWarnings("unchecked")
   public static <T> EmptyIterator<T> empty() {
      return (EmptyIterator<T>) EmptyIterator.INSTANCE;
   }

   public static <T> SingleObjectIterator<T> single(final T object) {
      return new SingleObjectIterator<>(object);
   }

   public static int size(final Iterator<?> iterator) {
      int size = 0;
      while (iterator.hasNext()) {
         size++;
         iterator.next();
      }
      return size;
   }

   public static <T> Iterable<T> toIterable(final Iterator<T> it) {
      return () -> it;
   }

   public static <T> UnmodifiableIterator<T> unmodifiable(final Iterator<T> delegate) {
      return new UnmodifiableIterator<>(delegate);
   }

   public static <T> UnmodifiableListIterator<T> unmodifiable(final ListIterator<T> delegate) {
      return new UnmodifiableListIterator<>(delegate);
   }
}
