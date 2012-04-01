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
package net.sf.jstuff.core;

import junit.framework.TestCase;

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

	public void testCountSubstrings()
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

		assertTrue(StringUtils.replace(a, b, 0).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000).equals("abcdefghijk"));

		a = "1234";
		b = "a";

		assertTrue(StringUtils.replace(a, b, 0).equals("a"));
		assertTrue(StringUtils.replace(a, b, 1).equals("1a"));
		assertTrue(StringUtils.replace(a, b, 2).equals("12a"));
		assertTrue(StringUtils.replace(a, b, 3).equals("123a"));
		assertTrue(StringUtils.replace(a, b, 4).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1).equals("123a"));
		assertTrue(StringUtils.replace(a, b, -2).equals("12a"));
		assertTrue(StringUtils.replace(a, b, -3).equals("1a"));
		assertTrue(StringUtils.replace(a, b, -4).equals("a"));
		assertTrue(StringUtils.replace(a, b, -5).equals("a"));
		assertTrue(StringUtils.replace(a, b, -1000000).equals("a"));

		a = "1234";
		b = "";

		assertTrue(StringUtils.replace(a, b, 0).equals(""));
		assertTrue(StringUtils.replace(a, b, 1).equals("1"));
		assertTrue(StringUtils.replace(a, b, 2).equals("12"));
		assertTrue(StringUtils.replace(a, b, 3).equals("123"));
		assertTrue(StringUtils.replace(a, b, 4).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1).equals("123"));
		assertTrue(StringUtils.replace(a, b, -2).equals("12"));
		assertTrue(StringUtils.replace(a, b, -3).equals("1"));
		assertTrue(StringUtils.replace(a, b, -4).equals(""));
		assertTrue(StringUtils.replace(a, b, -5).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000).equals(""));

		a = "";
		b = "";

		assertTrue(StringUtils.replace(a, b, 0).equals(""));
		assertTrue(StringUtils.replace(a, b, 1).equals(""));
		assertTrue(StringUtils.replace(a, b, 2).equals(""));
		assertTrue(StringUtils.replace(a, b, 3).equals(""));
		assertTrue(StringUtils.replace(a, b, 4).equals(""));
		assertTrue(StringUtils.replace(a, b, 5).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000).equals(""));

		assertTrue(StringUtils.replace(a, b, -1).equals(""));
		assertTrue(StringUtils.replace(a, b, -2).equals(""));
		assertTrue(StringUtils.replace(a, b, -3).equals(""));
		assertTrue(StringUtils.replace(a, b, -4).equals(""));
		assertTrue(StringUtils.replace(a, b, -5).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000).equals(""));

		a = "";
		b = "abcdefghijk";

		assertTrue(StringUtils.replace(a, b, 0).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000).equals("abcdefghijk"));
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

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, -100000, c).equals("abcdefghijk1234"));

		a = "1234";
		b = "a";
		c = 0;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1a234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12a34"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12a34"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1a234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("a1234"));

		a = "1234";
		b = "";
		c = 0;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("1234"));

		a = "";
		b = "";
		c = 0;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals(""));

		assertTrue(StringUtils.replace(a, b, -1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 0;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));
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

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, -100000, c).equals("abcdefghijk234"));

		a = "1234";
		b = "a";
		c = 1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("a234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1a34"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12a4"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123a"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123a"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12a4"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1a34"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("a234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("a234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("a234"));

		a = "1234";
		b = "";
		c = 1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("134"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("124"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("124"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("134"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("234"));

		a = "";
		b = "";
		c = 1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals(""));

		assertTrue(StringUtils.replace(a, b, -1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));
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

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));

		a = "1234";
		b = "a";
		c = 1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("a"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1a"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12a"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123a"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123a"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12a"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1a"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("a"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("a"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("a"));

		a = "1234";
		b = "";
		c = 1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "";
		c = 1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals(""));

		assertTrue(StringUtils.replace(a, b, -1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "abcdefghijk";
		c = 1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));
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

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk4"));

		a = "1234";
		b = "a";
		c = -1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("a4"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1a4"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12a4"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12a4"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1a4"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("a4"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("a4"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("a4"));

		a = "1234";
		b = "";
		c = -1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("4"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("14"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("124"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("124"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("14"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("4"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("4"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("4"));

		a = "";
		b = "";
		c = -1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals(""));

		assertTrue(StringUtils.replace(a, b, -1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "abcdefghijk";
		c = -1;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));
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

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123abcdefghijk4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12abcdefghijk34"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1abcdefghijk234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk1234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk1234"));

		a = "1234";
		b = "a";
		c = -1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1a234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("12a34"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234a"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234a"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("123a4"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("12a34"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1a234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("a1234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("a1234"));

		a = "1234";
		b = "";
		c = -1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("1234"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("1234"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("1234"));

		a = "";
		b = "";
		c = -1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals(""));

		assertTrue(StringUtils.replace(a, b, -1, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -2, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -3, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -4, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -5, c).equals(""));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals(""));

		a = "";
		b = "abcdefghijk";
		c = -1000000;

		assertTrue(StringUtils.replace(a, b, 0, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, 1000000, c).equals("abcdefghijk"));

		assertTrue(StringUtils.replace(a, b, -1, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -2, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -3, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -4, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -5, c).equals("abcdefghijk"));
		assertTrue(StringUtils.replace(a, b, -1000000, c).equals("abcdefghijk"));
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