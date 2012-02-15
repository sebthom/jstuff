/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
	private static final FastDateFormat RFC3399_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public static Date fromRFC3399(final String dateString) throws ParseException
	{
		if (dateString == null) return null;

		return (Date) RFC3399_FORMAT.parseObject(dateString);
	}

	public static int getCurrentYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * Calculates the number of days between two days
	 */
	public static int getDaysBetween(final Date lowDate, final Date highDate)
	{
		Args.notNull("lowDate", lowDate);
		Args.notNull("highDate", highDate);

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(lowDate);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(highDate);

		boolean swapped = false;
		if (cal1.after(cal2))
		{
			final Calendar swap = cal1;
			cal1 = cal2;
			cal2 = swap;
			swapped = true;
		}

		int days = cal2.get(Calendar.DAY_OF_YEAR) - cal1.get(Calendar.DAY_OF_YEAR);
		final int year2 = cal2.get(Calendar.YEAR);
		if (cal1.get(Calendar.YEAR) != year2) do
		{
			days += cal1.getActualMaximum(Calendar.DAY_OF_YEAR);
			cal1.add(Calendar.YEAR, 1);
		}
		while (cal1.get(Calendar.YEAR) != year2);

		return swapped ? -days : days;
	}

	/**
	 * Returns the number of days of the given month
	 * @param year
	 * @param month January = 1
	 */
	public static int getDaysOfMonth(final int year, final int month)
	{
		final Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @param year
	 * @param month January = 1
	 * @param day
	 */
	public static boolean isValidDate(final int year, final int month, final int day)
	{
		try
		{
			final GregorianCalendar cal = new GregorianCalendar();
			cal.setLenient(false);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DATE, day);
			cal.getTimeInMillis(); // throws Exception
		}
		catch (final Exception ex)
		{
			return false;
		}
		return true;
	}

	public static String toRFC3399(final Date date)
	{
		if (date == null) return null;

		return RFC3399_FORMAT.format(date);
	}

	public static String toRFC3399_UTC(final Date date)
	{
		if (date == null) return null;

		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		return RFC3399_FORMAT.format(c);
	}

	protected DateUtils()
	{
		super();
	}
}