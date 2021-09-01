/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Sets {

   public static <T> Set<T> newConcurrentHashSet() {
      return ConcurrentHashMap.newKeySet();
   }

   public static <T> Set<T> newConcurrentHashSet(final Collection<T> items) {
      if (items == null)
         return null;

      final Set<T> set = ConcurrentHashMap.newKeySet(items.size());
      set.addAll(items);
      return set;
   }

   public static <T> Set<T> newConcurrentHashSet(final int initialSize) {
      return ConcurrentHashMap.newKeySet(initialSize);
   }

   @SafeVarargs
   public static <T> Set<T> newConcurrentHashSet(final T... items) {
      if (items == null)
         return null;

      final Set<T> set = ConcurrentHashMap.newKeySet(items.length);
      CollectionUtils.addAll(set, items);
      return set;
   }

   public static <K> HashSet<K> newHashSet() {
      return new HashSet<>();
   }

   public static <K> HashSet<K> newHashSet(final Collection<K> initialValues) {
      return initialValues == null ? new HashSet<>() : new HashSet<>(initialValues);
   }

   public static <K> HashSet<K> newHashSet(final int initialSize) {
      return new HashSet<>(initialSize);
   }

   @SafeVarargs
   public static <K> HashSet<K> newHashSet(final K... values) {
      if (values == null || values.length == 0)
         return new HashSet<>();

      final HashSet<K> s = new HashSet<>(values.length);
      Collections.addAll(s, values);
      return s;
   }

   public static <V> LinkedHashSet<V> newLinkedHashSet() {
      return new LinkedHashSet<>();
   }

   public static <K> LinkedHashSet<K> newLinkedHashSet(final int initialSize) {
      return new LinkedHashSet<>(initialSize);
   }

   @SafeVarargs
   public static <K> LinkedHashSet<K> newLinkedHashSet(final K... values) {
      if (values == null || values.length == 0)
         return new LinkedHashSet<>();

      final LinkedHashSet<K> s = new LinkedHashSet<>(values.length);
      Collections.addAll(s, values);
      return s;
   }

   public static <S, T> Set<T> transform(final Set<S> source, final Function<? super S, ? extends T> op) {
      if (source == null)
         return null;

      final Set<T> target = Sets.newHashSet(source.size());
      for (final S sourceItem : source) {
         target.add(op.apply(sourceItem));
      }
      return target;
   }
}
