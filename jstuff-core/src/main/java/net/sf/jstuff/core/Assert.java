/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
	public static <T> void argumentNotEmpty(final String argumentName, final Collection<T> value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.size() == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	public static <K, V> void argumentNotEmpty(final String argumentName, final Map<K, V> value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.size() == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	public static <T> void argumentNotEmpty(final String argumentName, final String value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length() == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	public static <T> void argumentNotEmpty(final String argumentName, final T[] value)
	{
		argumentNotNull("argumentName", argumentName);

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
		if (value.length == 0)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
	}

	public static void argumentNotNull(final String argumentName, final Object value)
	{
		if (argumentName == null) throw new IllegalArgumentException("[argumentName] must not be null");

		if (value == null)
			throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
	}

	public static void isFalse(final boolean value, final String errorMessage)
	{
		if (value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

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

	public static void isNull(final Object value, final String errorMessage)
	{
		if (value != null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

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

	public static void notFalse(final boolean value, final String errorMessage)
	{
		if (!value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static void notNull(final Object value, final String errorMessage)
	{
		if (value == null) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	public static void notTrue(final boolean value, final String errorMessage)
	{
		if (value) throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
	}

	private Assert()
	{
		super();
	}
}
