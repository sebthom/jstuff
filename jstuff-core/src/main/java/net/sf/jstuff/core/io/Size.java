/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import static net.sf.jstuff.core.io.ByteUnit.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.mutable.MutableByte;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.mutable.MutableShort;

import net.sf.jstuff.core.math.Rounding;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Size implements Serializable, Comparable<Size> {

   private static final long serialVersionUID = 1L;

   private static final Rounding ROUNDING_2_HALFUP = new Rounding(2, RoundingMode.HALF_UP);

   public static Size of(final long value, final ByteUnit unit) {
      return of(BigInteger.valueOf(value), unit);
   }

   public static Size of(final Number value, final ByteUnit unit) {
      Args.notNull("value", value);

      if (unit == BYTES) {
         if (value instanceof BigInteger)
            return new Size((BigInteger) value);

         if (value instanceof Long || //
            value instanceof Integer || //
            value instanceof Short || //
            value instanceof Byte || //
            value instanceof AtomicLong || //
            value instanceof AtomicInteger || //
            value instanceof LongAdder || //
            value instanceof MutableLong || //
            value instanceof MutableInt || //
            value instanceof MutableShort || //
            value instanceof MutableByte //
         )
            return new Size(BigInteger.valueOf(value.longValue()));
      }

      return new Size(unit.toBytes(value));
   }

   public static Size ofBytes(final long value) {
      return of(value, BYTES);
   }

   public static Size ofGiB(final long value) {
      return of(value, GIBIBYTES);
   }

   public static Size ofKiB(final long value) {
      return of(value, KIBIBYTES);
   }

   public static Size ofMiB(final long value) {
      return of(value, MEBIBYTES);
   }

   public static Size ofTiB(final long value) {
      return of(value, TEBIBYTES);
   }

   private final BigInteger bytes;

   private Size(final BigInteger bytes) {
      this.bytes = bytes;
   }

   @Override
   public int compareTo(final Size o) {
      return bytes.compareTo(o.bytes);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Size other = (Size) obj;
      if (!Objects.equals(bytes, other.bytes)) {
         return false;
      }
      return true;
   }

   public BigInteger getBytes() {
      return bytes;
   }

   public BigDecimal getGiB(final Rounding rounding) {
      return GIBIBYTES.of(bytes, BYTES, rounding);
   }

   public BigDecimal getKiB(final Rounding rounding) {
      return KIBIBYTES.of(bytes, BYTES, rounding);
   }

   public BigDecimal getMiB(final Rounding rounding) {
      return MEBIBYTES.of(bytes, BYTES, rounding);
   }

   public BigDecimal getTiB(final Rounding rounding) {
      return TEBIBYTES.of(bytes, BYTES, rounding);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (bytes == null ? 0 : bytes.hashCode());
      return result;
   }

   public String toHumanReadableString(final Rounding rounding) {
      return BYTES.toHumanReadableString(bytes, rounding);
   }

   public String toHumanReadableString(final Rounding rounding, final Locale locale) {
      return BYTES.toHumanReadableString(bytes, rounding, locale);
   }

   @Override
   public String toString() {
      return BYTES.toHumanReadableString(bytes, ROUNDING_2_HALFUP);
   }

   public String toString(final ByteUnit targetUnit, final Rounding rounding) {
      return targetUnit.toString(targetUnit.of(bytes, BYTES, rounding));
   }

   public String toString(final ByteUnit targetUnit, final Rounding rounding, final Locale locale) {
      return targetUnit.toString(targetUnit.of(bytes, BYTES, rounding), locale);
   }
}