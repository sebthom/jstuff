/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
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
public final class NullAnalysisHelper {

   /**
    * Casts non-null value marked as {@link Nullable} to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws AssertionError if the given value is null
    */
   @NonNull
   public static <T> T asNonNull(final T value) {
      if (value == null)
         throw new AssertionError("Cannot cast null to non-null value.");
      return value;
   }

   /**
    * Casts non-null value marked as {@link Nullable} to {@link NonNull}.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    * <p>
    * This method is not meant for non-null input validation.
    *
    * @throws AssertionError if the given value is null or contains nulls
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @NonNull T @NonNull [] asNonNull(final T... value) {
      if (value == null || ArrayUtils.containsNulls(value))
         throw new AssertionError("Array is null of contains null element!");
      return value;
   }

   /**
    * Casts non-null value marked as {@link Nullable} to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    */
   @SuppressWarnings("null")
   @NonNull
   public static <T> T asNonNullUnsafe(final T value) {
      return value;
   }

   /**
    * Casts non-null value marked as {@link Nullable} to {@link NonNull} without any validation.
    * <p>
    * Only use if you are sure the value is non-null but annotation-based null analysis was not able to determine it.
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @NonNull T @NonNull [] asNonNullUnsafe(final T... value) {
      return value;
   }

   /**
    * Casts a non-null value as {@link Nullable}.
    */

   public static <T> @Nullable T asNullable(final T value) {
      return value;
   }

   /**
    * Casts a non-null value as {@link Nullable}.
    */
   @SuppressWarnings("null")
   @SafeVarargs
   public static <T> @Nullable T @Nullable [] asNullable(final T... value) {
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

   @NonNull
   @SuppressWarnings("null")
   public static <T> T eventuallyNonNull() {
      return asNonNullUnsafe((T) null);
   }

   private NullAnalysisHelper() {
   }
}
