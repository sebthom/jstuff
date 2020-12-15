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

import java.math.BigInteger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
