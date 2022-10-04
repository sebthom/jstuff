/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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

      switch (value.toLowerCase()) {
         case "1":
         case "true":
         case "t":
         case "yes":
         case "y":
            return TRUE;
         case "0":
         case "no":
         case "n":
         case "false":
         case "f":
            return FALSE;
         default:
            return UNKNOWN;
      }
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
      switch (this) {
         case TRUE:
            return FALSE;
         case FALSE:
            return TRUE;
         default:
            return UNKNOWN;
      }
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
      switch (this) {
         case TRUE:
            return Boolean.TRUE;
         case FALSE:
            return Boolean.FALSE;
         default:
            return null; // CHECKSTYLE:IGNORE ReturnNullInsteadOfBooleanCheck
      }
   }

   public boolean toBoolean(final boolean unknownValue) {
      switch (this) {
         case TRUE:
            return true;
         case FALSE:
            return false;
         default:
            return unknownValue;
      }
   }

   public byte toByte() {
      switch (this) {
         case TRUE:
            return 1;
         case FALSE:
            return 0;
         default:
            return -1;
      }
   }

   public byte toByte(final byte trueValue, final byte falseValue, final byte unknownValue) {
      switch (this) {
         case TRUE:
            return trueValue;
         case FALSE:
            return falseValue;
         default:
            return unknownValue;
      }
   }

   public int toInt(final int trueValue, final int falseValue, final int unknownValue) {
      switch (this) {
         case TRUE:
            return trueValue;
         case FALSE:
            return falseValue;
         default:
            return unknownValue;
      }
   }

   public long toLong(final long trueValue, final long falseValue, final long unknownValue) {
      switch (this) {
         case TRUE:
            return trueValue;
         case FALSE:
            return falseValue;
         default:
            return unknownValue;
      }
   }

   public <T> T toObject(final T trueValue, final T falseValue, final T unknownValue) {
      switch (this) {
         case TRUE:
            return trueValue;
         case FALSE:
            return falseValue;
         default:
            return unknownValue;
      }
   }

   @Override
   public String toString() {
      switch (this) {
         case TRUE:
            return "true";
         case FALSE:
            return "false";
         default:
            return "unknown";
      }
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
