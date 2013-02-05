/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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

import static net.sf.jstuff.core.StackTrace.*;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Assert
{
	public static <T> void containsNoNulls(final Collection<T> entries, final String errorMessage)
	{
		if (entries == null) return;

		for (final T entry : entries)
			if (entry == null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void containsNoNulls(final T[] entries, final String errorMessage)
	{
		if (entries == null) return;

		for (final T entry : entries)
			if (entry == null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
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
		Args.notNull("file", file);

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

	public static void minSize(final byte value, final byte min, final String errorMessage)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static void minSize(final int value, final int min, final String errorMessage)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static void minSize(final long value, final long min, final String errorMessage)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static void minSize(final short value, final short min, final String errorMessage)
	{
		if (value < min) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notEmpty(final CharSequence value, final String errorMessage)
	{
		if (value == null || value.length() == 0)
			throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</code> collection is null or empty
	 */
	public static <T> void notEmpty(final Collection<T> value, final String errorMessage)
	{
		if (value == null || value.isEmpty())
			throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	/**
	 * @throws IllegalStateException if <code>value</code> map is null or empty
	 */
	public static <K, V> void notEmpty(final Map<K, V> value, final String errorMessage)
	{
		if (value == null || value.isEmpty())
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

	public static <T> void notNegative(final byte value, final String errorMessage)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notNegative(final int value, final String errorMessage)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notNegative(final long value, final String errorMessage)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static <T> void notNegative(final short value, final String errorMessage)
	{
		if (value < 0) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
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

	protected Assert()
	{
		super();
	}
}
