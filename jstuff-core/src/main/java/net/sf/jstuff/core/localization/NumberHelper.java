/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.localization;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberHelper implements Serializable {
   private static final long serialVersionUID = 1L;

   private final Locale locale;

   @Nullable
   private transient String currencyCode;
   @Nullable
   private transient String currencySymbol;

   /**
    * locale will be set to Locale.getDefault();
    */
   public NumberHelper() {
      this(Locale.getDefault());
   }

   public NumberHelper(final Locale locale) {
      Args.notNull("locale", locale);

      this.locale = locale;
   }

   @Nullable
   public String getCurrencyCode() {
      if (currencyCode == null) {
         final var currency = Currency.getInstance(locale);
         if (currency != null) {
            currencyCode = currency.getCurrencyCode();
         }
      }
      return currencyCode;
   }

   public NumberFormat getCurrencyFormat(final int minDigits, final int maxDigits) {
      final DecimalFormat df = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
      df.setMinimumFractionDigits(minDigits);
      df.setMaximumFractionDigits(maxDigits);
      return df;
   }

   public String getCurrencyFormatted(final Number value, final int digits) {
      Args.notNull("value", value);

      return getCurrencyFormat(digits, digits).format(value);
   }

   @Nullable
   public String getCurrencySymbol() {
      if (currencySymbol == null) {
         final var currency = Currency.getInstance(locale);
         if (currency != null) {
            currencySymbol = currency.getSymbol();
         }
      }
      return currencySymbol;
   }

   public DecimalFormat getDecimalFormat(final int minDigits, final int maxDigits) {
      final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(locale);
      df.setMinimumFractionDigits(minDigits);
      df.setMaximumFractionDigits(maxDigits);
      return df;
   }

   public String getDecimalFormatted(final Number value, final int digits) {
      Args.notNull("value", value);

      return getDecimalFormat(digits, digits).format(value);
   }

   /**
    * @throws NumberFormatException if value is invalid
    */
   public double getDoubleValue(final String value) throws NumberFormatException {
      Args.notNull("value", value);

      if (isValidCurrency(value)) {
         try {
            return getCurrencyFormat(0, 0).parse(value).doubleValue();
         } catch (final ParseException e) {
            throw new NumberFormatException(e.getMessage());
         }
      }

      try {
         return getDecimalFormat(0, 0).parse(value).doubleValue();
      } catch (final ParseException e) {
         throw new NumberFormatException(e.getMessage());
      }
   }

   /**
    * @return returns 0 if value is invalid
    */
   public double getDoubleValueSafe(final @Nullable String value) {
      if (value == null)
         return 0;

      try {
         return getDoubleValue(value);
      } catch (final NumberFormatException e) {
         return 0;
      }
   }

   /**
    * @throws NumberFormatException if value is invalid
    */
   public int getIntValue(final String value) throws NumberFormatException {
      Args.notNull("value", value);

      if (isValidCurrency(value)) {
         try {
            return getCurrencyFormat(0, 0).parse(value).intValue();
         } catch (final ParseException e) {
            throw new NumberFormatException(e.getMessage());
         }
      }
      try {
         return getWholeNumberFormat().parse(value).intValue();
      } catch (final ParseException e) {
         throw new NumberFormatException(e.getMessage());
      }
   }

   /**
    * @return returns 0 if value is invalid
    */
   public int getIntValueSafe(final @Nullable String value) {
      if (value == null)
         return 0;

      try {
         return getIntValue(value);
      } catch (final NumberFormatException e) {
         return 0;
      }
   }

   public Locale getLocale() {
      return locale;
   }

   /**
    * @throws NumberFormatException if value is invalid
    */
   public long getLongValue(final String value) throws NumberFormatException {
      Args.notNull("value", value);

      if (isValidCurrency(value)) {
         try {
            return getCurrencyFormat(0, 0).parse(value).longValue();
         } catch (final ParseException e) {
            throw new NumberFormatException(e.getMessage());
         }
      }
      try {
         return getWholeNumberFormat().parse(value).longValue();
      } catch (final ParseException e) {
         throw new NumberFormatException(e.getMessage());
      }
   }

   /**
    * @return returns 0 if value is invalid
    */
   public long getLongValueSafe(final @Nullable String value) {
      if (value == null)
         return 0;

      try {
         return getLongValue(value);
      } catch (final NumberFormatException e) {
         return 0;
      }
   }

   public NumberFormat getPercentFormat(final int digits) {
      final DecimalFormat df = (DecimalFormat) NumberFormat.getPercentInstance(locale);
      df.setMinimumFractionDigits(digits);
      df.setMaximumFractionDigits(digits);
      return df;
   }

   public String getPercentFormatted(final Number value, final int digits) {
      Args.notNull("value", value);

      return getPercentFormat(digits).format(value);
   }

   public NumberFormat getWholeNumberFormat() {
      return NumberFormat.getIntegerInstance(locale);
   }

   public String getWholeNumberFormatted(final Number value) {
      Args.notNull("value", value);

      return getWholeNumberFormat().format(value);
   }

   public boolean isValidCurrency(final @Nullable String value) {
      if (value == null)
         return false;

      try {
         if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            // https://stackoverflow.com/questions/54579307/numberformat-currency-parsing-failing-unparseable-number
            getCurrencyFormat(0, 0).parse(value.replace(' ', '\u00a0'));
         } else {
            getCurrencyFormat(0, 0).parse(value);
         }
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidDecimal(final @Nullable String value) {
      if (value == null)
         return false;

      try {
         getDecimalFormat(0, 0).parse(value);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidPercent(final @Nullable String value) {
      if (value == null)
         return false;

      final NumberFormat nf = NumberFormat.getPercentInstance(locale);

      try {
         nf.parse(value);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }

   public boolean isValidWholeNumber(final @Nullable String value) {
      if (value == null)
         return false;

      try {
         getWholeNumberFormat().parse(value);
         return true;
      } catch (final ParseException e) {
         return false;
      }
   }
}
