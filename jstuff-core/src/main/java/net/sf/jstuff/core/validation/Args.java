/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
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

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Args {

   private static IllegalArgumentException _createIllegalArgumentException(final String argumentName, final String message) {
      return removeFirstStackTraceElement( //
         removeFirstStackTraceElement( //
            new IllegalArgumentException("[" + argumentName + "] " + message) //
         ) //

      );
   }

   private static <T> T _notNull(final String argumentName, final T value) {
      if (value == null)
         throw removeFirstStackTraceElement( //
            removeFirstStackTraceElement( //
               new IllegalArgumentException("[" + argumentName + "] must not be null") //
            ) //
         );
      return value;
   }

   public static byte greaterThan(final String argumentName, final byte value, final byte bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound);
      return value;
   }

   public static int greaterThan(final String argumentName, final int value, final int bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound);
      return value;
   }

   public static long greaterThan(final String argumentName, final long value, final long bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound);
      return value;
   }

   public static int greaterThan(final String argumentName, final short value, final short bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound);
      return value;
   }

   public static byte inRange(final String argumentName, final byte value, final byte min, final byte max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max);
      return value;
   }

   public static int inRange(final String argumentName, final int value, final int min, final int max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max);
      return value;
   }

   public static long inRange(final String argumentName, final long value, final long min, final long max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max);
      return value;
   }

   public static <T extends Number> T inRange(final String argumentName, final T value, final long min, final long max) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      final long lValue = value.longValue();
      if (lValue < min || lValue > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max);
      return value;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    */
   public static File isFileReadable(final String argumentName, final File file) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, file);

      if (!file.exists())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] does not exist.");
      if (!file.isFile())
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canRead())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file either does not exists or points to a regular file and it's path is writeable by the current user.
    */
   public static File isFileWriteable(final String argumentName, final File file) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, file);

      if (file.exists() && !file.isFile())
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canWrite())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] is not readable.");
      return file;
   }

   public static byte max(final String argumentName, final byte value, final byte max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller");
      return value;
   }

   public static int max(final String argumentName, final int value, final int max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller");
      return value;
   }

   public static long max(final String argumentName, final long value, final long max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller");
      return value;
   }

   public static short max(final String argumentName, final short value, final short max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller");
      return value;
   }

   public static byte min(final String argumentName, final byte value, final byte min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater");
      return value;
   }

   public static int min(final String argumentName, final int value, final int min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater");
      return value;
   }

   public static long min(final String argumentName, final long value, final long min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater");
      return value;
   }

   public static short min(final String argumentName, final short value, final short min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater");
      return value;
   }

   public static <C extends Collection<?>> C noNulls(final String argumentName, final C items) {
      _notNull("argumentName", argumentName);

      if (items == null)
         return null;

      for (final Object item : items)
         if (item == null)
            throw _createIllegalArgumentException(argumentName, "must not contain elements with value <null>");
      return items;
   }

   public static <T> T[] noNulls(final String argumentName, final T... items) {
      _notNull("argumentName", argumentName);

      if (items == null)
         return null;

      for (final T item : items)
         if (item == null)
            throw _createIllegalArgumentException(argumentName, "must not contain elements with value <null>");
      return items;
   }

   /**
    * @throws IllegalArgumentException if string <code>value</code> is null, has a length of 0, or only contains whitespace chars
    */
   public static <S extends CharSequence> S notBlank(final String argumentName, final S value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.length() == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");

      if (Strings.isBlank(value))
         throw _createIllegalArgumentException(argumentName, "must not be blank");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
    */
   public static <A> A[] notEmpty(final String argumentName, final A[] value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.length == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> collection is null or empty
    */
   public static <C extends Collection<?>> C notEmpty(final String argumentName, final C value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.isEmpty())
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> map is null or empty
    */
   public static <M extends Map<?, ?>> M notEmpty(final String argumentName, final M value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.isEmpty())
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
    */
   public static <S extends CharSequence> S notEmpty(final String argumentName, final S value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.length() == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   public static byte notNegative(final String argumentName, final byte value) {
      _notNull("argumentName", argumentName);

      if (value < 0)
         throw _createIllegalArgumentException(argumentName, "must not be negative");
      return value;
   }

   public static int notNegative(final String argumentName, final int value) {
      _notNull("argumentName", argumentName);

      if (value < 0)
         throw _createIllegalArgumentException(argumentName, "must not be negative");
      return value;
   }

   public static long notNegative(final String argumentName, final long value) {
      _notNull("argumentName", argumentName);

      if (value < 0)
         throw _createIllegalArgumentException(argumentName, "must not be negative");
      return value;
   }

   public static short notNegative(final String argumentName, final short value) {
      _notNull("argumentName", argumentName);

      if (value < 0)
         throw _createIllegalArgumentException(argumentName, "must not be negative");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> is null
    */
   public static <T> T notNull(final String argumentName, final T value) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      return value;
   }

   public static byte smallerThan(final String argumentName, final byte value, final byte bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound);
      return value;
   }

   public static byte smallerThan(final String argumentName, final byte value, final short bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound);
      return value;
   }

   public static int smallerThan(final String argumentName, final int value, final int bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound);
      return value;
   }

   public static long smallerThan(final String argumentName, final long value, final long bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound);
      return value;
   }
}
