/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import org.eclipse.jdt.annotation.Nullable;

/**
 * See https://en.wikipedia.org/wiki/Three-valued_logic
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum Trilean {

   TRUE,
   FALSE,
   UNKNOWN;

   public static Trilean of(final boolean value) {
      return value ? TRUE : FALSE;
   }

   public static Trilean of(final @Nullable Boolean value) {
      if (value == null)
         return UNKNOWN;
      return value ? TRUE : FALSE;
   }

   public static Trilean of(final @Nullable String value) {
      if (value == null)
         return UNKNOWN;

      return switch (value.toLowerCase()) {
         case "1", "true", "t", "yes", "y" -> TRUE;
         case "0", "no", "n", "false", "f" -> FALSE;
         default -> UNKNOWN;
      };
   }

   public Trilean and(@Nullable Trilean state) {
      if (state == null) {
         state = UNKNOWN;
      }
      if (this == TRUE && state == TRUE)
         return TRUE;
      if (this == FALSE || state == FALSE)
         return FALSE;
      return UNKNOWN;
   }

   public boolean isFalse() {
      return this == FALSE;
   }

   public boolean isKnown() {
      return this != UNKNOWN;
   }

   public boolean isTrue() {
      return this == TRUE;
   }

   public boolean isUnknown() {
      return this == UNKNOWN;
   }

   public Trilean negate() {
      return switch (this) {
         case TRUE -> FALSE;
         case FALSE -> TRUE;
         default -> UNKNOWN;
      };
   }

   public Trilean or(@Nullable Trilean state) {
      if (state == null) {
         state = UNKNOWN;
      }
      if (this == TRUE || state == TRUE)
         return TRUE;
      if (this == UNKNOWN || state == UNKNOWN)
         return UNKNOWN;
      return FALSE;
   }

   /**
    * @return null for {@link #UNKNOWN}
    */
   @Nullable
   public Boolean toBoolean() {
      return switch (this) {
         case TRUE -> Boolean.TRUE;
         case FALSE -> Boolean.FALSE;
         default -> null; // CHECKSTYLE:IGNORE ReturnNullInsteadOfBooleanCheck
      };
   }

   public boolean toBoolean(final boolean unknownValue) {
      return switch (this) {
         case TRUE -> true;
         case FALSE -> false;
         default -> unknownValue;
      };
   }

   public byte toByte() {
      return switch (this) {
         case TRUE -> 1;
         case FALSE -> 0;
         default -> -1;
      };
   }

   public byte toByte(final byte trueValue, final byte falseValue, final byte unknownValue) {
      return switch (this) {
         case TRUE -> trueValue;
         case FALSE -> falseValue;
         default -> unknownValue;
      };
   }

   public int toInt(final int trueValue, final int falseValue, final int unknownValue) {
      return switch (this) {
         case TRUE -> trueValue;
         case FALSE -> falseValue;
         default -> unknownValue;
      };
   }

   public long toLong(final long trueValue, final long falseValue, final long unknownValue) {
      return switch (this) {
         case TRUE -> trueValue;
         case FALSE -> falseValue;
         default -> unknownValue;
      };
   }

   public <T> T toObject(final T trueValue, final T falseValue, final T unknownValue) {
      return switch (this) {
         case TRUE -> trueValue;
         case FALSE -> falseValue;
         default -> unknownValue;
      };
   }

   @Override
   public String toString() {
      return switch (this) {
         case TRUE -> "true";
         case FALSE -> "false";
         default -> "unknown";
      };
   }

   public Trilean xor(@Nullable Trilean state) {
      if (state == null) {
         state = UNKNOWN;
      }
      if (this == TRUE && state == TRUE //
            || this == FALSE && state == FALSE)
         return FALSE;

      if (this == UNKNOWN || state == UNKNOWN)
         return UNKNOWN;

      return TRUE;
   }
}
