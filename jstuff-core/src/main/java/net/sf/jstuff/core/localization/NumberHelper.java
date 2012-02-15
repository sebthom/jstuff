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
package net.sf.jstuff.core.localization;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberHelper
{
	private final Locale locale;

	/**
	 * public constructor
	 * 
	 * locale will be set to Locale.getDefault();
	 */
	public NumberHelper()
	{
		this(Locale.getDefault());
	}

	/**
	 * public constructor
	 * 
	 * @param locale Locale to be used for all conversations
	 */
	public NumberHelper(final Locale locale)
	{
		Args.notNull("locale", locale);

		this.locale = locale;
	}

	public NumberFormat getCurrencyFormat(final int minDigits, final int maxDigits)
	{
		final DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
		df.setMinimumFractionDigits(minDigits);
		df.setMaximumFractionDigits(maxDigits);
		return df;
	}

	public String getCurrencyFormatted(final Number value, final int digits)
	{
		Args.notNull("value", value);

		return getCurrencyFormat(digits, digits).format(value);
	}

	public DecimalFormat getDecimalFormat(final int minDigits, final int maxDigits)
	{
		final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(locale);
		df.setMinimumFractionDigits(minDigits);
		df.setMaximumFractionDigits(maxDigits);
		return df;
	}

	public String getDecimalFormatted(final Number value, final int digits)
	{
		Args.notNull("value", value);

		return getDecimalFormat(digits, digits).format(value);
	}

	/**
	 * @param value
	 * @throws NumberFormatException if value is invalid
	 */
	public double getDoubleValue(final String value) throws NumberFormatException
	{
		Args.notNull("value", value);

		if (isValidCurrency(value)) try
		{
			return getCurrencyFormat(0, 0).parse(value).doubleValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}

		try
		{
			return getDecimalFormat(0, 0).parse(value).doubleValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}
	}

	/**
	 * @param value
	 * @return returns 0 if value is invalid
	 */
	public double getDoubleValueSafe(final String value)
	{
		if (value == null) return 0;

		try
		{
			return getDoubleValue(value);
		}
		catch (final NumberFormatException e)
		{
			return 0;
		}
	}

	/**
	 * @param value
	 * @throws NumberFormatException if value is invalid
	 */
	public int getIntValue(final String value) throws NumberFormatException
	{
		Args.notNull("value", value);

		if (isValidCurrency(value)) try
		{
			return getCurrencyFormat(0, 0).parse(value).intValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}
		try
		{
			return getWholeNumberFormat().parse(value).intValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}
	}

	/**
	 * @param value
	 * @return returns 0 if value is invalid
	 */
	public int getIntValueSafe(final String value)
	{
		if (value == null) return 0;

		try
		{
			return getIntValue(value);
		}
		catch (final NumberFormatException e)
		{
			return 0;
		}
	}

	/**
	 * Returns the locale.
	 * @return Locale
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * @param value
	 * @throws NumberFormatException if value is invalid
	 */
	public long getLongValue(final String value) throws NumberFormatException
	{
		Args.notNull("value", value);

		if (isValidCurrency(value)) try
		{
			return getCurrencyFormat(0, 0).parse(value).longValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}
		try
		{
			return getWholeNumberFormat().parse(value).longValue();
		}
		catch (final ParseException e)
		{
			throw new NumberFormatException(e.getMessage());
		}
	}

	/**
	 * @param value
	 * @return returns 0 if value is invalid
	 */
	public long getLongValueSafe(final String value)
	{
		if (value == null) return 0;

		try
		{
			return getLongValue(value);
		}
		catch (final NumberFormatException e)
		{
			return 0;
		}
	}

	public NumberFormat getPercentFormat(final int digits)
	{
		final DecimalFormat df = (DecimalFormat) NumberFormat.getPercentInstance(locale);
		df.setMinimumFractionDigits(digits);
		df.setMaximumFractionDigits(digits);
		return df;
	}

	public String getPercentFormatted(final Number value, final int digits)
	{
		Args.notNull("value", value);

		return getPercentFormat(digits).format(value);
	}

	public NumberFormat getWholeNumberFormat()
	{
		return NumberFormat.getIntegerInstance(locale);
	}

	public String getWholeNumberFormatted(final Number value)
	{
		Args.notNull("value", value);

		return getWholeNumberFormat().format(value);
	}

	public boolean isValidCurrency(final String value)
	{
		if (value == null) return false;

		try
		{
			getCurrencyFormat(0, 0).parse(value);
			return true;
		}
		catch (final ParseException e)
		{
			return false;
		}
	}

	public boolean isValidDecimal(final String value)
	{
		if (value == null) return false;

		try
		{
			getDecimalFormat(0, 0).parse(value);
			return true;
		}
		catch (final ParseException e)
		{
			return false;
		}
	}

	public boolean isValidPercent(final String value)
	{
		if (value == null) return false;

		final NumberFormat nf = NumberFormat.getPercentInstance(locale);

		try
		{
			nf.parse(value);
			return true;
		}
		catch (final ParseException e)
		{
			return false;
		}
	}

	public boolean isValidWholeNumber(final String value)
	{
		if (value == null) return false;

		try
		{
			getWholeNumberFormat().parse(value);
			return true;
		}
		catch (final ParseException e)
		{
			return false;
		}
	}
}
