/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Assert {
    /**
     * @throws IllegalStateException if <code>value</value> is <code>true</code>
     */
    public static boolean isFalse(final boolean value, final String errorMessage) {
        if (value)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is <code>true</code>
     */
    public static boolean isFalse(final boolean value, final String errorMessage, final Object... errorMessageArgs) {
        if (value)
            throw removeFirstStackTraceElement(new IllegalStateException(String.format(errorMessage, errorMessageArgs)));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is not readable
     */
    public static File isFileReadable(final File file) {
        Args.notNull("file", file);

        if (!file.exists())
            throw removeFirstStackTraceElement(new IllegalStateException("File [" + file.getAbsolutePath() + "] does not exist."));
        if (!file.isFile())
            throw removeFirstStackTraceElement(new IllegalStateException("Resource [" + file.getAbsolutePath() + "] is not a file."));
        if (!file.canRead())
            throw removeFirstStackTraceElement(new IllegalStateException("File [" + file.getAbsolutePath() + "] is not readable."));
        return file;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is not <code>null</code>
     */
    public static <T> T isNull(final T value, final String errorMessage) {
        if (value != null)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is <code>false</code>
     */
    public static boolean isTrue(final boolean value, final String errorMessage) {
        if (!value)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is <code>false</code>
     */
    public static boolean isTrue(final boolean value, final String errorMessage, final Object... errorMessageArgs) {
        if (!value)
            throw removeFirstStackTraceElement(new IllegalStateException(String.format(errorMessage, errorMessageArgs)));
        return value;
    }

    public static byte max(final byte value, final byte max, final String errorMessage) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static int max(final int value, final int max, final String errorMessage) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static long max(final long value, final long max, final String errorMessage) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static short max(final short value, final short max, final String errorMessage) {
        if (value > max)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static byte min(final byte value, final byte min, final String errorMessage) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static int min(final int value, final int min, final String errorMessage) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static long min(final long value, final long min, final String errorMessage) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static short min(final short value, final short min, final String errorMessage) {
        if (value < min)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static <C extends Collection<?>> C noNulls(final C items, final String errorMessage) {
        if (items == null)
            return null;

        for (final Object item : items)
            if (item == null)
                throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return items;
    }

    public static <T> T[] noNulls(final T[] items, final String errorMessage) {
        if (items == null)
            return null;

        for (final T item : items)
            if (item == null)
                throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return items;
    }

    public static <A> A[] notEmpty(final A[] value, final String errorMessage) {
        if (value == null || value.length == 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</code> collection is null or empty
     */
    public static <C extends Collection<?>> C notEmpty(final C value, final String errorMessage) {
        if (value == null || value.isEmpty())
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</code> map is null or empty
     */
    public static <M extends Map<?, ?>> M notEmpty(final M value, final String errorMessage) {
        if (value == null || value.isEmpty())
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static <S extends CharSequence> S notEmpty(final S value, final String errorMessage) {
        if (value == null || value.length() == 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static byte notNegative(final byte value, final String errorMessage) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static int notNegative(final int value, final String errorMessage) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static long notNegative(final long value, final String errorMessage) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    public static short notNegative(final short value, final String errorMessage) {
        if (value < 0)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is <code>null</code>
     */
    public static <T> T notNull(final T value, final String errorMessage) {
        if (value == null)
            throw removeFirstStackTraceElement(new IllegalStateException(errorMessage));
        return value;
    }

    /**
     * @throws IllegalStateException if <code>value</value> is <code>null</code>
     */
    public static <T> T notNull(final T value, final String errorMessage, final Object... errorMessageArgs) {
        if (value == null)
            throw removeFirstStackTraceElement(new IllegalStateException(String.format(errorMessage, errorMessageArgs)));
        return value;
    }
}
