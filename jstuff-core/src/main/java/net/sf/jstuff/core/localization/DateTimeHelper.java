/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.localization;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateTimeHelper implements Serializable {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.create();

   private final Locale locale;

   public DateTimeHelper() {
      this(Locale.getDefault());
   }

   public DateTimeHelper(final Locale locale) {
      Args.notNull("locale", locale);

      this.locale = locale;
   }

   public String formatDate(final DateFormatStyle style, final Date date) {
      Args.notNull("style", style);
      Args.notNull("date", date);

      return getDateFormat(style).format(date);
   }

   public String formatDateFull(final Date date) {
      Args.notNull("date", date);

      return getDateFormat(DateFormatStyle.FULL).format(date);
   }

   public String formatDateLong(final Date date) {
      Args.notNull("date", date);

      return getDateFormat(DateFormatStyle.LONG).format(date);
   }

   public String formatDateMedium(final Date date) {
      Args.notNull("date", date);

      return getDateFormat(DateFormatStyle.MEDIUM).format(date);
   }

   public String formatDateShort(final Date date) {
      Args.notNull("date", date);

      return getDateFormat(DateFormatStyle.SHORT).format(date);
   }

   public String formatDateTime(final DateFormatStyle style, final Date date) {
      Args.notNull("style", style);

      return getDateTimeFormat(style).format(date);
   }

   public String formatDateTimeFull(final Date date) {
      Args.notNull("date", date);

      return getDateTimeFormat(DateFormatStyle.FULL).format(date);
   }

   public String formatDateTimeLong(final Date date) {
      Args.notNull("date", date);

      return getDateTimeFormat(DateFormatStyle.LONG).format(date);
   }

   public String formatDateTimeMedium(final Date date) {
      Args.notNull("date", date);

      return getDateTimeFormat(DateFormatStyle.MEDIUM).format(date);
   }

   public String formatDateTimeShort(final Date date) {
      Args.notNull("date", date);

      return getDateTimeFormat(DateFormatStyle.SHORT).format(date);
   }

   public String formatTime(final DateFormatStyle style, final Date date) {
      Args.notNull("style", style);
      Args.notNull("date", date);

      return getTimeFormat(style).format(date);
   }

   public String formatTimeFull(final Date date) {
      Args.notNull("date", date);

      return getTimeFormat(DateFormatStyle.FULL).format(date);
   }

   public String formatTimeLong(final Date date) {
      Args.notNull("date", date);

      return getTimeFormat(DateFormatStyle.LONG).format(date);
   }

   public String formatTimeMedium(final Date date) {
      Args.notNull("date", date);

      return getTimeFormat(DateFormatStyle.MEDIUM).format(date);
   }

   public String formatTimeShort(final Date date) {
      Args.notNull("date", date);

      return getTimeFormat(DateFormatStyle.SHORT).format(date);
   }

   public DateFormat getDateFormat(final DateFormatStyle style) {
      Args.notNull("style", style);

      return DateFormat.getDateInstance(style.style, locale);
   }

   public DateFormatStyle getDateStyle(final String date) {
      Args.notNull("date", date);

      if (isValidDate(DateFormatStyle.SHORT, date))
         return DateFormatStyle.SHORT;
      if (isValidDate(DateFormatStyle.MEDIUM, date))
         return DateFormatStyle.MEDIUM;
      if (isValidDate(DateFormatStyle.LONG, date))
         return DateFormatStyle.LONG;
      if (isValidDate(DateFormatStyle.FULL, date))
         return DateFormatStyle.FULL;
      return null;
   }

   public DateFormat getDateTimeFormat(final DateFormatStyle style) {
      Args.notNull("style", style);

      return DateFormat.getDateTimeInstance(style.style, style.style, locale);
   }

   public DateFormat getDateTimeFormat(final DateFormatStyle dateStyle, final DateFormatStyle timeStyle) {
      Args.notNull("dateStyle", dateStyle);
      Args.notNull("timeStyle", timeStyle);

      return DateFormat.getDateTimeInstance(dateStyle.style, timeStyle.style, locale);
   }

   public DateFormatStyle getDateTimeStyle(final String style) {
      Args.notNull("style", style);

      if (isValidDateTime(DateFormatStyle.SHORT, style))
         return DateFormatStyle.SHORT;
      if (isValidDateTime(DateFormatStyle.MEDIUM, style))
         return DateFormatStyle.MEDIUM;
      if (isValidDateTime(DateFormatStyle.LONG, style))
         return DateFormatStyle.LONG;
      if (isValidDateTime(DateFormatStyle.FULL, style))
         return DateFormatStyle.FULL;
      return null;
   }

   public Locale getLocale() {
      return locale;
   }

   public DateFormat getTimeFormat(final DateFormatStyle style) {
      Args.notNull("style", style);

      return DateFormat.getTimeInstance(style.style, locale);
   }

   public DateFormatStyle getTimeStyle(final String style) {
      Args.notNull("style", style);

      if (isValidTime(DateFormatStyle.SHORT, style))
         return DateFormatStyle.SHORT;
      if (isValidTime(DateFormatStyle.MEDIUM, style))
         return DateFormatStyle.MEDIUM;
      if (isValidTime(DateFormatStyle.LONG, style))
         return DateFormatStyle.LONG;
      if (isValidTime(DateFormatStyle.FULL, style))
         return DateFormatStyle.FULL;
      return null;
   }

   protected boolean isValidDate(final DateFormatStyle style, final String date) {
      Args.notNull("style", style);

      if (date == null || date.length() == 0)
         return false;

      final DateFormat df = getDateFormat(style);
      df.setLenient(false);

      try {
         df.parse(date);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidDate(final String date) {
      return date != null && (isValidDate(DateFormatStyle.SHORT, date) || isValidDate(DateFormatStyle.MEDIUM, date) || isValidDate(
         DateFormatStyle.LONG, date) || isValidDate(DateFormatStyle.FULL, date));
   }

   protected boolean isValidDateTime(final DateFormatStyle style, final String dateTime) {
      Args.notNull("style", style);

      if (dateTime == null || dateTime.length() == 0)
         return false;

      final DateFormat df = getDateTimeFormat(style);
      df.setLenient(false);

      try {
         df.parse(dateTime);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidDateTime(final String dateTime) {
      return Strings.isNotBlank(dateTime) //
         && (isValidDateTime(DateFormatStyle.SHORT, dateTime) //
            || isValidDateTime(DateFormatStyle.MEDIUM, dateTime) //
            || isValidDateTime(DateFormatStyle.LONG, dateTime) //
            || isValidDateTime(DateFormatStyle.FULL, dateTime));
   }

   protected boolean isValidTime(final DateFormatStyle style, final String time) {
      Args.notNull("style", style);

      if (time == null || time.isBlank())
         return false;

      final DateFormat df = getTimeFormat(style);
      df.setLenient(false);

      try {
         df.parse(time);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidTime(final String time) {
      return Strings.isNotBlank(time) //
         && (isValidTime(DateFormatStyle.SHORT, time) //
            || isValidTime(DateFormatStyle.MEDIUM, time) //
            || isValidTime(DateFormatStyle.LONG, time) //
            || isValidTime(DateFormatStyle.FULL, time));
   }

   /**
    * @param date String containing the date to convert
    */
   public Date parseDate(final String date) {
      if (Strings.isEmpty(date))
         return null;

      final DateFormat df;
      var dfs = getDateTimeStyle(date);
      if (dfs != null) {
         df = DateFormat.getDateInstance(dfs.style, locale);
      } else {
         dfs = getDateStyle(date);
         if (dfs != null) {
            df = DateFormat.getDateInstance(dfs.style, locale);
         } else {
            dfs = getTimeStyle(date);
            if (dfs != null) {
               df = DateFormat.getTimeInstance(dfs.style, locale);
            } else {
               df = DateFormat.getDateInstance();
            }
         }
      }

      df.setLenient(false);

      try {
         return df.parse(date);
      } catch (final ParseException ex) {
         LOG.debug("Parsing of date %s failed.", date, ex);
         return null;
      }
   }
}
