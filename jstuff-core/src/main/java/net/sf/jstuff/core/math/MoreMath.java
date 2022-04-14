/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import java.math.BigInteger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MoreMath {

   /**
    * Clamps the given value to be between min and max.
    */
   public static long clamp(final long value, final long min, final long max) {
      if (value < min)
         return min;
      if (value > max)
         return max;
      return value;
   }

   /**
    * Clamps the given value to be between min and max.
    */
   public static long clamp(final int value, final int min, final int max) {
      if (value < min)
         return min;
      if (value > max)
         return max;
      return value;
   }

   /**
    * @throws ArithmeticException if result overflows long range
    * @throws ArithmeticException if exponent is negative
    */
   public static long pow(final long value, final int exponent) {
      return BigInteger.valueOf(value).pow(exponent).longValueExact();
   }

}
