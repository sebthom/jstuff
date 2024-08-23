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
public final class Tuple2<T1, T2> extends AbstractList<Object> implements Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1, T2> Tuple2<T1, T2> create(final T1 value1, final T2 value2) {
      return new Tuple2<>(value1, value2);
   }

   private final T1 v1;
   private final T2 v2;

   public Tuple2(final T1 value1, final T2 value2) {
      v1 = value1;
      v2 = value2;
   }

   @Override
   @SuppressWarnings("null")
   public Object get(final int index) {
      return switch (index) {
         case 0 -> v1;
         case 1 -> v2;
         default -> throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
      };
   }

   public T1 get1() {
      return v1;
   }

   public T2 get2() {
      return v2;
   }

   @Override
   public int size() {
      return 2;
   }

   @Override
   public Object[] toArray() {
      return new Object[] {v1, v2};
   }
}
