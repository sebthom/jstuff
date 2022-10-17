/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Sets {

   public static <T> Set<T> newConcurrentHashSet() {
      return ConcurrentHashMap.newKeySet();
   }

   public static <T> Set<T> newConcurrentHashSet(final @Nullable Collection<T> initialValues) {
      if (initialValues == null || initialValues.isEmpty())
         return newConcurrentHashSet();

      final Set<T> set = ConcurrentHashMap.newKeySet(initialValues.size());
      set.addAll(initialValues);
      return set;
   }

   public static <T> Set<T> newConcurrentHashSet(final int initialSize) {
      return ConcurrentHashMap.newKeySet(initialSize);
   }

   @SafeVarargs
   public static <T> Set<T> newConcurrentHashSet(final T @Nullable... initialValues) {
      if (initialValues == null || initialValues.length == 0)
         return newConcurrentHashSet();

      final Set<T> set = ConcurrentHashMap.newKeySet(initialValues.length);
      CollectionUtils.addAll(set, initialValues);
      return set;
   }

   public static <K> HashSet<K> newHashSet() {
      return new HashSet<>();
   }

   public static <K> HashSet<K> newHashSet(final @Nullable Collection<K> initialValues) {
      return initialValues == null ? new HashSet<>() : new HashSet<>(initialValues);
   }

   public static <K> HashSet<K> newHashSet(final int initialSize) {
      return new HashSet<>(initialSize);
   }

   @SafeVarargs
   public static <K> HashSet<K> newHashSet(final K @Nullable... initialValues) {
      if (initialValues == null || initialValues.length == 0)
         return new HashSet<>();

      final var s = new HashSet<K>(initialValues.length);
      Collections.addAll(s, initialValues);
      return s;
   }

   public static <V> LinkedHashSet<V> newLinkedHashSet() {
      return new LinkedHashSet<>();
   }

   public static <K> LinkedHashSet<K> newLinkedHashSet(final int initialSize) {
      return new LinkedHashSet<>(initialSize);
   }

   @SafeVarargs
   public static <K> LinkedHashSet<K> newLinkedHashSet(final K @Nullable... initialValues) {
      if (initialValues == null || initialValues.length == 0)
         return new LinkedHashSet<>();

      final var s = new LinkedHashSet<K>(initialValues.length);
      Collections.addAll(s, initialValues);
      return s;
   }

   public static <S, T> Set<T> transform(final Set<S> source, final Function<? super S, ? extends T> op) {
      return asNonNullUnsafe(transformNullable(source, op));
   }

   public static <S, T> @Nullable Set<T> transformNullable(final @Nullable Set<S> source, final Function<? super S, ? extends T> op) {
      if (source == null)
         return null;
      Args.notNull("op", op);

      final var target = new HashSet<T>(source.size());
      for (final S sourceItem : source) {
         target.add(op.apply(sourceItem));
      }
      return target;
   }
}
