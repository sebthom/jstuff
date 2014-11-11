/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c,b) 2005-2014 Sebastian
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
package net.sf.jstuff.core;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringUtilsTest extends TestCase
{

	public void testAnsiToHTML()
	{
		assertEquals(
				"<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>",
				StringUtils.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?").toString());

		assertEquals(
				"<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>",
				StringUtils.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?\u001B[0m").toString());

		assertEquals(0, StringUtils.ansiColorsToHTML("\u001B[0m\u001B[0m").length());
	}

	public void testContainsAny()
	{
		assertTrue(StringUtils.containsAny("abcdef", "abcdef"));
		assertTrue(StringUtils.containsAny("abcdef", "123", "bc"));
		assertFalse(StringUtils.containsAny("abcdef", "123", "456"));

		assertFalse(StringUtils.containsAny("abcdef", ""));
		assertFalse(StringUtils.containsAny("abcdef", (String) null));

		assertTrue(StringUtils.containsAny(CollectionUtils.newArrayList("abc", "def"), "de", "456"));
		assertFalse(StringUtils.containsAny(CollectionUtils.newArrayList("abc", "def"), "123", "456"));
	}

	public void testCountMatches()
	{
		assertTrue(StringUtils.countMatches("1234512345", "1", 0) == 2);
		assertTrue(StringUtils.countMatches("1234512345", "1", 1) == 1);
		assertTrue(StringUtils.countMatches("1234512345", "1", 9) == 0);
		assertTrue(StringUtils.countMatches("1234512345", "1", 100) == 0);
		assertTrue(StringUtils.countMatches("1234512345", "1", -100) == 0);
		assertTrue(StringUtils.countMatches(null, "1", 1) == 0);
		assertTrue(StringUtils.countMatches("1", null, 1) == 0);
	}

	/**
	 * test: replace(string, replacement, start) --> no length argument
	 */
	public void testReplace1()
	{
		String a;
		String b;

		a = "1234";
		b = "abcdefghijk";

		assertTrue(StringUtils.replace(a, 0, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, b).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, b).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, b).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, b).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, b).equals("abcdefghijk"));

		a = "1234";
		b = "a";

		assertTrue(StringUtils.replace(a, 0, b).equals("a"));
		assertTrue(StringUtils.replace(a, 1, b).equals("1a"));
		assertTrue(StringUtils.replace(a, 2, b).equals("12a"));
		assertTrue(StringUtils.replace(a, 3, b).equals("123a"));
		assertTrue(StringUtils.replace(a, 4, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, b).equals("123a"));
		assertTrue(StringUtils.replace(a, -2, b).equals("12a"));
		assertTrue(StringUtils.replace(a, -3, b).equals("1a"));
		assertTrue(StringUtils.replace(a, -4, b).equals("a"));
		assertTrue(StringUtils.replace(a, -5, b).equals("a"));
		assertTrue(StringUtils.replace(a, -1000000, b).equals("a"));

		a = "1234";
		b = "";

		assertTrue(StringUtils.replace(a, 0, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, b).equals("1"));
		assertTrue(StringUtils.replace(a, 2, b).equals("12"));
		assertTrue(StringUtils.replace(a, 3, b).equals("123"));
		assertTrue(StringUtils.replace(a, 4, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, b).equals("123"));
		assertTrue(StringUtils.replace(a, -2, b).equals("12"));
		assertTrue(StringUtils.replace(a, -3, b).equals("1"));
		assertTrue(StringUtils.replace(a, -4, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, b).equals(""));

		a = "";
		b = "";

		assertTrue(StringUtils.replace(a, 0, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, b).equals(""));

		a = "";
		b = "abcdefghijk";

		assertTrue(StringUtils.replace(a, 0, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, b).equals("abcdefghijk"));
	}

	/**
	 * test: replace(string, replacement, start, length) --> length = 0
	 */
	public void testReplace2()
	{
		String a;
		String b;
		int c;

		a = "1234";
		b = "abcdefghijk";
		c = 0;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, -100000, c, b).equals("abcdefghijk1234"));

		a = "1234";
		b = "a";
		c = 0;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1a234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12a34"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12a34"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1a234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("a1234"));

		a = "1234";
		b = "";
		c = 0;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("1234"));

		a = "";
		b = "";
		c = 0;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 0;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));
	}

	/**
	 * test: replace(string, replacement, start, length) --> length = 1
	 */
	public void testReplace3()
	{
		String a;
		String b;
		int c;

		a = "1234";
		b = "abcdefghijk";
		c = 1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1abcdefghijk34"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1abcdefghijk34"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, -100000, c, b).equals("abcdefghijk234"));

		a = "1234";
		b = "a";
		c = 1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("a234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1a34"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12a4"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123a"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123a"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12a4"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1a34"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("a234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("a234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("a234"));

		a = "1234";
		b = "";
		c = 1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("134"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("124"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("124"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("134"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("234"));

		a = "";
		b = "";
		c = 1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));
	}

	/**
	 * test: replace(string, replacement, start, length) --> length = 1000000
	 */
	public void testReplace4()
	{
		String a;
		String b;
		int c;

		a = "1234";
		b = "abcdefghijk";
		c = 1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));

		a = "1234";
		b = "a";
		c = 1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("a"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1a"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12a"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123a"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123a"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12a"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1a"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("a"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("a"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("a"));

		a = "1234";
		b = "";
		c = 1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "";
		c = 1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));
	}

	/**
	 * test: replace(string, replacement, start, length) --> length = -1
	 */
	public void testReplace5()
	{
		String a;
		String b;
		int c;

		a = "1234";
		b = "abcdefghijk";
		c = -1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk4"));

		a = "1234";
		b = "a";
		c = -1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("a4"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1a4"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12a4"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12a4"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1a4"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("a4"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("a4"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("a4"));

		a = "1234";
		b = "";
		c = -1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("4"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("14"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("124"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("124"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("14"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("4"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("4"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("4"));

		a = "";
		b = "";
		c = -1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "abcdefghijk";
		c = -1;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));
	}

	/**
	 * test: replace(string, replacement, start, length) --> length = -1000000
	 */
	public void testReplace6()
	{
		String a;
		String b;
		int c;

		a = "1234";
		b = "abcdefghijk";
		c = -1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk1234"));

		a = "1234";
		b = "a";
		c = -1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1a234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("12a34"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234a"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234a"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("123a4"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("12a34"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1a234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("a1234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("a1234"));

		a = "1234";
		b = "";
		c = -1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("1234"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("1234"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("1234"));

		a = "";
		b = "";
		c = -1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals(""));

		assertTrue(StringUtils.replace(a, -1, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -2, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -3, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -4, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -5, c, b).equals(""));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals(""));

		a = "";
		b = "abcdefghijk";
		c = -1000000;

		assertTrue(StringUtils.replace(a, 0, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, 1000000, c, b).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, -1, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -2, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -3, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -4, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -5, c, b).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, -1000000, c, b).equals("abcdefghijk"));
	}

	public void testSplitLines()
	{
		final String lines = "A\nB\n\nC\nD";
		assertEquals(4, StringUtils.splitLines(lines).length);
		assertEquals(5, StringUtils.splitLinesPreserveAllTokens(lines).length);
	}

	public void testSubstringBeforeIgnoreCase()
	{
		String a;

		a = "abcdef";
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "c").equals("ab"));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "C").equals("ab"));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "X").equals(""));

		a = null;
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "c").equals(""));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "C").equals(""));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "X").equals(""));

		a = "";
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "c").equals(""));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "C").equals(""));
		assertTrue(StringUtils.substringBeforeIgnoreCase(a, "X").equals(""));
	}
}