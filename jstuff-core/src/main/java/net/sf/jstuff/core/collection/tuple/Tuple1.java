/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class Tuple1<T1> extends Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1> Tuple1<T1> create(final T1 value1) {
      return new Tuple1<>(value1);
   }

   public Tuple1(final T1 value1) {
      super(value1);
   }

   /*
    * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
    * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
    */
   @SuppressWarnings("unchecked")
   public T1 get1() {
      return (T1) getTyped(0);
   }
}
