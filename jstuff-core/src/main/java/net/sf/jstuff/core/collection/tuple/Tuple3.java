/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class Tuple3<T1, T2, T3> extends Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1, T2, T3> Tuple3<T1, T2, T3> create(final T1 value1, final T2 value2, final T3 value3) {
      return new Tuple3<>(value1, value2, value3);
   }

   public Tuple3(final T1 value1, final T2 value2, final T3 value3) {
      super(value1, value2, value3);
   }

   /*
    * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
    * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
    */
   @SuppressWarnings("unchecked")
   public T1 get1() {
      return (T1) getTyped(0);
   }

   /*
    * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
    * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
    */
   @SuppressWarnings("unchecked")
   public T2 get2() {
      return (T2) getTyped(1);
   }

   /*
    * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
    * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
    */
   @SuppressWarnings("unchecked")
   public T3 get3() {
      return (T3) getTyped(2);
   }
}
