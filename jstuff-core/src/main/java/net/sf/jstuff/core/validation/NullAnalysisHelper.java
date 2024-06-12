/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.validation;

import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
@NonNullByDefault({})
public abstract class NullAnalysisHelper {

   /**
    * Casts a non-null value marked as {@link Nullable} to {@link NonNull}.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws IllegalStateException if the given value is null
    */

   public static <T> @NonNull T asNonNull(final T value) {
      if (value == null)
         throw new IllegalStateException("Null cannot be cast to non-null value!");
      return value;
   }

   /**
    * Casts a non-null value marked as {@link Nullable} to {@link NonNull}.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws IllegalStateException with the given <code>errorMessage</code> if the given value is null
    */

   public static <T> @NonNull T asNonNull(final T value, final String errorMessage) {
      if (value == null)
         throw new IllegalStateException(errorMessage);
      return value;
   }

   /**
    * Casts an array with non-null elements as {@link Nullable} to {@link NonNull}.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws IllegalStateException if the given value is null or contains nulls
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @NonNull T @NonNull [] asNonNull(final T... value) {
      if (value == null)
         throw new IllegalStateException("Null cannot be cast to non-null array!");
      if (ArrayUtils.containsNulls(value))
         throw new IllegalStateException("Array with null elements cannot be cast to array with non-null elements!");
      return value;
   }

   /**
    * Casts the given value to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    */
   @SuppressWarnings("null")
   public static <T> @NonNull T asNonNullUnsafe(final T value) {
      return value;
   }

   /**
    * Casts the elements of given array to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> @NonNull T @NonNull [] asNonNullUnsafe(final T... value) {
      return value;
   }

   /**
    * Casts the given non-null value as {@link Nullable}.
    */

   public static <T> @Nullable T asNullable(final T value) {
      return value;
   }

   /**
    * Casts the elements of the given array as {@link Nullable}.
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @Nullable T[] asNullableElements(final T... value) {
      return value;
   }

   public static <T> @NonNull T defaultIfNull(final T object, final @NonNull Supplier<@NonNull T> defaultValue) {
      if (object == null)
         return defaultValue.get();
      return object;
   }

   public static <T> @NonNull T defaultIfNull(final T object, final @NonNull T defaultValue) {
      if (object == null)
         return defaultValue;
      return object;
   }

   @SuppressWarnings("null")
   public static <T> @NonNull T lateNonNull() {
      return (@NonNull T) null;
   }

   /**
    * @deprecated use {@link #lateNonNull()}
    */
   @Deprecated

   @SuppressWarnings("null")
   public static <T> @NonNull T lazyNonNull() {
      return (@NonNull T) null;
   }

   protected NullAnalysisHelper() {
   }
}
