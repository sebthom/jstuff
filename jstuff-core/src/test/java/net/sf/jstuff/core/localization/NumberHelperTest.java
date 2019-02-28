/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.localization;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberHelperTest extends TestCase {
   public void testIsValidCurrency() {
      final NumberHelper numberHelper = new NumberHelper(Locale.GERMANY);

      assertTrue(numberHelper.isValidCurrency("10,00 €"));
      assertTrue(numberHelper.isValidCurrency("1000 €"));
      assertTrue(numberHelper.isValidCurrency("1.000,00 €"));
      assertTrue(numberHelper.isValidCurrency("1000.00 €"));

      assertFalse(numberHelper.isValidCurrency("10,00"));
      assertFalse(numberHelper.isValidCurrency("1000"));
      assertFalse(numberHelper.isValidCurrency("1.000,00"));
      assertFalse(numberHelper.isValidCurrency("1000.00"));

      assertFalse(numberHelper.isValidCurrency("10,00.00 €"));
      assertFalse(numberHelper.isValidCurrency(" €"));
   }
}
