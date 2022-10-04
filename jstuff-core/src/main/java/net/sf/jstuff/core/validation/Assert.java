/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.validation;

import static net.sf.jstuff.core.reflection.StackTrace.*;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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

   public static byte equals(final byte value, final byte expectedValue, final String errorMessage) {
      if (value != expectedValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static int equals(final int value, final byte expectedValue, final String errorMessage) {
      if (value != expectedValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static long equals(final long value, final byte expectedValue, final String errorMessage) {
      if (value != expectedValue)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <T> T equals(final T value, final Object expectedValue, final String errorMessage) {
      if (!Objects.equals(value, expectedValue))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static short equals(final short value, final byte expectedValue, final String errorMessage) {
      if (value != expectedValue)
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
    * Ensures file exists, points to a directory and is readable by the current user.
    */
   public static Path isDirectoryReadable(@Nullable Path file) {
      file = Args.notNull("file", file);

      if (!Files.exists(file))
         throw _createIllegalStateException("File [" + file.toAbsolutePath() + "] does not exist.");
      if (!Files.isDirectory(file))
         throw _createIllegalStateException("Resource [" + file.toAbsolutePath() + "] is not a directory.");
      if (!Files.isReadable(file))
         throw _createIllegalStateException("File [" + file.toAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    *
    * @throws IllegalStateException if <code>file</code> is not readable
    */
   public static File isFileReadable(@Nullable File file) {
      file = Args.notNull("file", file);

      if (!file.exists())
         throw _createIllegalStateException("File [" + file.getAbsolutePath() + "] does not exist.");
      if (!file.isFile())
         throw _createIllegalStateException("Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canRead())
         throw _createIllegalStateException("File [" + file.getAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    *
    * @throws IllegalStateException if <code>file</code> is not readable
    */
   public static Path isFileReadable(@Nullable Path file) {
      file = Args.notNull("file", file);

      if (!Files.exists(file))
         throw _createIllegalStateException("File [" + file.toAbsolutePath() + "] does not exist.");
      if (!Files.isRegularFile(file))
         throw _createIllegalStateException("Resource [" + file.toAbsolutePath() + "] is not a regular file.");
      if (!Files.isReadable(file))
         throw _createIllegalStateException("File [" + file.toAbsolutePath() + "] is not readable.");
      return file;
   }

   /**
    * Ensures file either does not exists or points to a regular file and it's path is writable by the current user.
    *
    * @throws IllegalStateException if <code>file</code> exists or is not writable
    */
   public static File isFileWritable(@Nullable File file) {
      file = Args.notNull("file", file);

      if (file.exists() && !file.isFile())
         throw _createIllegalStateException("Resource [" + file.getAbsolutePath() + "] is not a regular file.");
      if (!file.canWrite())
         throw _createIllegalStateException("File [" + file.getAbsolutePath() + "] is not writable.");
      return file;
   }

   /**
    * Ensures file exists, points to a regular file and is readable by the current user.
    *
    * @throws IllegalStateException if <code>file</code> is not readable
    */
   public static Path isFileWritable(@Nullable Path file) {
      file = Args.notNull("file", file);

      if (Files.exists(file) && !Files.isRegularFile(file))
         throw _createIllegalStateException("Resource [" + file.toAbsolutePath() + "] is not a regular file.");
      if (!Files.isWritable(file))
         throw _createIllegalStateException("File [" + file.toAbsolutePath() + "] is not writable.");
      return file;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is not <code>null</code>
    */
   public static <T> @NonNull T isInstanceOf(final @Nullable T value, final Class<?> type, final String errorMessage) {
      Args.notNull("type", type);
      if (!type.isInstance(value))
         throw _createIllegalStateException(errorMessage);
      assert value != null;
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is not <code>null</code>
    */
   public static <T> @Nullable T isNull(final @Nullable T value, final String errorMessage) {
      if (value != null)
         throw _createIllegalStateException(errorMessage);
      return null;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is not <code>null</code>
    */
   public static <T> @Nullable T isNull(final @Nullable T value, final Supplier<String> errorMessageSupplier) {
      Args.notNull("errorMessageSupplier", errorMessageSupplier);
      if (value != null)
         throw _createIllegalStateException(errorMessageSupplier.get());
      return null;
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

   public static <S extends CharSequence> @NonNull S matches(final @Nullable S value, final Pattern pattern, final String errorMessage) {
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

   /**
    * @throws IllegalStateException if <code>items</code> is null or contains any null items
    */
   public static <C extends Collection<?>> @NonNull C noNulls(@Nullable C items, final String errorMessage) {
      items = Args.notNull("items", items);

      for (final Object item : items)
         if (item == null)
            throw _createIllegalStateException(errorMessage);
      return items;
   }

   /**
    * @throws IllegalStateException if <code>items</code> is null or contains any null items
    */
   public static <T> @NonNull T[] noNulls(T @Nullable [] items, final String errorMessage) {
      items = Args.notNull("items", items);

      for (final T item : items)
         if (item == null)
            throw _createIllegalStateException(errorMessage);
      return asNonNullUnsafe(items);
   }

   /**
    * @throws IllegalStateException if string <code>value</code> is null, has a length of 0, or only contains whitespace chars
    */
   public static <S extends CharSequence> @NonNull S notBlank(final @Nullable S value, final String errorMessage) {
      if (value == null || Strings.isBlank(value))
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static byte[] notEmpty(final byte @Nullable [] value, final String errorMessage) {
      if (value == null || value.length == 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <A> A[] notEmpty(final A @Nullable [] value, final String errorMessage) {
      if (value == null || value.length == 0)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> collection is null or empty
    */
   public static <C extends Collection<?>> @NonNull C notEmpty(final @Nullable C value, final String errorMessage) {
      if (value == null || value.isEmpty())
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> map is null or empty
    */
   public static <M extends Map<?, ?>> @NonNull M notEmpty(final @Nullable M value, final String errorMessage) {
      if (value == null || value.isEmpty())
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   public static <S extends CharSequence> @NonNull S notEmpty(final @Nullable S value, final String errorMessage) {
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

   public static <T> T notEquals(final T value, final Object invalidValue, final String errorMessage) {
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
   public static <T> @NonNull T notNull(final @Nullable T value, final String errorMessage) {
      if (value == null)
         throw _createIllegalStateException(errorMessage);
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>null</code>
    */
   public static <T> @NonNull T notNull(final @Nullable T value, final Supplier<String> errorMessageSupplier) {
      Args.notNull("errorMessageSupplier", errorMessageSupplier);
      if (value == null)
         throw _createIllegalStateException(errorMessageSupplier.get());
      return value;
   }

   /**
    * @throws IllegalStateException if <code>value</code> is <code>null</code>
    */
   public static <T> @NonNull T notNull(final @Nullable T value, final String errorMessage, final Object... errorMessageArgs) {
      if (value == null)
         throw _createIllegalStateException(errorMessage, errorMessageArgs);
      return value;
   }
}
