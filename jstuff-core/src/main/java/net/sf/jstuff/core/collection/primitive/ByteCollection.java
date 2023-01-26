/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import net.sf.jstuff.core.collection.ext.CollectionExt;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ByteCollection extends CollectionExt<Byte> {

   boolean addAll(byte... values);

   boolean add(byte value);

   boolean contains(byte value);

   boolean containsAll(byte... values);

   void forEach(IntConsumer consumer);

   boolean removeIf(IntPredicate filter);

   /**
    * @deprecated use {@link #removeIf(IntPredicate)}
    */
   @Deprecated
   @Override
   default boolean removeIf(final Predicate<? super Byte> filter) {
      return CollectionExt.super.removeIf(filter);
   }

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Byte[] toArray();

   byte[] toValueArray();
}
