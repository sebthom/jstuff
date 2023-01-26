/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import java.math.BigInteger;

import net.sf.jstuff.core.validation.Args;

/**
 * Numerical systems calculator: https://www.calculand.com/unit-converter/zahlen.php
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumericalSystem {

   /**
    * Binary / Dual
    */
   public static final NumericalSystem BASE2 = new NumericalSystem("01");

   /**
    * Hexal / Senary
    */
   public static final NumericalSystem BASE6 = new NumericalSystem("012345");

   /**
    * Octal
    */
   public static final NumericalSystem BASE8 = new NumericalSystem("01234567");

   /**
    * Hexadecimal / Sedecimal
    */
   public static final NumericalSystem BASE16 = new NumericalSystem("0123456789abcdef");

   /**
    * Hexatrigesimal / Hexatridecimal
    */
   public static final NumericalSystem BASE36 = new NumericalSystem("0123456789abcdefghijklmnopqrstuvwxyz");

   /**
    * Duohexagesimal
    */
   public static final NumericalSystem BASE62 = new NumericalSystem("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

   private final String digits;
   private final char[] digitsArray;
   private final BigInteger radix;

   public NumericalSystem(final String digits) {
      Args.notNull("digits", digits);

      this.digits = digits;
      digitsArray = digits.toCharArray();
      radix = BigInteger.valueOf(digitsArray.length);
   }

   public BigInteger decodeAsBigInteger(final CharSequence encoded) {
      Args.notNull("encoded", encoded);

      if ("0".equals(encoded))
         return BigInteger.ZERO;

      final BigInteger base = radix;
      BigInteger val = BigInteger.ZERO;
      for (int i = 0, l = encoded.length(); i < l; i++) {
         val = val //
            .multiply(base) //
            .add(BigInteger.valueOf(digits.indexOf(encoded.charAt(i))));
      }
      return val;
   }

   public int decodeAsInt(final CharSequence encoded) {
      Args.notNull("encoded", encoded);

      if ("0".equals(encoded))
         return 0;

      final int base = radix.intValue();
      int val = 0;
      for (int i = 0, l = encoded.length(); i < l; i++) {
         val = val * base + digits.indexOf(encoded.charAt(i));
      }
      return val;
   }

   public long decodeAsLong(final CharSequence encoded) {
      Args.notNull("encoded", encoded);

      if ("0".equals(encoded))
         return 0;

      final int base = radix.intValue();
      long val = 0;
      for (int i = 0, l = encoded.length(); i < l; i++) {
         val = val * base + digits.indexOf(encoded.charAt(i));
      }
      return val;
   }

   public String encode(final BigInteger value) {
      Args.notNull("value", value);

      switch (value.compareTo(BigInteger.ZERO)) {
         case -1:
            throw new IllegalArgumentException("[value] negative numbers not supported.");
         case 0:
            return "0";
      }

      if (Numbers.isLong(value))
         return encode(value.longValue());

      final int base = digitsArray.length;
      final BigInteger baseBI = BigInteger.valueOf(base);

      BigInteger remainder = value;
      long remainderAsLong = -1;

      final var sb = new StringBuilder();

      while (true) {
         final int idx;
         if (remainderAsLong == -1) {
            idx = remainder.mod(baseBI).byteValue();
            remainder = remainder.divide(baseBI);
            final boolean remainderIsNowLong = remainder.compareTo(Numbers.LONG_MAX_VALUE) <= 0;
            if (remainderIsNowLong) {
               remainderAsLong = remainder.longValue();
            }
         } else {
            idx = (int) (remainderAsLong % base);
            remainderAsLong = remainderAsLong / base;
         }

         sb.append(digitsArray[idx]);

         if (remainderAsLong == 0) {
            break;
         }
      }
      return sb.reverse().toString();
   }

   public String encode(final int value) {
      Args.notNegative("value", value);

      if (value == 0)
         return "0";

      final int base = digitsArray.length;

      final var sb = new StringBuilder();
      int remainder = value;
      while (remainder > 0) {
         sb.append(digitsArray[remainder % base]);
         remainder = remainder / base;
      }
      return sb.reverse().toString();
   }

   public String encode(final long value) {
      Args.notNegative("value", value);

      if (value == 0)
         return "0";

      final int base = digitsArray.length;

      final var sb = new StringBuilder();
      long remainder = value;
      while (remainder > 0) {
         sb.append(digitsArray[(int) (remainder % base)]);
         remainder = remainder / base;
      }

      return sb.reverse().toString();
   }

   /**
    * non-optimized version of {@link #encode(BigInteger)}
    */
   String encode_slow(final BigInteger value) {
      Args.notNull("value", value);

      switch (value.compareTo(BigInteger.ZERO)) {
         case -1:
            throw new IllegalArgumentException("[value] negative numbers not supported.");
         case 0:
            return "0";
      }

      if (Numbers.isLong(value))
         return encode(value.longValue());

      final BigInteger base = BigInteger.valueOf(digitsArray.length);
      BigInteger remainder = value;
      final var sb = new StringBuilder();
      while (remainder.compareTo(BigInteger.ZERO) > 0) {
         sb.append(digitsArray[remainder.mod(base).intValue()]);
         remainder = remainder.divide(base);
      }
      return sb.reverse().toString();
   }

   public String getDigits() {
      return digits;
   }

   /**
    * Base of the numerical system
    */
   public int getRadix() {
      return digitsArray.length;
   }
}
