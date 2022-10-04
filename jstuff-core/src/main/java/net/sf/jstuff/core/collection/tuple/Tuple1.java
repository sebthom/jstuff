/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

import java.util.AbstractList;

import net.sf.jstuff.core.concurrent.Immutable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Immutable
public final class Tuple1<T1> extends AbstractList<Object> implements Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1> Tuple1<T1> create(final T1 value1) {
      return new Tuple1<>(value1);
   }

   private final T1 v1;

   public Tuple1(final T1 value1) {
      v1 = value1;
   }

   @Override
   @SuppressWarnings("null")
   public Object get(final int index) {
      if (index == 0)
         return v1;
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
   }

   public T1 get1() {
      return v1;
   }

   @Override
   public int size() {
      return 1;
   }

   @Override
   public Object[] toArray() {
      return new Object[] {v1};
   }
}
