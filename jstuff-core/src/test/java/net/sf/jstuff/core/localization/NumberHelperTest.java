/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
