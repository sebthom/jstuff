/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.validation;

import static net.sf.jstuff.core.reflection.StackTrace.*;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Args {
    public static byte greaterThan(final String argumentName, final byte value, final byte bound) {
        if (value <= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + bound));
        return value;
    }

    public static int greaterThan(final String argumentName, final int value, final int bound) {
        if (value <= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + bound));
        return value;
    }

    public static long greaterThan(final String argumentName, final long value, final long bound) {
        if (value <= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + bound));
        return value;
    }

    public static int greaterThan(final String argumentName, final short value, final short bound) {
        if (value <= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be greater than " + bound));
        return value;
    }

    public static byte inRange(final String argumentName, final byte value, final byte min, final byte max) {
        if (value < min || value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
        return value;
    }

    public static int inRange(final String argumentName, final int value, final int min, final int max) {
        if (value < min || value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
        return value;
    }

    public static long inRange(final String argumentName, final long value, final long min, final long max) {
        if (value < min || value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
        return value;
    }

    public static <T extends Number> T inRange(final String argumentName, final T value, final long min, final long max) {
        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

        final long lValue = value.longValue();
        if (lValue < min || lValue > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be in range of " + min + " to " + max));
        return value;
    }

    public static File isFileReadable(final String argumentName, final File value) {
        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

        if (!value.exists())
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] File [" + value.getAbsolutePath() + "] does not exist."));
        if (!value.isFile())
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] Resource [" + value.getAbsolutePath()
                    + "] is not a file."));
        if (!value.canRead())
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] File [" + value.getAbsolutePath() + "] is not readable."));
        return value;
    }

    public static byte max(final String argumentName, final byte value, final byte max) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + max + " or smaller"));
        return value;
    }

    public static int max(final String argumentName, final int value, final int max) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + max + " or smaller"));
        return value;
    }

    public static long max(final String argumentName, final long value, final long max) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + max + " or smaller"));
        return value;
    }

    public static short max(final String argumentName, final short value, final short max) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + max + " or smaller"));
        return value;
    }

    public static byte min(final String argumentName, final byte value, final byte min) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
        return value;
    }

    public static int min(final String argumentName, final int value, final int min) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
        return value;
    }

    public static long min(final String argumentName, final long value, final long min) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
        return value;
    }

    public static short min(final String argumentName, final short value, final short min) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be " + min + " or greater"));
        return value;
    }

    public static <C extends Collection<?>> C noNulls(final String argumentName, final C items) {
        if (items == null)
            return null;

        for (final Object item : items)
            if (item == null)
                throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not contain elements with value <null>"));
        return items;
    }

    public static <T> T[] noNulls(final String argumentName, final T... items) {
        if (items == null)
            return null;

        for (final T item : items)
            if (item == null)
                throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not contain elements with value <null>"));
        return items;
    }

    /**
     * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
     */
    public static <A> A[] notEmpty(final String argumentName, final A[] value) {
        notNull("argumentName", argumentName);

        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
        if (value.length == 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
        return value;
    }

    /**
     * @throws IllegalArgumentException if <code>value</code> collection is null or empty
     */
    public static <C extends Collection<?>> C notEmpty(final String argumentName, final C value) {
        notNull("argumentName", argumentName);

        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
        if (value.isEmpty())
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
        return value;
    }

    /**
     * @throws IllegalArgumentException if <code>value</code> map is null or empty
     */
    public static <M extends Map<?, ?>> M notEmpty(final String argumentName, final M value) {
        notNull("argumentName", argumentName);

        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
        if (value.isEmpty())
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
        return value;
    }

    /**
     * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
     */
    public static <S extends CharSequence> S notEmpty(final String argumentName, final S value) {
        notNull("argumentName", argumentName);

        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));
        if (value.length() == 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be empty"));
        return value;
    }

    public static byte notNegative(final String argumentName, final byte value) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
        return value;
    }

    public static int notNegative(final String argumentName, final int value) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
        return value;
    }

    public static long notNegative(final String argumentName, final long value) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
        return value;
    }

    public static short notNegative(final String argumentName, final short value) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be negative"));
        return value;
    }

    /**
     * @throws IllegalArgumentException if <code>value</code> is null
     */
    public static <T> T notNull(final String argumentName, final T value) {
        if (argumentName == null)
            throw new IllegalArgumentException("[argumentName] must not be null");

        if (value == null)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must not be null"));

        return value;
    }

    public static byte smallerThan(final String argumentName, final byte value, final byte bound) {
        if (value >= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be smaller than " + bound));
        return value;
    }

    public static byte smallerThan(final String argumentName, final byte value, final short bound) {
        if (value >= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be smaller than " + bound));
        return value;
    }

    public static int smallerThan(final String argumentName, final int value, final int bound) {
        if (value >= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be smaller than " + bound));
        return value;
    }

    public static long smallerThan(final String argumentName, final long value, final long bound) {
        if (value >= bound)
            throw removeFirstStackTraceElement(new IllegalArgumentException("[" + argumentName + "] must be smaller than " + bound));
        return value;
    }
}
