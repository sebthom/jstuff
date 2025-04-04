/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import java.util.Date;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ImmutableDate extends Date {
   private static final long serialVersionUID = 1L;

   public static ImmutableDate now() {
      return new ImmutableDate();
   }

   public static ImmutableDate of(final java.util.Date date) {
      return new ImmutableDate(date);
   }

   public ImmutableDate() {
      super(System.currentTimeMillis());
   }

   public ImmutableDate(final java.util.Date date) {
      super(date.getTime());
   }

   @Override
   public ImmutableDate clone() {
      return new ImmutableDate(this);
   }

   @Deprecated
   @Override
   public void setDate(final int date) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setHours(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setMinutes(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setMonth(final int month) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setSeconds(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setTime(final long date) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @Override
   public void setYear(final int year) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
