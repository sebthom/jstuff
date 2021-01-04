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
public interface ByteCollection extends Collection<Byte> {

   boolean addAll(byte... values);

   boolean add(byte value);

   boolean contains(byte value);

   boolean containsAll(byte... values);

   void forEach(LongConsumer consumer);

   boolean removeIf(LongPredicate filter);

   /**
    * @deprecated use {@link #removeIf(LongPredicate)}
    */
   @Deprecated
   @Override
   default boolean removeIf(final Predicate<? super Byte> filter) {
      return Collection.super.removeIf(filter);
   }

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Byte[] toArray();

   byte[] toValueArray();
}
