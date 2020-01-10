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

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeMapTest extends TestCase {

   public void testCompositeList() {
      final Map<String, String> m1 = Maps.toMap("a=1;b=2;c=3", ";", "=");
      final Map<String, String> m2 = Maps.toMap("c=4;d=5;e=6", ";", "=");

      final CompositeMap<String, String> cm = CompositeMap.of(m1, m2);
      assertEquals(5, cm.size());
      assertEquals(5, cm.keySet().size());
      assertEquals(5, cm.values().size());
      assertEquals("1", cm.get("a"));
      assertEquals("3", cm.get("c"));

      m1.put("a", "X");
      m1.put("d", "Y");
      m2.put("f", "Z");
      assertEquals(6, cm.size());
      assertEquals(6, cm.keySet().size());
      assertEquals(6, cm.values().size());
      try {
         cm.put("f", "f");
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }

      try {
         cm.remove("a");
         fail();
      } catch (final UnsupportedOperationException ex) {
         // expected
      }
   }
}
