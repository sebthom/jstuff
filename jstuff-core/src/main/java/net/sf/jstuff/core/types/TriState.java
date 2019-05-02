/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public enum TriState {

   TRUE,
   FALSE,
   UNKNOWN;

   public static TriState negate(final TriState state) {
      if (state == null)
         return UNKNOWN;

      switch (state) {
         case TRUE:
            return FALSE;
         case FALSE:
            return TRUE;
         default:
            return UNKNOWN;
      }
   }

   public static TriState valueOf(final boolean value) {
      return value ? TRUE : FALSE;
   }

   public static TriState valueOf(final Boolean value) {
      if (value == null)
         return UNKNOWN;
      return value.booleanValue() ? TRUE : FALSE;
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

   public Boolean toBoolean() {
      switch (this) {
         case TRUE:
            return Boolean.TRUE;
         case FALSE:
            return Boolean.FALSE;
         default:
            // CHECKSTYLE:IGNORE ReturnNullInsteadOfBooleanCheck FOR NEXT LINE
            return null;
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

   public Object toObject(final Object trueValue, final Object falseValue, final Object unknownValue) {
      switch (this) {
         case TRUE:
            return trueValue;
         case FALSE:
            return falseValue;
         default:
            return unknownValue;
      }
   }
}
