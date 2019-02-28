/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import java.sql.Date;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ImmutableDate extends Date {
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

   @Override
   public void setDate(final int date) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setHours(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setMinutes(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setMonth(final int month) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setSeconds(final int i) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setTime(final long date) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setYear(final int year) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
