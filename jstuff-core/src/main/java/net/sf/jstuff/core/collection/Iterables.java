/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Iterables {

   public static boolean contains(final @Nullable Iterable<?> it, final @Nullable Object searchFor) {
      if (it == null)
         return false;

      return StreamSupport.stream(it.spliterator(), false).anyMatch(e -> Objects.equals(e, searchFor));
   }

   public static boolean containsIdentical(final @Nullable Iterable<?> it, final @Nullable Object searchFor) {
      if (it == null)
         return false;

      return StreamSupport.stream(it.spliterator(), false).anyMatch(e -> e == searchFor);
   }

   public static long size(final @Nullable Iterable<?> it) {
      if (it == null)
         return 0;

      if (it instanceof final Collection<?> coll)
         return coll.size();

      return StreamSupport.stream(it.spliterator(), false).count();
   }

   public static <T> Collection<T> toCollection(final @Nullable Iterable<T> it) {
      if (it == null)
         return Collections.emptyList();

      if (it instanceof final Collection<T> coll)
         return coll;

      final var result = new ArrayList<T>();
      for (final T e : it) {
         result.add(e);
      }
      return result;
   }

   public static <T> List<T> toList(final @Nullable Iterable<T> it) {
      if (it == null)
         return Collections.emptyList();

      if (it instanceof List)
         return (List<T>) it;

      final var result = new ArrayList<T>();
      for (final T e : it) {
         result.add(e);
      }
      return result;
   }

   public static <T> Set<T> toSet(final @Nullable Iterable<T> it) {
      if (it == null)
         return Collections.emptySet();

      if (it instanceof final Set<T> set)
         return set;

      final var result = new HashSet<T>();
      for (final T e : it) {
         result.add(e);
      }
      return result;
   }

   public static <T> SortedSet<T> toSortedSet(final @Nullable Iterable<T> it) {
      if (it == null)
         return new TreeSet<>();

      if (it instanceof final SortedSet<T> set)
         return set;

      final var result = new TreeSet<T>();
      for (final T e : it) {
         result.add(e);
      }
      return result;
   }

   public static <T> Stream<T> toStream(final @Nullable Iterable<T> it) {
      if (it == null)
         return Stream.empty();

      return StreamSupport.stream(it.spliterator(), false);
   }
}
