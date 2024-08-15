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
    * @return the input value cast to {@link NonNull}
    * @throws IllegalStateException if the given value is {@code null}
    */
   public static <T> @NonNull T asNonNull(final @Nullable T value) {
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
    * @return the input value cast to {@link NonNull}
    * @throws IllegalStateException with the given <code>errorMessage</code> if the given value is {@code null}
    */
   public static <T> @NonNull T asNonNull(final @Nullable T value, final String errorMessage) {
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
    * @return the input value cast to {@link NonNull}
    * @throws IllegalStateException if the given value is {@code null} or contains {@code null}s
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @NonNull T @NonNull [] asNonNull(final T @Nullable... value) {
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
    *
    * @return the input value cast to {@link NonNull}
    */
   @SuppressWarnings("null")
   public static <T> @NonNull T asNonNullUnsafe(final T value) {
      return value;
   }

   /**
    * Casts the elements of given array to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    *
    * @return the input array cast to {@link NonNull}
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> @NonNull T @NonNull [] asNonNullUnsafe(final T... value) {
      return value;
   }

   /**
    * Casts the given non-null value as {@link Nullable}.
    *
    * @return the input value cast to {@link Nullable}
    */
   public static <T> @Nullable T asNullable(final T value) {
      return value;
   }

   /**
    * Casts the elements of the given array as {@link Nullable}.
    *
    * @return the input array cast to {@link Nullable}
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @Nullable T[] asNullableElements(final T... value) {
      return value;
   }

   public static <T> @NonNull T defaultIfNull(final @Nullable T object, final @NonNull Supplier<@NonNull T> defaultValue) {
      if (object == null)
         return defaultValue.get();
      return object;
   }

   public static <T> @NonNull T defaultIfNull(final @Nullable T object, final @NonNull T defaultValue) {
      if (object == null)
         return defaultValue;
      return object;
   }

   /**
    * Allows the temporary assignment of {@code null} to a {@code @NonNull} field during declaration,
    * deferring proper initialization until a later point when the actual non-null value is available.
    *
    * <p>
    * This method is useful when a field must be initialized later but cannot be left unassigned at the
    * point of declaration (e.g. when a value is provided by a later setup step).
    *
    * <p>
    * <strong>Note:</strong> The field must be assigned a non-null value before it is accessed to
    * prevent {@link NullPointerException}s.
    *
    * <p>
    * This method is functionally identical to {@link #sneakyNull()}, but it is named differently
    * to clarify its intended use for deferred field initialization.
    */
   @SuppressWarnings("null")
   public static <T> @NonNull T lateNonNull() {
      return (@NonNull T) null;
   }

   /**
    * Returns {@code null} while pretending to be a {@code @NonNull} value. This can be used to pass
    * {@code null} values to methods or fields that enforce non-null constraints, bypassing the
    * compiler's non-null checks.
    *
    * <p>
    * <strong>Note:</strong> This method intentionally breaks the {@code @NonNull} contract.
    * Ensure that the receiving code can safely handle a {@code null} value to avoid
    * {@link NullPointerException}s.
    */
   @SuppressWarnings("null")
   public static <T> @NonNull T sneakyNull() {
      return (@NonNull T) null;
   }

   /**
    * @deprecated use {@link #lateNonNull()}
    */
   @Deprecated
   public static <T> @NonNull T lazyNonNull() {
      return lateNonNull();
   }

   protected NullAnalysisHelper() {
   }
}
