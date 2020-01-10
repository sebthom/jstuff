/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import static net.sf.jstuff.core.types.Trilean.*;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TrileanTest extends TestCase {

   public void testNegate() {
      assertEquals(FALSE, TRUE.negate());
      assertEquals(TRUE, FALSE.negate());
      assertEquals(UNKNOWN, UNKNOWN.negate());
   }

   public void testAnd() {
      assertEquals(TRUE, TRUE.and(TRUE));
      assertEquals(FALSE, TRUE.and(FALSE));
      assertEquals(UNKNOWN, TRUE.and(UNKNOWN));
      assertEquals(UNKNOWN, TRUE.and(null));

      assertEquals(FALSE, FALSE.and(TRUE));
      assertEquals(FALSE, FALSE.and(FALSE));
      assertEquals(FALSE, FALSE.and(UNKNOWN));
      assertEquals(FALSE, FALSE.and(null));

      assertEquals(UNKNOWN, UNKNOWN.and(TRUE));
      assertEquals(FALSE, UNKNOWN.and(FALSE));
      assertEquals(UNKNOWN, UNKNOWN.and(UNKNOWN));
      assertEquals(UNKNOWN, UNKNOWN.and(null));
   }

   public void testOr() {
      assertEquals(TRUE, TRUE.or(TRUE));
      assertEquals(TRUE, TRUE.or(FALSE));
      assertEquals(TRUE, TRUE.or(UNKNOWN));
      assertEquals(TRUE, TRUE.or(null));

      assertEquals(TRUE, FALSE.or(TRUE));
      assertEquals(FALSE, FALSE.or(FALSE));
      assertEquals(UNKNOWN, FALSE.or(UNKNOWN));
      assertEquals(UNKNOWN, FALSE.or(null));

      assertEquals(TRUE, UNKNOWN.or(TRUE));
      assertEquals(UNKNOWN, UNKNOWN.or(FALSE));
      assertEquals(UNKNOWN, UNKNOWN.or(UNKNOWN));
      assertEquals(UNKNOWN, UNKNOWN.or(null));
   }

   public void testXor() {
      assertEquals(FALSE, TRUE.xor(TRUE));
      assertEquals(TRUE, TRUE.xor(FALSE));
      assertEquals(UNKNOWN, TRUE.xor(UNKNOWN));
      assertEquals(UNKNOWN, TRUE.xor(null));

      assertEquals(TRUE, FALSE.xor(TRUE));
      assertEquals(FALSE, FALSE.xor(FALSE));
      assertEquals(UNKNOWN, FALSE.xor(UNKNOWN));
      assertEquals(UNKNOWN, FALSE.xor(null));

      assertEquals(UNKNOWN, UNKNOWN.xor(TRUE));
      assertEquals(UNKNOWN, UNKNOWN.xor(FALSE));
      assertEquals(UNKNOWN, UNKNOWN.xor(UNKNOWN));
      assertEquals(UNKNOWN, UNKNOWN.xor(null));
   }
}
