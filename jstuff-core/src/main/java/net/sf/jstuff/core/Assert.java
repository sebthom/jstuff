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
package net.sf.jstuff.core;

import static net.sf.jstuff.core.StackTraceUtils.removeFirstStackTraceElement;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Assert
{
	public static void argumentInRange(final String argumentName, final byte value, final byte min, final byte max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void argumentInRange(final String argumentName, final int value, final int min, final int max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	public static void argumentInRange(final String argumentName, final long value, final long min, final long max)
	{
		if (value < min || value > max)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName
					+ "] must be in range of " + min + " to " + max));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> collection is null or empty
	 */
	public static <T> void argumentNotEmpty(final String argumentName, final Collection<T> value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty())
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> map is null or empty
	 */
	public static <K, V> void argumentNotEmpty(final String argumentName, final Map<K, V> value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.isEmpty())
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
	 */
	public static <T> void argumentNotEmpty(final String argumentName, final String value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length() == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
	 */
	public static <T> void argumentNotEmpty(final String argumentName, final T[] value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	/**
	 * @throws IllegalArgumentException if <code>value</code> is null
	 */
	public static void argumentNotNull(final String argumentName, final Object value)
	{
		if (argumentName == null) throw new IllegalArgumentException("[argumentName] must not be null");

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is <code>true</code>
	 */
	public static void isFalse(final boolean value, final String errorMessage)
	{
		if (value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is not readable
	 */
	public static void isFileReadable(final File file)
	{
		argumentNotNull("file", file);

		if (!file.exists())
			throw removeFirstStackTraceElement(new IllegalStateException("File [" + file.getAbsolutePath()
					+ "] does not exist."));
		if (!file.isFile())
			throw removeFirstStackTraceElement(new IllegalStateException("System resource [" + file.getAbsolutePath()
					+ "] is not a file."));
		if (!file.canRead())
			throw removeFirstStackTraceElement(new IllegalStateException("File [" + file.getAbsolutePath()
					+ "] is not readable."));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is not <code>null</code>
	 */
	public static void isNull(final Object value, final String errorMessage)
	{
		if (value != null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is <code>false</code>
	 */
	public static void isTrue(final boolean value, final String errorMessage)
	{
		if (!value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notEmpty(final String value, final String errorMessage)
	{
		if (value == null || value.length() == 0)
			throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notEmpty(final T[] value, final String errorMessage)
	{
		if (value == null || value.length == 0)
			throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is <code>false</code>
	 */
	public static void notFalse(final boolean value, final String errorMessage)
	{
		if (!value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is <code>null</code>
	 */
	public static void notNull(final Object value, final String errorMessage)
	{
		if (value == null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</value> is <code>true</code>
	 */
	public static void notTrue(final boolean value, final String errorMessage)
	{
		if (value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	private Assert()
	{
		super();
	}
}
