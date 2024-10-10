/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import net.sf.jstuff.core.collection.ext.CollectionExt;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface LongCollection extends CollectionExt<Long> {

   boolean addAll(long... values);

   boolean add(long value);

   boolean contains(long value);

   boolean containsAll(long... values);

   void forEach(LongConsumer consumer);

   boolean removeIf(LongPredicate filter);

   /**
    * @deprecated Use {@link #removeIf(LongPredicate)}
    */
   @Deprecated
   @Override
   default boolean removeIf(final Predicate<? super Long> filter) {
      return CollectionExt.super.removeIf(filter);
   }

   /**
    * @deprecated Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Long[] toArray();

   long[] toValueArray();
}
