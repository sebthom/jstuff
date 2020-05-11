/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.validation;

import static net.sf.jstuff.core.reflection.StackTrace.*;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Assert {

   private static IllegalStateException _createIllegalStateException(final String errorMessage) {
      return removeFirstStackTraceElement( //
         removeFirstStackTraceElement( //
            new IllegalStateException(errorMessage) //
         ) //
      );
   }

   private static IllegalStateException _createIllegalStateException(final String errorMessage, final Object... errorMessageArgs) {
      return removeFirstStackTraceElement( //
         removeFirstStackTraceElement( //
            new IllegalStateException(String.format(errorMessage, errorMessageArgs)) //
         ) //
      );
   }

   public static byte equals(final byte value, final byte invalidValue, final String errorMessage) {
      if (value != invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int equals(final int value, final byte invalidValue, final String errorMessage) {
      if (value != invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long equals(final long value, final byte invalidValue, final String errorMessage) {
      if (value != invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static Object equals(final Object value, final Object invalidValue, final String errorMessage) {
      if (!Objects.equals(value, invalidValue))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short equals(final short value, final byte invalidValue, final String errorMessage) {
      if (value != invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>true</code>
    */
   public static boolean isFalse(final boolean value, final String errorMessage) {
      if (value)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>true</code>
    */
   public static boolean isFalse(final boolean value, final String errorMessage, final Object... errorMessageArgs) {
      if (value)
         throw _createIllegalStateException(errorMessage, errorMessageArgs);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>true</code>
    */
   public static boolean isFalse(final boolean value, final Supplier<String> errorMessageSupplier) {
      Args.notNull("errorMessage", errorMessageSupplier);
      if (value)
         throw _createIllegalStateException(errorMessageSupplier.get());
      return value;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    *
    * @throws IllegalStateException if <code>value</code> is not readable
    */
   public static File isFileReadable(final File file) {
      Args.notNull("file", file);

      if (!file.exists())
         throw _createIllegalStateException("File [" + file.getAbsolutePath() + "] does not exist.");
      if (!file.isFile())
         throw _createIllegalStateException("Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canRead())
         throw _createIllegalStateException("File [" + file.getAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is not <code>null</code>
    */
   public static <T> T isInstanceOf(final T value, final Class<?> type, final String errorMessage) {
      Args.notNull("type", type);
      if (!type.isInstance(value))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is not <code>null</code>
    */
   public static <T> T isNull(final T value, final String errorMessage) {
      if (value != null)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>false</code>
    */
   public static boolean isTrue(final boolean value, final String errorMessage) {
      if (!value)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>false</code>
    */
   public static boolean isTrue(final boolean value, final String errorMessage, final Object... errorMessageArgs) {
      if (!value)
         throw _createIllegalStateException(errorMessage, errorMessageArgs);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>false</code>
    */
   public static boolean isTrue(final boolean value, final Supplier<String> errorMessageSupplier) {
      Args.notNull("errorMessage", errorMessageSupplier);
      if (!value)
         throw _createIllegalStateException(errorMessageSupplier.get());
      return value;
   }

   public static <S extends CharSequence> S matches(final S value, final Pattern pattern, final String errorMessage) {
      Args.notNull("pattern", pattern);

      if (value == null || !pattern.matcher(value).matches())
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static byte max(final byte value, final byte max, final String errorMessage) {
      if (value > max)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int max(final int value, final int max, final String errorMessage) {
      if (value > max)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long max(final long value, final long max, final String errorMessage) {
      if (value > max)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short max(final short value, final short max, final String errorMessage) {
      if (value > max)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static byte min(final byte value, final byte min, final String errorMessage) {
      if (value < min)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int min(final int value, final int min, final String errorMessage) {
      if (value < min)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long min(final long value, final long min, final String errorMessage) {
      if (value < min)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short min(final short value, final short min, final String errorMessage) {
      if (value < min)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <C extends Collection<?>> C noNulls(final C items, final String errorMessage) {
      if (items == null)
         return null;

      for (final Object item : items)
         if (item == null)
            throw _createIllegalStateException(errorMessage);
      return items;
   }

   public static <T> T[] noNulls(final T[] items, final String errorMessage) {
      if (items == null)
         return null;

      for (final T item : items)
         if (item == null)
            throw _createIllegalStateException(errorMessage);
      return items;
   }

   /**
    * @throws IllegalStateException if string <code>value</code> is null, has a length of 0, or only contains whitespace chars
    */
   public static <S extends CharSequence> S notBlank(final S value, final String errorMessage) {
      if (Strings.isBlank(value))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <A> A[] notEmpty(final A[] value, final String errorMessage) {
      if (value == null || value.length == 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> collection is null or empty
    */
   public static <C extends Collection<?>> C notEmpty(final C value, final String errorMessage) {
      if (value == null || value.isEmpty())
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> map is null or empty
    */
   public static <M extends Map<?, ?>> M notEmpty(final M value, final String errorMessage) {
      if (value == null || value.isEmpty())
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <S extends CharSequence> S notEmpty(final S value, final String errorMessage) {
      if (value == null || value.length() == 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static byte notEquals(final byte value, final byte invalidValue, final String errorMessage) {
      if (value == invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int notEquals(final int value, final byte invalidValue, final String errorMessage) {
      if (value == invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long notEquals(final long value, final byte invalidValue, final String errorMessage) {
      if (value == invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static Object notEquals(final Object value, final Object invalidValue, final String errorMessage) {
      if (Objects.equals(value, invalidValue))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short notEquals(final short value, final byte invalidValue, final String errorMessage) {
      if (value == invalidValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static byte notNegative(final byte value, final String errorMessage) {
      if (value < 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int notNegative(final int value, final String errorMessage) {
      if (value < 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long notNegative(final long value, final String errorMessage) {
      if (value < 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short notNegative(final short value, final String errorMessage) {
      if (value < 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>null</code>
    */
   public static <T> T notNull(final T value, final String errorMessage) {
      if (value == null)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>null</code>
    */
   public static <T> T notNull(final T value, final String errorMessage, final Object... errorMessageArgs) {
      if (value == null)
         throw _createIllegalStateException(errorMessage, errorMessageArgs);
      return value;
   }
}
