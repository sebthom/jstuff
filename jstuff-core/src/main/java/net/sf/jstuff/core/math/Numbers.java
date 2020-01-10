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
import java.nio.ByteBuffer;
import java.util.UUID;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Numbers extends org.apache.commons.lang3.math.NumberUtils {

   public static final int MILLION = 1000 * 1000;
   public static final int BILLION = 1000 * MILLION;

   static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
   static final BigInteger INTEGER_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);

   static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
   static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

   public static boolean isInteger(final BigInteger number) {
      Args.notNull("number", number);
      return INTEGER_MAX_VALUE.compareTo(number) >= 0 && INTEGER_MIN_VALUE.compareTo(number) <= 0;
   }

   public static boolean isLong(final BigInteger number) {
      Args.notNull("number", number);
      return LONG_MAX_VALUE.compareTo(number) >= 0 && LONG_MIN_VALUE.compareTo(number) <= 0;
   }

   public static BigInteger toBigInteger(final UUID uuid) {
      if (uuid == null)
         return null;

      return new BigInteger(1, //
         ByteBuffer.wrap(new byte[16]) //
            .putLong(uuid.getMostSignificantBits())//
            .putLong(uuid.getLeastSignificantBits())//
            .array() //
      );
   }
}
