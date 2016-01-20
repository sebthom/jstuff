/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.date;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.FastDateFormat;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Dates extends org.apache.commons.lang3.time.DateUtils {
    private static final Logger LOG = Logger.create();

    private static final FastDateFormat RFC3399_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final String DURATION_REGEX = "" + //
            "((\\d+)(d((ay)s?)?)+)?" + // 1d, 1day, 2days
            "\\s*" + //
            "((\\d+)(h((our)s?)?)+)?" + // 1h, 1hour, 2hours
            "\\s*" + //
            "((\\d+)(m(in)?)+)?" + // 1m, 1min
            "\\s*" + //
            "((\\d+)(s((ec)s?)?)+)?" + // 1s, 1sec, 2secs
            "\\s*" + //
            "((\\d+)(ms)+)?" // 1ms
            ;

    private static final Pattern DURATION_PATTERN = Pattern.compile("^" + DURATION_REGEX + "$");

    public static Date fromRFC3399(final String dateString) throws ParseException {
        if (dateString == null)
            return null;

        return (Date) RFC3399_FORMAT.parseObject(dateString);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Calculates the number of days between two days
     */
    public static int getDaysBetween(final Date lowDate, final Date highDate) {
        Args.notNull("lowDate", lowDate);
        Args.notNull("highDate", highDate);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(lowDate);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(highDate);

        boolean swapped = false;
        if (cal1.after(cal2)) {
            final Calendar swap = cal1;
            cal1 = cal2;
            cal2 = swap;
            swapped = true;
        }

        int days = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
        final int year2 = cal2.get(Calendar.YEAR);
        if (cal1.get(Calendar.YEAR) != year2)
            do {
            days += cal1.getActualMaximum(Calendar.DAY_OF_YEAR);
            cal1.add(Calendar.YEAR, 1);
        }
        while (cal1.get(Calendar.YEAR) != year2);

        return swapped ? -days : days;
    }

    /**
     * Returns the number of days of the given month
     * 
     * @param year
     * @param month January = 1
     */
    public static int getDaysOfMonth(final int year, final int month) {
        final Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static boolean isToday(final Date date) {
        return isSameDay(date, new Date());
    }

    /**
     *
     * @param year
     * @param month January = 1
     * @param day
     */
    public static boolean isValidDate(final int year, final int month, final int day) {
        try {
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setLenient(false);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DATE, day);
            cal.getTimeInMillis(); // throws Exception
        } catch (final Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * @param duration e.g. "5d 2h 4m 30s 500ms"
     * @return duration in milliseconds
     */
    public static long parseDuration(String duration) throws ParseException {
        LOG.entry(duration);

        duration = duration.trim().toLowerCase();
        final Matcher m = DURATION_PATTERN.matcher(duration);
        if (!m.find())
            throw new ParseException("Cannot parse duration " + duration, 0);
        long milliseconds = 0;

        if (LOG.isDebugEnabled())
            for (int i = 0; i <= m.groupCount(); i++)
            LOG.debug(i + "=" + m.group(i));

        final String days = m.group(2);
        final String hours = m.group(7);
        final String mins = m.group(12);
        final String secs = m.group(16);
        final String msecs = m.group(21);
        if (days != null)
            milliseconds += Integer.parseInt(days) * 1000 * 60 * 60 * 24;
        if (hours != null)
            milliseconds += Integer.parseInt(hours) * 1000 * 60 * 60;
        if (mins != null)
            milliseconds += Integer.parseInt(mins) * 1000 * 60;
        if (secs != null)
            milliseconds += Integer.parseInt(secs) * 1000;
        if (msecs != null)
            milliseconds += Integer.parseInt(msecs);
        return milliseconds;
    }

    public static String toRFC3399(final Date date) {
        if (date == null)
            return null;

        return RFC3399_FORMAT.format(date);
    }

    public static String toRFC3399_UTC(final Date date) {
        if (date == null)
            return null;

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        return RFC3399_FORMAT.format(c);
    }
}