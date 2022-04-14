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
public final class Tuple3<T1, T2, T3> extends AbstractList<Object> implements Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1, T2, T3> Tuple3<T1, T2, T3> create(final T1 value1, final T2 value2, final T3 value3) {
      return new Tuple3<>(value1, value2, value3);
   }

   private final T1 v1;
   private final T2 v2;
   private final T3 v3;

   public Tuple3(final T1 value1, final T2 value2, final T3 value3) {
      v1 = value1;
      v2 = value2;
      v3 = value3;
   }

   @Override
   public Object get(final int index) {
      switch (index) {
         case 0:
            return v1;
         case 1:
            return v2;
         case 2:
            return v3;
         default:
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
      }
   }

   public T1 get1() {
      return v1;
   }

   public T2 get2() {
      return v2;
   }

   public T3 get3() {
      return v3;
   }

   @Override
   public int size() {
      return 3;
   }

   @Override
   public Object[] toArray() {
      return new Object[] {v1, v2, v3};
   }
}
