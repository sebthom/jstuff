/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

import java.util.AbstractList;

import net.sf.jstuff.core.concurrent.Immutable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Immutable
public final class Tuple5<T1, T2, T3, T4, T5> extends AbstractList<Object> implements Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> create(final T1 value1, final T2 value2, final T3 value3, final T4 value4,
         final T5 value5) {
      return new Tuple5<>(value1, value2, value3, value4, value5);
   }

   private final T1 v1;
   private final T2 v2;
   private final T3 v3;
   private final T4 v4;
   private final T5 v5;

   public Tuple5(final T1 value1, final T2 value2, final T3 value3, final T4 value4, final T5 value5) {
      v1 = value1;
      v2 = value2;
      v3 = value3;
      v4 = value4;
      v5 = value5;
   }

   @Override
   @SuppressWarnings("null")
   public Object get(final int index) {
      switch (index) {
         case 0:
            return v1;
         case 1:
            return v2;
         case 2:
            return v3;
         case 3:
            return v4;
         case 4:
            return v5;
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

   public T4 get4() {
      return v4;
   }

   public T5 get5() {
      return v5;
   }

   @Override
   public int size() {
      return 5;
   }

   @Override
   public Object[] toArray() {
      return new Object[] {v1, v2, v3, v4, v5};
   }
}
