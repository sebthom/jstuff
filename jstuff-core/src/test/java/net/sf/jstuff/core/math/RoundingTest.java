/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RoundingTest extends TestCase {

   private void testRounding(final long expected, final long input, final Rounding rounding) {
      assertEquals(expected, rounding.round(input));
      assertEquals((double) expected, rounding.round((double) input));
      assertEquals(BigDecimal.valueOf(expected), rounding.round(BigDecimal.valueOf(input)));
      assertEquals(BigInteger.valueOf(expected), rounding.round(BigInteger.valueOf(input)));
   }

   private void testRounding(final double expected, final double input, final Rounding rounding) {
      assertEquals(expected, rounding.round(input));
      assertEquals(BigDecimal.valueOf(expected).stripTrailingZeros(), rounding.round(BigDecimal.valueOf(input)));
   }

   public void testIntegerRounding() {
      {
         final Rounding rounding = new Rounding(-2, RoundingMode.UP);
         testRounding(100, 1, rounding);
         testRounding(100, 49, rounding);
         testRounding(100, 50, rounding);
         testRounding(100, 99, rounding);
         testRounding(100, 100, rounding);
         testRounding(200, 101, rounding);
         testRounding(200, 149, rounding);
         testRounding(200, 150, rounding);
         testRounding(200, 199, rounding);
         testRounding(200, 200, rounding);
         testRounding(1100, 1001, rounding);
         testRounding(1100, 1049, rounding);
         testRounding(1100, 1050, rounding);
         testRounding(1100, 1099, rounding);
         testRounding(1100, 1100, rounding);
      }

      {
         final Rounding rounding = new Rounding(-2, RoundingMode.HALF_UP);
         testRounding(0, 1, rounding);
         testRounding(0, 49, rounding);
         testRounding(100, 50, rounding);
         testRounding(100, 99, rounding);
         testRounding(100, 100, rounding);
         testRounding(100, 101, rounding);
         testRounding(100, 149, rounding);
         testRounding(200, 150, rounding);
         testRounding(200, 199, rounding);
         testRounding(200, 200, rounding);
         testRounding(1000, 1001, rounding);
         testRounding(1000, 1049, rounding);
         testRounding(1100, 1050, rounding);
         testRounding(1100, 1099, rounding);
         testRounding(1100, 1100, rounding);
      }
   }

   public void testDecimalRounding() {
      {
         final Rounding rounding = new Rounding(2, RoundingMode.UP);
         testRounding(0.01, 0.0001, rounding);
         testRounding(0.01, 0.0049, rounding);
         testRounding(0.01, 0.0050, rounding);
         testRounding(0.01, 0.0099, rounding);
         testRounding(0.01, 0.0100, rounding);
         testRounding(0.02, 0.0101, rounding);
         testRounding(0.02, 0.0149, rounding);
         testRounding(0.02, 0.0150, rounding);
         testRounding(0.02, 0.0199, rounding);
         testRounding(0.02, 0.0200, rounding);
         testRounding(0.11, 0.1001, rounding);
         testRounding(0.11, 0.1049, rounding);
         testRounding(0.11, 0.1050, rounding);
         testRounding(0.11, 0.1099, rounding);
         testRounding(0.11, 0.1100, rounding);
      }

      {
         final Rounding rounding = new Rounding(2, RoundingMode.HALF_UP);
         testRounding(0.0, 0.0001, rounding);
         testRounding(0.0, 0.0049, rounding);
         testRounding(0.01, 0.0050, rounding);
         testRounding(0.01, 0.0099, rounding);
         testRounding(0.01, 0.0100, rounding);
         testRounding(0.01, 0.0101, rounding);
         testRounding(0.01, 0.0149, rounding);
         testRounding(0.02, 0.0150, rounding);
         testRounding(0.02, 0.0199, rounding);
         testRounding(0.02, 0.0200, rounding);
         testRounding(0.10, 0.1001, rounding);
         testRounding(0.10, 0.1049, rounding);
         testRounding(0.11, 0.1050, rounding);
         testRounding(0.11, 0.1099, rounding);
         testRounding(0.11, 0.1100, rounding);
      }
   }
}
