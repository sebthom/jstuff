/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.localization;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberHelperTest {

   @Test
   public void testIsValidCurrency() {
      final NumberHelper numberHelper = new NumberHelper(Locale.GERMANY);

      assertThat(numberHelper.isValidCurrency("10,00 €")).isTrue();
      assertThat(numberHelper.isValidCurrency("1000 €")).isTrue();
      assertThat(numberHelper.isValidCurrency("1000,00 €")).isTrue();
      assertThat(numberHelper.isValidCurrency("1.000,00 €")).isTrue();
      assertThat(numberHelper.isValidCurrency("1000.00 €")).isTrue();

      assertThat(numberHelper.isValidCurrency("10,00")).isFalse();
      assertThat(numberHelper.isValidCurrency("1000")).isFalse();
      assertThat(numberHelper.isValidCurrency("1000,00")).isFalse();
      assertThat(numberHelper.isValidCurrency("1.000,00")).isFalse();
      assertThat(numberHelper.isValidCurrency("1000.00")).isFalse();

      assertThat(numberHelper.isValidCurrency("10,00.00 €")).isFalse();
      assertThat(numberHelper.isValidCurrency(" €")).isFalse();
   }
}
