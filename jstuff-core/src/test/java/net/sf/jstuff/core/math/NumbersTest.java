/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.math;

import java.math.BigInteger;
import java.util.UUID;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumbersTest extends TestCase {

   public void testIsLong() {
      assertEquals(true, Numbers.isLong(BigInteger.ZERO));
      assertEquals(true, Numbers.isLong(BigInteger.TEN));
      assertEquals(true, Numbers.isLong(BigInteger.valueOf(Long.MAX_VALUE)));
      assertEquals(true, Numbers.isLong(BigInteger.valueOf(Long.MIN_VALUE)));
      assertEquals(false, Numbers.isLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1))));
      assertEquals(false, Numbers.isLong(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1))));
   }

   public void testUuidToBigInteger() {
      final UUID uuid = UUID.randomUUID();
      assertEquals(new BigInteger(uuid.toString().replaceAll("-", ""), 16), Numbers.toBigInteger(uuid));
   }

}
