/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import java.math.BigInteger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MoreMath {

   /**
    * @throws ArithmeticException if result overflows long range
    * @throws ArithmeticException if exponent is negative
    */
   public static long pow(final long value, final int exponent) {
      return BigInteger.valueOf(value).pow(exponent).longValueExact();
   }
}
