/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.math.Numbers;
import net.sf.jstuff.core.math.Rounding;
import net.sf.jstuff.core.validation.Args;

/**
 * https://en.wikipedia.org/wiki/Kibibyte
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum ByteUnit {

   BYTES("B", 0),
   KIBIBYTES("KiB", 1),
   MEBIBYTES("MiB", 2),
   GIBIBYTES("GiB", 3),
   TEBIBYTES("TiB", 4),
   PEBIBYTES("PiB", 5),
   EXBIBYTES("EiB", 6),
   ZEBIBYTES("ZiB", 7),
   YOBIBYTES("YiB", 8);

   private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);

   private static final Rounding ROUNDING_0_HALF_UP = new Rounding(0, RoundingMode.HALF_UP);
   private static final Rounding ROUNDING_0_UP = new Rounding(0, RoundingMode.UP);

   private final String symbol;
   private final BigDecimal toBytesMultiplierBD;
   private final BigInteger toBytesMultiplierBI;

   ByteUnit(final String symbol, final int exponent) {
      Args.notNull("symbol", symbol);

      this.symbol = symbol;
      toBytesMultiplierBI = BigInteger.valueOf(1024).pow(exponent);
      toBytesMultiplierBD = new BigDecimal(toBytesMultiplierBI);
   }

   /**
    * Converts the value in the given byte unit to this byte unit.
    */
   public BigDecimal of(final Number value, final ByteUnit sourceUnit, final Rounding rounding) {
      Args.notNull("value", value);
      Args.notNull("sourceUnit", sourceUnit);
      Args.notNull("rounding", rounding);

      final BigDecimal bd = Numbers.toBigDecimal(value);

      if (BigDecimal.ZERO.equals(bd))
         return BigDecimal.ZERO;

      if (this == sourceUnit)
         return bd;

      return bd //
         .multiply(sourceUnit.toBytesMultiplierBD) //
         .divide(toBytesMultiplierBD, rounding.roundAt > -1 ? rounding.roundAt : 0, rounding.roundingMode);
   }

   /**
    * Converts the value in the given byte unit to this byte unit.
    *
    * @throws ArithmeticException if result overflows long range
    */
   public long of(final long value, final ByteUnit sourceUnit) {
      Args.notNull("sourceUnit", sourceUnit);

      if (value == 0)
         return 0;

      if (this == sourceUnit)
         return value;

      return of(BigDecimal.valueOf(value), sourceUnit, ROUNDING_0_HALF_UP).longValueExact();
   }

   /**
    * Converts the value in the given byte unit to this byte unit.
    */
   public BigDecimal of(final long value, final ByteUnit sourceUnit, final Rounding rounding) {
      return of(BigDecimal.valueOf(value), sourceUnit, rounding);
   }

   public BigInteger toBytes(final Number value) {
      if (value instanceof BigInteger) {
         if (this == BYTES)
            return (BigInteger) value;
         return ((BigInteger) value).multiply(toBytesMultiplierBI);
      }

      final BigDecimal bd = Numbers.toBigDecimal(value);
      if (this == BYTES)
         return ROUNDING_0_UP.round(bd).toBigInteger();
      return ROUNDING_0_UP.round(bd.multiply(toBytesMultiplierBD)).toBigInteger();
   }

   public BigInteger toBytes(final long value) {
      if (this == BYTES)
         return BigInteger.valueOf(value);
      return toBytes(BigInteger.valueOf(value));
   }

   public String toHumanReadableString(final @Nullable Number value, final Rounding rounding) {
      return toHumanReadableString(value, rounding, Locale.getDefault());
   }

   public String toHumanReadableString(final @Nullable Number value, final Rounding rounding, final Locale locale) {
      if (value == null)
         return "<null> " + symbol;

      Args.notNull("rounding", rounding);
      Args.notNull("locale", locale);

      final BigDecimal bd = Numbers.toBigDecimal(value);

      if (BigDecimal.ZERO.equals(bd))
         return BYTES.toString(0);

      if (Numbers.isWhole(bd) && bd.abs().compareTo(THOUSAND) < 0)
         return toString(bd);

      /*
       * Using RoundingMode.HALF_UP instead of RoundingMode.HALF_EVEN which rounds 0.5 to 0 instead of 1.
       */
      final ByteUnit[] units = ByteUnit.values();
      final BigDecimal threshold = BigDecimal.valueOf(0.976);
      ByteUnit effectiveUnit = this;
      BigDecimal effectiveValue = bd;
      for (int i = units.length - 1; i >= 0; i--) {
         effectiveUnit = units[i];
         effectiveValue = effectiveUnit.of(bd, this, rounding.roundAt < 3 ? new Rounding(3, rounding.roundingMode) : rounding);
         if (effectiveValue.abs().compareTo(threshold) > 0) {
            break;
         }
      }

      return effectiveUnit.toString(rounding.round(effectiveValue), locale);
   }

   public String toHumanReadableString(final long value, final Rounding rounding) {
      return toHumanReadableString(value, rounding, Locale.getDefault());
   }

   public String toHumanReadableString(final long value, final Rounding rounding, final Locale locale) {
      if (value == 0)
         return BYTES.toString(0);

      if (Math.abs(value) < 1000)
         return toString(value);

      return toHumanReadableString(BigDecimal.valueOf(value), rounding, locale);
   }

   @Override
   public String toString() {
      return symbol;
   }

   public String toString(final @Nullable Number value) {
      return toString(value, Locale.getDefault());
   }

   public String toString(final @Nullable Number value, @Nullable Locale locale) {
      if (value == null)
         return "<null> " + symbol;

      if (locale == null) {
         locale = Locale.getDefault();
      }

      final BigDecimal bd = Numbers.toBigDecimal(value);

      final NumberFormat fmt = NumberFormat.getNumberInstance(locale);
      fmt.setMinimumFractionDigits(0);
      fmt.setMaximumFractionDigits(bd.scale() > -1 ? bd.scale() : 0);
      fmt.setGroupingUsed(true);
      fmt.setRoundingMode(RoundingMode.UNNECESSARY);
      return fmt.format(bd) + " " + symbol;
   }

   public String toString(final long value) {
      return toString(value, Locale.getDefault());
   }

   public String toString(final long value, final Locale locale) {
      Args.notNull("locale", locale);

      final NumberFormat fmt = NumberFormat.getNumberInstance(locale);
      fmt.setMinimumFractionDigits(0);
      fmt.setMaximumFractionDigits(0);
      fmt.setGroupingUsed(true);
      fmt.setRoundingMode(RoundingMode.UNNECESSARY);
      return fmt.format(value) + " " + symbol;
   }
}
