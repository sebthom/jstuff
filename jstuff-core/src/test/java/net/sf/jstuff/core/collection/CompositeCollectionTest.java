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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollectionTest extends TestCase {

   public void testCompositeCollection() {
      final List<String> l1 = Arrays.asList("a", "b");
      final List<String> l2 = Arrays.asList("c", "d");

      final Collection<String> cc = CompositeCollection.of(l1, l2);
      assertEquals(4, cc.size());

      final List<String> l3 = new ArrayList<>(cc);
      assertEquals("a", l3.get(0));
      assertEquals("b", l3.get(1));
      assertEquals("c", l3.get(2));
      assertEquals("d", l3.get(3));
      assertEquals(4, l3.size());

      try {
         cc.add("foo");
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cc.remove("a");
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
