/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Rounding implements Serializable {

   private static final long serialVersionUID = 1L;

   private transient BigDecimal divider;
   public final int roundAt;
   public final RoundingMode roundingMode;

   public Rounding(final int roundAt, final RoundingMode roundingMode) {
      Args.notNull("roundingMode", roundingMode);

      this.roundAt = roundAt;
      this.roundingMode = roundingMode;
      initDivider();
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final Rounding other = (Rounding) obj;
      if (roundAt != other.roundAt)
         return false;
      if (roundingMode != other.roundingMode)
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + roundAt;
      result = prime * result + (roundingMode == null ? 0 : roundingMode.hashCode());
      return result;
   }

   private void initDivider() {
      if (roundAt < 0) {
         divider = new BigDecimal(10).pow(-1 * roundAt);
      }
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      initDivider();
   }

   public BigDecimal round(final Number value) {
      Args.notNull("value", value);

      final BigDecimal bd = Numbers.toBigDecimal(value);
      if (roundAt < 0)
         return bd.divide(divider).setScale(0, roundingMode).multiply(divider);
      return bd.setScale(roundAt, roundingMode).stripTrailingZeros();
   }

   public BigInteger round(final BigInteger value) {
      Args.notNull("value", value);

      return round(new BigDecimal(value)).toBigIntegerExact();
   }

   public double round(final double value) {
      return round(BigDecimal.valueOf(value)).doubleValue();
   }

   public long round(final long value) {
      return round(BigDecimal.valueOf(value)).longValueExact();
   }
}
