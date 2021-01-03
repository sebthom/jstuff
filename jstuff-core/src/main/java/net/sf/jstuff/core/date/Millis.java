/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Millis {

   private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
   private static final long HOUR_IN_MILLIS = TimeUnit.HOURS.toMillis(1);
   private static final long MINUTE_IN_MILLIS = TimeUnit.MINUTES.toMillis(1);
   private static final long SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);

   public static long between(final Date date1, final Date date2) {
      return Math.abs(date2.getTime() - date1.getTime());
   }

   public static long between(final Temporal date1, final Temporal date2) {
      return Math.abs(date1.until(date2, ChronoUnit.MILLIS));
   }

   public static long fromDays(final int days) {
      return days * DAY_IN_MILLIS;
   }

   public static long fromDays(final int days, final int hours) {
      return days * DAY_IN_MILLIS + hours * HOUR_IN_MILLIS;
   }

   public static long fromDays(final int days, final int hours, final int minutes) {
      return days * DAY_IN_MILLIS + hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS;
   }

   public static long fromDays(final int days, final int hours, final int minutes, final int seconds) {
      return days * DAY_IN_MILLIS + hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS + seconds * SECOND_IN_MILLIS;
   }

   public static long fromHours(final int hours) {
      return hours * HOUR_IN_MILLIS;
   }

   public static long fromHours(final int hours, final int minutes) {
      return hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS;
   }

   public static long fromHours(final int hours, final int minutes, final int seconds) {
      return hours * HOUR_IN_MILLIS + minutes * MINUTE_IN_MILLIS + seconds * SECOND_IN_MILLIS;
   }

   public static long fromMinutes(final int minutes) {
      return minutes * MINUTE_IN_MILLIS;
   }

   public static long fromMinutes(final int minutes, final int seconds) {
      return minutes * MINUTE_IN_MILLIS + seconds * SECOND_IN_MILLIS;
   }

   public static long fromSeconds(final int seconds) {
      return seconds * SECOND_IN_MILLIS;
   }
}
