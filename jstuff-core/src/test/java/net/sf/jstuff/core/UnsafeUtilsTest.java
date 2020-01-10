/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core;

import static org.junit.Assert.*;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnsafeUtilsTest extends TestCase {

   public void testAddressOf() {
      final Object obj1 = new Object();
      assertTrue(UnsafeUtils.addressOf(obj1) > 0);

      assertEquals(UnsafeUtils.addressOf(obj1), UnsafeUtils.addressOf(obj1));

      final Object obj2 = new Object();
      assertTrue(UnsafeUtils.addressOf(obj2) > 0);

      assertNotEquals(UnsafeUtils.addressOf(obj1), UnsafeUtils.addressOf(obj2));
   }
}
