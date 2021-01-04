/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.Collection;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface LongCollection extends Collection<Long> {

   boolean addAll(long... values);

   boolean add(long value);

   boolean contains(long value);

   boolean containsAll(long... values);

   void forEach(LongConsumer consumer);

   boolean removeIf(LongPredicate filter);

   /**
    * @deprecated use {@link #removeIf(LongPredicate)}
    */
   @Deprecated
   @Override
   default boolean removeIf(final Predicate<? super Long> filter) {
      return Collection.super.removeIf(filter);
   }

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Long[] toArray();

   long[] toValueArray();
}
