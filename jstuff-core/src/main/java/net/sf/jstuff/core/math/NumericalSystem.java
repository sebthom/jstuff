/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.math;

import java.math.BigInteger;

import net.sf.jstuff.core.validation.Args;

/**
 * Numerical systems calculator: https://www.calculand.com/unit-converter/zahlen.php
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
      if (encoded == null)
         return null;

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
      if (value == null)
         return null;

      if (value.compareTo(BigInteger.ZERO) == 0)
         return "0";

      if (Numbers.isLong(value))
         return encode(value.longValue());

      final BigInteger base = BigInteger.valueOf(digitsArray.length);
      BigInteger remainder = value;
      final StringBuilder sb = new StringBuilder();
      while (remainder.compareTo(BigInteger.ZERO) > 0) {
         sb.append(digitsArray[remainder.mod(base).intValue()]);
         remainder = remainder.divide(base);
      }
      return sb.reverse().toString();
   }

   public String encode(final int value) {
      if (value == 0)
         return "0";

      final int base = digitsArray.length;

      final StringBuilder sb = new StringBuilder();
      int remainder = value;
      while (remainder > 0) {
         sb.append(digitsArray[remainder % base]);
         remainder = remainder / base;
      }
      return sb.reverse().toString();
   }

   public String encode(final long value) {
      if (value == 0)
         return "0";

      final int base = digitsArray.length;

      final StringBuilder sb = new StringBuilder();
      long remainder = value;
      while (remainder > 0) {
         sb.append(digitsArray[(int) (remainder % base)]);
         remainder = remainder / base;
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
