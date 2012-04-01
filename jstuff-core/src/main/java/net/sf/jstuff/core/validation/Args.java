/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.core.validation;

import static net.sf.jstuff.core.StackTraceUtils.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Args
{
	public static <T> void containsNoNulls(final String argumentName, final Collection<T> entries)
	{
		if (entries == null) return;

		for (final T entry : entries)
			if (entry == null)
				throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
						+ "] must not contain elements with value <null>"));
	}

	public static <T> void containsNoNulls(final String argumentName, final T... entries)
	{
		if (entries == null) return;

		for (final T entry : entries)
			if (entry == null)
				throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
						+ "] must not contain elements with value <null>"));
	}

	public static void inRange(final String argumentName, final byte value, final byte min, final byte max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void inRange(final String argumentName, final int value, final int min, final int max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void inRange(final String argumentName, final long value, final long min, final long max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void inRange(final String argumentName, final Number value, final long min, final long max)
	{
		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

		final long lValue = value.longValue();
		if (lValue < min || lValue > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void minSize(final String argumentName, final byte value, final byte min)
	{
		if (value < min)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min
					+ " or greater"));
	}

	public static void minSize(final String argumentName, final int value, final int min)
	{
		if (value < min)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min
					+ " or greater"));
	}

	public static void minSize(final String argumentName, final long value, final long min)
	{
		if (value < min)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min
					+ " or greater"));
	}

	public static void minSize(final String argumentName, final short value, final short min)
	{
		if (value < min)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min
					+ " or greater"));
	}

	/**
	 * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
	 */
	public static <T> void notEmpty(final String argumentName, final CharSequence value)
	{
		notNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length() == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> collection is null or empty
	 */
	public static <T> void notEmpty(final String argumentName, final Collection<T> value)
	{
		notNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty())
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> map is null or empty
	 */
	public static <K, V> void notEmpty(final String argumentName, final Map<K, V> value)
	{
		notNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty())
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
	 */
	public static <T> void notEmpty(final String argumentName, final T[] value)
	{
		notNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	public static <T> void notNegative(final String argumentName, final byte value)
	{
		if (value < 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must not be negative"));
	}

	public static <T> void notNegative(final String argumentName, final int value)
	{
		if (value < 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must not be negative"));
	}

	public static <T> void notNegative(final String argumentName, final long value)
	{
		if (value < 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must not be negative"));
	}

	public static <T> void notNegative(final String argumentName, final short value)
	{
		if (value < 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must not be negative"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> is null
	 */
	public static void notNull(final String argumentName, final Object value)
	{
		if (argumentName == null) throw new IllegalArgumentException("[argumentName] must not be null");

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
	}

	protected Args()
	{
		super();
	}
}
