/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.mutable.MutableByte;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.mutable.MutableShort;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class Numbers extends org.apache.commons.lang3.math.NumberUtils {

   public static final int MILLION = 1000 * 1000;
   public static final int BILLION = 1000 * MILLION;
   public static final int TRILLION = 1000 * BILLION;

   public static final BigInteger BYTE_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
   public static final BigInteger BYTE_MIN_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);

   private static final BigDecimal BYTE_MAX_VALUE_BD = BigDecimal.valueOf(Byte.MAX_VALUE);
   private static final BigDecimal BYTE_MIN_VALUE_BD = BigDecimal.valueOf(Byte.MIN_VALUE);

   public static final BigInteger SHORT_MAX_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
   public static final BigInteger SHORT_MIN_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);

   private static final BigDecimal SHORT_MAX_VALUE_BD = BigDecimal.valueOf(Short.MAX_VALUE);
   private static final BigDecimal SHORT_MIN_VALUE_BD = BigDecimal.valueOf(Short.MIN_VALUE);

   public static final BigInteger INTEGER_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
   public static final BigInteger INTEGER_MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);

   private static final BigDecimal INTEGER_MAX_VALUE_BD = BigDecimal.valueOf(Integer.MAX_VALUE);
   private static final BigDecimal INTEGER_MIN_VALUE_BD = BigDecimal.valueOf(Integer.MIN_VALUE);

   public static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
   public static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

   private static final BigDecimal LONG_MAX_VALUE_BD = BigDecimal.valueOf(Long.MAX_VALUE);
   private static final BigDecimal LONG_MIN_VALUE_BD = BigDecimal.valueOf(Long.MIN_VALUE);

   public static boolean isByte(final Number number) {
      if (number == null)
         return false;

      if (number instanceof Byte || number instanceof MutableByte)
         return true;

      if (number instanceof Integer //
         || number instanceof Short //
         || number instanceof AtomicLong //
         || number instanceof AtomicInteger //
         || number instanceof LongAdder //
         || number instanceof MutableLong //
         || number instanceof MutableInt //
         || number instanceof MutableShort //
      ) {
         final long longValue = number.longValue();
         return longValue <= Byte.MAX_VALUE && longValue >= Byte.MIN_VALUE;
      }

      if (number instanceof BigInteger) {
         final BigInteger numberBI = (BigInteger) number;
         return BYTE_MAX_VALUE.compareTo(numberBI) >= 0 && BYTE_MIN_VALUE.compareTo(numberBI) <= 0;
      }

      final BigDecimal bd = toBigDecimal(number);
      if (!isWhole(bd))
         return false;

      return BYTE_MAX_VALUE_BD.compareTo(bd) >= 0 && BYTE_MIN_VALUE_BD.compareTo(bd) <= 0;
   }

   public static boolean isInteger(final BigInteger number) {
      if (number == null)
         return false;

      return INTEGER_MAX_VALUE.compareTo(number) >= 0 && INTEGER_MIN_VALUE.compareTo(number) <= 0;
   }

   public static boolean isInteger(final long number) {
      return number <= Integer.MAX_VALUE && number >= Integer.MIN_VALUE;
   }

   public static boolean isInteger(final Number number) {
      if (number == null)
         return false;

      if (number instanceof Integer //
         || number instanceof Short //
         || number instanceof Byte //
         || number instanceof AtomicInteger //
         || number instanceof MutableInt //
         || number instanceof MutableShort //
         || number instanceof MutableByte //
      )
         return true;

      if (number instanceof AtomicLong //
         || number instanceof LongAdder //
         || number instanceof MutableLong) {
         final long longValue = number.longValue();
         return longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE;
      }

      if (number instanceof BigInteger) {
         final BigInteger numberBI = (BigInteger) number;
         return INTEGER_MAX_VALUE.compareTo(numberBI) >= 0 && INTEGER_MIN_VALUE.compareTo(numberBI) <= 0;
      }

      final BigDecimal bd = toBigDecimal(number);
      if (!isWhole(bd))
         return false;

      return INTEGER_MAX_VALUE_BD.compareTo(bd) >= 0 && INTEGER_MIN_VALUE_BD.compareTo(bd) <= 0;
   }

   public static boolean isLong(final Number number) {
      if (number == null)
         return false;

      if (number instanceof Long //
         || number instanceof Integer //
         || number instanceof Short //
         || number instanceof Byte //
         || number instanceof AtomicLong //
         || number instanceof AtomicInteger //
         || number instanceof LongAdder //
         || number instanceof MutableLong //
         || number instanceof MutableInt //
         || number instanceof MutableShort //
         || number instanceof MutableByte //
      )
         return true;

      if (number instanceof BigInteger) {
         final BigInteger numberBI = (BigInteger) number;
         return LONG_MAX_VALUE.compareTo(numberBI) >= 0 && LONG_MIN_VALUE.compareTo(numberBI) <= 0;
      }

      final BigDecimal bd = toBigDecimal(number);
      if (!isWhole(bd))
         return false;

      return LONG_MAX_VALUE_BD.compareTo(bd) >= 0 && LONG_MIN_VALUE_BD.compareTo(bd) <= 0;
   }

   public static boolean isShort(final Number number) {
      if (number == null)
         return false;

      if (number instanceof Short //
         || number instanceof Byte //
         || number instanceof MutableShort //
         || number instanceof MutableByte //
      )
         return true;

      if (number instanceof Integer //
         || number instanceof AtomicLong //
         || number instanceof LongAdder //
         || number instanceof AtomicInteger //
         || number instanceof MutableInt //
         || number instanceof MutableLong) {
         final long longValue = number.longValue();
         return longValue <= Short.MAX_VALUE && longValue >= Short.MIN_VALUE;
      }

      if (number instanceof BigInteger) {
         final BigInteger numberBI = (BigInteger) number;
         return SHORT_MAX_VALUE.compareTo(numberBI) >= 0 && SHORT_MIN_VALUE.compareTo(numberBI) <= 0;
      }

      final BigDecimal bd = toBigDecimal(number);
      if (!isWhole(bd))
         return false;

      return SHORT_MAX_VALUE_BD.compareTo(bd) >= 0 && SHORT_MIN_VALUE_BD.compareTo(bd) <= 0;
   }

   public static boolean isWhole(final Number number) {
      if (number == null)
         return false;

      if (number instanceof BigInteger //
         || number instanceof Long //
         || number instanceof Integer //
         || number instanceof Short //
         || number instanceof Byte //
         || number instanceof AtomicLong //
         || number instanceof AtomicInteger //
         || number instanceof LongAdder //
         || number instanceof MutableLong //
         || number instanceof MutableInt //
         || number instanceof MutableShort //
         || number instanceof MutableByte //
      )
         return true;

      final BigDecimal bd = toBigDecimal(number);
      return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
   }

   public static BigDecimal toBigDecimal(final Number number) {
      if (number == null)
         return null;

      final BigDecimal bd;
      if (number instanceof BigDecimal) {
         bd = (BigDecimal) number;
      } else if (number instanceof BigInteger) {
         bd = new BigDecimal((BigInteger) number);
      } else if (number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte) {
         bd = BigDecimal.valueOf(number.longValue());
      } else {
         bd = BigDecimal.valueOf(number.doubleValue());
      }
      return bd;
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
