/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Iterators {

   @SafeVarargs
   public static <T> ArrayIterator<T> array(final T... array) {
      return new ArrayIterator<T>(array);
   }

   public static <V> CompositeIterator<V> composite(final Collection<? extends Iterator<V>> components) {
      return new CompositeIterator<V>(components);
   }

   @SafeVarargs
   public static <V> CompositeIterator<V> composite(final Iterator<V>... components) {
      return new CompositeIterator<V>(components);
   }

   public static boolean contains(final Iterator<?> iterator, final Object searchFor) {
      Args.notNull("iterator", iterator);
      while (iterator.hasNext()) {
         final Object elem = iterator.next();
         if (Objects.equals(elem, searchFor))
            return true;
      }
      return false;
   }

   public static boolean containsIdentical(final Iterator<?> iterator, final Object searchFor) {
      Args.notNull("iterator", iterator);
      while (iterator.hasNext())
         if (searchFor == iterator.next())
            return true;
      return false;
   }

   @SuppressWarnings("unchecked")
   public static <T> EmptyIterator<T> empty() {
      return (EmptyIterator<T>) EmptyIterator.INSTANCE;
   }

   public static <T> SingleObjectIterator<T> single(final T object) {
      return new SingleObjectIterator<T>(object);
   }

   public static int size(final Iterator<?> iterator) {
      Args.notNull("iterator", iterator);
      int size = 0;
      while (iterator.hasNext()) {
         size++;
         iterator.next();
      }
      return size;
   }

   public static <T> Iterable<T> toIterable(final Iterator<T> it) {
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return it;
         }
      };
   }

   public static <T> UnmodifiableIterator<T> unmodifiable(final Iterator<T> delegate) {
      return new UnmodifiableIterator<T>(delegate);
   }

   public static <T> UnmodifiableListIterator<T> unmodifiable(final ListIterator<T> delegate) {
      return new UnmodifiableListIterator<T>(delegate);
   }
}
