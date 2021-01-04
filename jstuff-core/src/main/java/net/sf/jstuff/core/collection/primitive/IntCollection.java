/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.Collection;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface IntCollection extends Collection<Integer> {

   boolean add(int value);

   boolean addAll(int... values);

   boolean contains(int value);

   boolean containsAll(int... values);

   void forEach(IntConsumer consumer);

   boolean removeIf(IntPredicate filter);

   /**
    * @deprecated use {@link #removeIf(IntPredicate)}
    */
   @Deprecated
   @Override
   default boolean removeIf(final Predicate<? super Integer> filter) {
      return Collection.super.removeIf(filter);
   }

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Integer[] toArray();

   int[] toValueArray();
}
