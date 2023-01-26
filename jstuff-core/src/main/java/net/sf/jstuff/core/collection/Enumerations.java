/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.iterator.Iterators;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Enumerations {

   public static boolean contains(final @Nullable Enumeration<?> en, final @Nullable Object searchFor) {
      if (en == null)
         return false;

      while (en.hasMoreElements()) {
         final Object elem = en.nextElement();
         if (Objects.equals(elem, searchFor))
            return true;
      }
      return false;
   }

   public static boolean containsIdentical(final @Nullable Enumeration<?> en, final @Nullable Object searchFor) {
      if (en == null)
         return false;

      while (en.hasMoreElements())
         if (searchFor == en.nextElement())
            return true;
      return false;
   }

   public static int size(final @Nullable Enumeration<?> en) {
      if (en == null)
         return 0;

      int size = 0;
      while (en.hasMoreElements()) {
         size++;
         en.nextElement();
      }
      return size;
   }

   public static <T> Iterable<T> toIterable(final @Nullable Enumeration<T> en) {
      if (en == null)
         return Iterators::empty;

      return () -> new Iterator<>() {
         @Override
         public boolean hasNext() {
            return en.hasMoreElements();
         }

         @Override
         public T next() {
            return en.nextElement();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public static <T> List<T> toList(final @Nullable Enumeration<T> en) {
      if (en == null)
         return Collections.emptyList();

      return Collections.list(en);
   }

   public static <T> Set<T> toSet(final @Nullable Enumeration<T> en) {
      if (en == null)
         return Collections.emptySet();

      final var result = new HashSet<T>();
      while (en.hasMoreElements()) {
         result.add(en.nextElement());
      }
      return result;
   }

   public static <T> SortedSet<T> toSortedSet(final @Nullable Enumeration<T> en) {
      if (en == null)
         return new TreeSet<>();

      final var result = new TreeSet<T>();
      while (en.hasMoreElements()) {
         result.add(en.nextElement());
      }
      return result;
   }

   public static <T> Stream<T> toStream(final @Nullable Enumeration<T> en) {
      if (en == null)
         return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(final Consumer<? super T> action) {
               return false;
            }
         }, false);

      return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
         @Override
         public void forEachRemaining(final Consumer<? super T> action) {
            while (en.hasMoreElements()) {
               action.accept(en.nextElement());
            }
         }

         @Override
         public boolean tryAdvance(final Consumer<? super T> action) {
            if (en.hasMoreElements()) {
               action.accept(en.nextElement());
               return true;
            }
            return false;
         }
      }, false);
   }
}
