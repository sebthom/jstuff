/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.validation;

import static net.sf.jstuff.core.reflection.StackTrace.removeFirstStackTraceElement;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Args {

   private static IllegalArgumentException _createIllegalArgumentException(final String argumentName, final String message) {
      return removeFirstStackTraceElement( //
         removeFirstStackTraceElement( //
            new IllegalArgumentException("[" + argumentName + "] " + message) //
         ) //

      );
   }

   private static <T> @NonNull T _notNull(final String argumentName, final @Nullable T value) {
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
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound + " but is " + value);
      return value;
   }

   public static int greaterThan(final String argumentName, final int value, final int bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound + " but is " + value);
      return value;
   }

   public static long greaterThan(final String argumentName, final long value, final long bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound + " but is " + value);
      return value;
   }

   public static int greaterThan(final String argumentName, final short value, final short bound) {
      _notNull("argumentName", argumentName);

      if (value <= bound)
         throw _createIllegalArgumentException(argumentName, "must be greater than " + bound + " but is " + value);
      return value;
   }

   public static byte inRange(final String argumentName, final byte value, final byte min, final byte max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max + " but is " + value);
      return value;
   }

   public static int inRange(final String argumentName, final int value, final int min, final int max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max + " but is " + value);
      return value;
   }

   public static long inRange(final String argumentName, final long value, final long min, final long max) {
      _notNull("argumentName", argumentName);

      if (value < min || value > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max + " but is " + value);
      return value;
   }

   public static <T extends Number> T inRange(final String argumentName, final T value, final long min, final long max) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      final long lValue = value.longValue();
      if (lValue < min || lValue > max)
         throw _createIllegalArgumentException(argumentName, "must be in range of " + min + " to " + max + " but is " + value);
      return value;
   }

   /**
    * Ensures file exists, points to a directory and is readable by the current user.
    */
   public static Path isDirectoryReadable(final String argumentName, @Nullable Path file) {
      _notNull("argumentName", argumentName);
      file = _notNull(argumentName, file);

      if (!Files.exists(file))
         throw _createIllegalArgumentException(argumentName, "Directory [" + file.toAbsolutePath() + "] does not exist.");
      if (!Files.isDirectory(file))
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.toAbsolutePath() + "] is not a directory.");
      if (!Files.isReadable(file))
         throw _createIllegalArgumentException(argumentName, "File [" + file.toAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    */
   public static File isFileReadable(final String argumentName, @Nullable File file) {
      _notNull("argumentName", argumentName);
      file = _notNull(argumentName, file);

      if (!file.exists())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] does not exist.");
      if (!file.isFile())
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canRead())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    */
   public static Path isFileReadable(final String argumentName, @Nullable Path file) {
      _notNull("argumentName", argumentName);
      file = _notNull(argumentName, file);

      if (!Files.exists(file))
         throw _createIllegalArgumentException(argumentName, "File [" + file.toAbsolutePath() + "] does not exist.");
      if (!Files.isRegularFile(file))
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.toAbsolutePath() + "] is not a regular file.");
      if (!Files.isReadable(file))
         throw _createIllegalArgumentException(argumentName, "File [" + file.toAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file either does not exists or points to a regular file and it's path is writeable by the current user.
    */
   public static File isFileWriteable(final String argumentName, @Nullable File file) {
      _notNull("argumentName", argumentName);
      file = _notNull(argumentName, file);

      if (file.exists() && !file.isFile())
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canWrite())
         throw _createIllegalArgumentException(argumentName, "File [" + file.getAbsolutePath() + "] is not writable.");
      return file;
   }

   /**
    * Ensures file either does not exists or points to a regular file and it's path is writeable by the current user.
    */
   public static Path isFileWritable(final String argumentName, final Path file) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, file);

      if (Files.exists(file) && !Files.isRegularFile(file))
         throw _createIllegalArgumentException(argumentName, "Resource [" + file.toAbsolutePath() + "] is not a regular file.");
      if (!Files.isWritable(file))
         throw _createIllegalArgumentException(argumentName, "File [" + file.toAbsolutePath() + "] is not writable.");
      return file;
   }

   public static <S extends CharSequence> S matches(final String argumentName, final S value, final Pattern pattern) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);
      _notNull("pattern", pattern);

      if (!pattern.matcher(value).matches())
         throw _createIllegalArgumentException(argumentName, "must match pattern " + pattern);
      return value;
   }

   public static byte max(final String argumentName, final byte value, final byte max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller but is " + value);
      return value;
   }

   public static int max(final String argumentName, final int value, final int max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller but is " + value);
      return value;
   }

   public static long max(final String argumentName, final long value, final long max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller but is " + value);
      return value;
   }

   public static short max(final String argumentName, final short value, final short max) {
      _notNull("argumentName", argumentName);

      if (value > max)
         throw _createIllegalArgumentException(argumentName, "must be " + max + " or smaller but is " + value);
      return value;
   }

   public static <S extends CharSequence> S maxLength(final String argumentName, final S value, final int maxLength) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.length() > maxLength)
         throw _createIllegalArgumentException(argumentName, "must not exceed " + maxLength + " chars");
      return value;
   }

   public static byte min(final String argumentName, final byte value, final byte min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater but is " + value);
      return value;
   }

   public static int min(final String argumentName, final int value, final int min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater but is " + value);
      return value;
   }

   public static long min(final String argumentName, final long value, final long min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater but is " + value);
      return value;
   }

   public static short min(final String argumentName, final short value, final short min) {
      _notNull("argumentName", argumentName);

      if (value < min)
         throw _createIllegalArgumentException(argumentName, "must be " + min + " or greater but is " + value);
      return value;
   }

   public static <S extends CharSequence> S minLength(final String argumentName, final S value, final int minLength) {
      _notNull("argumentName", argumentName);
      _notNull(argumentName, value);

      if (value.length() < minLength)
         throw _createIllegalArgumentException(argumentName, "must not have at least " + minLength + " chars");
      return value;
   }

   public static <C extends Collection<?>> C noNulls(final String argumentName, @Nullable C items) {
      _notNull("argumentName", argumentName);
      items = _notNull(argumentName, items);

      for (final Object item : items)
         if (item == null)
            throw _createIllegalArgumentException(argumentName, "must not contain elements with value <null>");
      return items;
   }

   @SafeVarargs
   public static <T> @NonNull T[] noNulls(final String argumentName, T @Nullable... items) {
      _notNull("argumentName", argumentName);
      items = _notNull(argumentName, items);

      for (final Object item : items)
         if (item == null)
            throw _createIllegalArgumentException(argumentName, "must not contain elements with value <null>");
      return asNonNullUnsafe(items);
   }

   /**
    * @throws IllegalArgumentException if string <code>value</code> is null, has a length of 0, or only contains whitespace chars
    */
   public static <S extends CharSequence> @NonNull S notBlank(final String argumentName, @Nullable S value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.length() == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");

      if (Strings.isBlank(value))
         throw _createIllegalArgumentException(argumentName, "must not be blank");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
    */
   public static byte[] notEmpty(final String argumentName, byte @Nullable [] value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.length == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> array is null or has a length of 0
    */
   public static <A> A[] notEmpty(final String argumentName, A @Nullable [] value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.length == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> collection is null or empty
    */
   public static <C extends Collection<?>> @NonNull C notEmpty(final String argumentName, @Nullable C value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.isEmpty())
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if <code>value</code> map is null or empty
    */
   public static <M extends Map<?, ?>> @NonNull M notEmpty(final String argumentName, @Nullable M value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.isEmpty())
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   /**
    * @throws IllegalArgumentException if string <code>value</code> is null or has a length of 0
    */
   public static <S extends CharSequence> @NonNull S notEmpty(final String argumentName, @Nullable S value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);

      if (value.length() == 0)
         throw _createIllegalArgumentException(argumentName, "must not be empty");
      return value;
   }

   public static byte notEquals(final String argumentName, final byte value, final byte invalidValue) {
      _notNull("argumentName", argumentName);

      if (value == invalidValue)
         throw _createIllegalArgumentException(argumentName, "must not equal " + invalidValue);
      return value;
   }

   public static int notEquals(final String argumentName, final int value, final int invalidValue) {
      _notNull("argumentName", argumentName);

      if (value == invalidValue)
         throw _createIllegalArgumentException(argumentName, "must not equal " + invalidValue);
      return value;
   }

   public static long notEquals(final String argumentName, final long value, final long invalidValue) {
      _notNull("argumentName", argumentName);

      if (value == invalidValue)
         throw _createIllegalArgumentException(argumentName, "must not equal " + invalidValue);
      return value;
   }

   public static <T> T notEquals(final String argumentName, final T value, final Object invalidValue) {
      _notNull("argumentName", argumentName);

      if (Objects.equals(value, invalidValue))
         throw _createIllegalArgumentException(argumentName, "must not equal " + invalidValue);
      return value;
   }

   public static short notEquals(final String argumentName, final short value, final short invalidValue) {
      _notNull("argumentName", argumentName);

      if (value == invalidValue)
         throw _createIllegalArgumentException(argumentName, "must not equal " + invalidValue);
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
   public static <T> @NonNull T notNull(final String argumentName, @Nullable T value) {
      _notNull("argumentName", argumentName);
      value = _notNull(argumentName, value);
      return value;
   }

   public static byte smallerThan(final String argumentName, final byte value, final byte bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound + " but is " + value);
      return value;
   }

   public static byte smallerThan(final String argumentName, final byte value, final short bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound + " but is " + value);
      return value;
   }

   public static int smallerThan(final String argumentName, final int value, final int bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound + " but is " + value);
      return value;
   }

   public static long smallerThan(final String argumentName, final long value, final long bound) {
      _notNull("argumentName", argumentName);

      if (value >= bound)
         throw _createIllegalArgumentException(argumentName, "must be smaller than " + bound + " but is " + value);
      return value;
   }
}
