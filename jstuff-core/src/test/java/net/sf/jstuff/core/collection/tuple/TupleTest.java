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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TupleTest extends TestCase {

   public void testTuple1() {
      final Tuple1<String> t1 = Tuple1.create("a");
      final Tuple1<String> t2 = Tuple1.create("b");
      final Tuple1<String> t3 = Tuple1.create("a");
      assertFalse(t1.equals(t2));
      assertEquals(t1, t3);
   }

   public void testTuple2() {
      final Tuple2<String, String> t1 = Tuple2.create("a", "a");
      final Tuple2<String, String> t2 = Tuple2.create("b", "a");
      final Tuple2<String, String> t3 = Tuple2.create("a", "a");
      assertFalse(t1.equals(t2));
      assertEquals(t1, t3);
   }
}
