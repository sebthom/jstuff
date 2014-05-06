/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import static net.sf.jstuff.core.reflection.StackTrace.*;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Args
{
	public static <C extends Collection< ? >> C containsNoNulls(final String argumentName, final C entries)
	{
		if (entries == null) return null;

		for (final Object entry : entries)
			if (entry == null)
				throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not contain elements with value <null>"));
		return entries;
	}

	public static <T> T[] containsNoNulls(final String argumentName, final T... entries)
	{
		if (entries == null) return null;

		for (final T entry : entries)
			if (entry == null)
				throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not contain elements with value <null>"));
		return entries;
	}

	public static byte greaterThan(final String argumentName, final byte value, final byte min)
	{
		if (value <= min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + min));
		return value;
	}

	public static int greaterThan(final String argumentName, final int value, final int min)
	{
		if (value <= min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + min));
		return value;
	}

	public static long greaterThan(final String argumentName, final long value, final long min)
	{
		if (value <= min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + min));
		return value;
	}

	public static byte inRange(final String argumentName, final byte value, final byte min, final byte max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
		return value;
	}

	public static int inRange(final String argumentName, final int value, final int min, final int max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
		return value;
	}

	public static long inRange(final String argumentName, final long value, final long min, final long max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
		return value;
	}

	public static <T extends Number> T inRange(final String argumentName, final T value, final long min, final long max)
	{
		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

		final long lValue = value.longValue();
		if (lValue < min || lValue > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
		return value;
	}

	public static byte minSize(final String argumentName, final byte value, final byte min)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
		return value;
	}

	public static int minSize(final String argumentName, final int value, final int min)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
		return value;
	}

	public static long minSize(final String argumentName, final long value, final long min)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
		return value;
	}

	public static short minSize(final String argumentName, final short value, final short min)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
		return value;
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
	 */
	public static <A> A[] notEmpty(final String argumentName, final A[] value)
	{
		notNull("argumentName", argumentName);

		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length == 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
		return value;
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> collection is null or empty
	 */
	public static <C extends Collection< ? >> C notEmpty(final String argumentName, final C value)
	{
		notNull("argumentName", argumentName);

		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty()) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
		return value;
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> map is null or empty
	 */
	public static <M extends Map< ? , ? >> M notEmpty(final String argumentName, final M value)
	{
		notNull("argumentName", argumentName);

		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty()) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
		return value;
	}

	/**
	 * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
	 */
	public static <S extends CharSequence> S notEmpty(final String argumentName, final S value)
	{
		notNull("argumentName", argumentName);

		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length() == 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
		return value;
	}

	public static byte notNegative(final String argumentName, final byte value)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
		return value;
	}

	public static int notNegative(final String argumentName, final int value)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
		return value;
	}

	public static long notNegative(final String argumentName, final long value)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
		return value;
	}

	public static short notNegative(final String argumentName, final short value)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
		return value;
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> is null
	 */
	public static <T> T notNull(final String argumentName, final T value)
	{
		if (argumentName == null) throw new IllegalArgumentException("[argumentName] must not be null");

		if (value == null) throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

		return value;
	}
}
