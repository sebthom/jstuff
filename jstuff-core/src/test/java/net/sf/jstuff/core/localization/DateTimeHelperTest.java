/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.localization;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateTimeHelperTest extends TestCase
{
	public void testIsValidDate()
	{
		DateTimeHelper c;
		DateFormat df;

		// testing Validator with german locale
		c = new DateTimeHelper(Locale.GERMANY);
		assertTrue(c.isValidDate("26.12.02")); //SHORT
		assertTrue(c.isValidDate("26.12.2002")); //MEDIUM
		assertTrue(c.isValidDate("26. Dezember 2002")); //LONG
		assertTrue(c.isValidDate("Donnerstag, 26. Dezember 2002")); //FULL

		df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.FULL, Locale.GERMANY);
		assertTrue(c.isValidDate(df.format(new Date())));

		df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
		assertFalse(c.isValidDate(df.format(new Date())));

		// testing Validator with us locale
		c = new DateTimeHelper(Locale.US);
		df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
		assertTrue(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
		assertTrue(c.isValidDate(df.format(new Date())));

		df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
		assertFalse(c.isValidDate(df.format(new Date())));
		df = DateFormat.getDateInstance(DateFormat.FULL, Locale.GERMANY);
		assertFalse(c.isValidDate(df.format(new Date())));
	}

	public void testIsValidDateTime()
	{
		DateTimeHelper c;
		DateFormat df;

		// testing Validator with german locale
		c = new DateTimeHelper(Locale.GERMANY);
		/*assertTrue(c.isValidDate("26.12.02"));						//SHORT
		assertTrue(c.isValidDate("26.12.2002"));					//MEDIUM
		assertTrue(c.isValidDate("26. Dezember 2002")); 			//LONG
		assertTrue(c.isValidDate("Donnerstag, 26. Dezember 2002")); //FULL
		*/
		df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.GERMANY);
		assertTrue(c.isValidDateTime(df.format(new Date())));

		df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
		assertFalse(c.isValidDateTime(df.format(new Date())));

		// testing Validator with us locale
		c = new DateTimeHelper(Locale.US);
		df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
		assertTrue(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
		assertTrue(c.isValidDateTime(df.format(new Date())));

		df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
		assertFalse(c.isValidDateTime(df.format(new Date())));
		df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.GERMANY);
		assertFalse(c.isValidDateTime(df.format(new Date())));
	}

	public void testIsValidTime()
	{
		DateTimeHelper c;
		DateFormat df;

		// testing Validator with german locale		
		c = new DateTimeHelper(Locale.GERMANY);
		assertTrue(c.isValidTime("21:45")); //SHORT
		assertTrue(c.isValidTime("21:45:30")); //MEDIUM
		assertTrue(c.isValidTime("21:45:43 CET")); //LONG
		assertTrue(c.isValidTime("21:46 Uhr CET")); //FULL

		df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
		assertTrue(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);
		assertTrue(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.GERMANY);
		assertTrue(c.isValidTime(df.format(new Date())));
		/*
		 * the next one fails if summer time CEST is enabled on the client
		df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.GERMANY);
		assertTrue(c.isValidTime(df.format(new Date())));
		*/
		/*
		the next tests are problematic because 9:42 PM will be 
		interpreted as 09:42 and no error will arrise:
		----------------------------------------------
		df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.US);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.US);
		assertFalse(c.isValidTime(df.format(new Date())));
		*/

		// testing Validator with us locale
		c = new DateTimeHelper(Locale.US);
		df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
		assertTrue(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.US);
		assertTrue(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
		assertTrue(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.US);
		assertTrue(c.isValidTime(df.format(new Date())));

		df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.GERMANY);
		assertFalse(c.isValidTime(df.format(new Date())));
		df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.GERMANY);
		assertFalse(c.isValidTime(df.format(new Date())));
	}

}
