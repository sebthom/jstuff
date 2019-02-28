/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.tuple;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Tuple4<T1, T2, T3, T4> extends Tuple {
   private static final long serialVersionUID = 1L;

   public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> create(final T1 value1, final T2 value2, final T3 value3, final T4 value4) {
      return new Tuple4<T1, T2, T3, T4>(value1, value2, value3, value4);
   }

   public Tuple4(final T1 value1, final T2 value2, final T3 value3, final T4 value4) {
      super(value1, value2, value3, value4);
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

   /*
    * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
    * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
    */
   @SuppressWarnings("unchecked")
   public T4 get4() {
      return (T4) getTyped(3);
   }
}
