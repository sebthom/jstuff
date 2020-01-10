/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeListTest extends TestCase {

   public void testCompositeList() {
      final List<String> l1 = Arrays.asList("a", "b");
      final List<String> l2 = Arrays.asList("c", "d");

      final List<String> cl = new CompositeList<>(l1, l2);
      assertEquals(4, cl.size());
      assertEquals("a", cl.get(0));
      assertEquals("b", cl.get(1));
      assertEquals("c", cl.get(2));
      assertEquals("d", cl.get(3));
      try {
         cl.get(10);
      } catch (final IndexOutOfBoundsException ex) {
         assertEquals("Index: 10, Size: 4", ex.getMessage());
      }

      try {
         cl.add("foo");
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cl.remove(2);
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
