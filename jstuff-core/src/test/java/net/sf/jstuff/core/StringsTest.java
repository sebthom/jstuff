/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core;

import java.util.regex.Pattern;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringsTest extends TestCase {

   public void testAnsiToHTML() {
      assertEquals(
         "<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>",
         Strings.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?").toString());

      assertEquals(
         "<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>",
         Strings.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?\u001B[0m").toString());

      assertEquals(0, Strings.ansiColorsToHTML("\u001B[0m\u001B[0m").length());
   }

   public void testContainsAny() {
      assertTrue(Strings.containsAny("abcdef", "abcdef"));
      assertTrue(Strings.containsAny("abcdef", "123", "bc"));
      assertFalse(Strings.containsAny("abcdef", "123", "456"));

      assertFalse(Strings.containsAny("abcdef", ""));
      assertFalse(Strings.containsAny("abcdef", (String) null));

      assertTrue(Strings.containsAny(CollectionUtils.newArrayList("abc", "def"), "de", "456"));
      assertFalse(Strings.containsAny(CollectionUtils.newArrayList("abc", "def"), "123", "456"));
   }

   public void testCountMatches() {
      assertEquals(2, Strings.countMatches("1234512345", "1", 0));
      assertEquals(1, Strings.countMatches("1234512345", "1", 1));
      assertEquals(1, Strings.countMatches("1234512345", "1", 5));
      assertEquals(0, Strings.countMatches("1234512345", "1", 6));
      assertEquals(0, Strings.countMatches("1234512345", "1", 100));
      assertEquals(0, Strings.countMatches("1234512345", "1", -4));
      assertEquals(1, Strings.countMatches("1234512345", "1", -5));
      assertEquals(1, Strings.countMatches("1234512345", "1", -9));
      assertEquals(2, Strings.countMatches("1234512345", "1", -10));
      assertEquals(2, Strings.countMatches("1234512345", "1", -100));
      assertEquals(0, Strings.countMatches(null, "1", 1));
      assertEquals(0, Strings.countMatches("1", null, 1));
   }

   public void testGetNewLineSeparator() {
      assertEquals("\n", Strings.getNewLineSeparator("abc\ndef"));
      assertEquals("\n", Strings.getNewLineSeparator("abc\n"));
      assertEquals("\n", Strings.getNewLineSeparator("\n"));
      assertEquals("\r", Strings.getNewLineSeparator("abc\rdef"));
      assertEquals("\r", Strings.getNewLineSeparator("abc\r"));
      assertEquals("\r", Strings.getNewLineSeparator("\r"));
      assertEquals("\r\n", Strings.getNewLineSeparator("abc\r\ndef"));
      assertEquals("\r\n", Strings.getNewLineSeparator("abc\r\n"));
      assertEquals("\r\n", Strings.getNewLineSeparator("\r\n"));
      assertEquals("\n", Strings.getNewLineSeparator("abc\n\rdef"));
      assertEquals("\n", Strings.getNewLineSeparator("abc\n\r"));
      assertEquals("\n", Strings.getNewLineSeparator("\n\r"));
      assertEquals(null, Strings.getNewLineSeparator("abcdef"));
   }

   public void testGlobToRegEx() {
      assertTrue(Pattern.compile(Strings.globToRegex("**/file?.txt").toString()).matcher("aa/bb/file1.txt").matches());
      assertTrue(Pattern.compile(Strings.globToRegex("*.txt").toString()).matcher("file.txt").matches());
      assertFalse(Pattern.compile(Strings.globToRegex("*.txt").toString()).matcher("file.pdf").matches());
      assertTrue(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.txt").matches());
      assertTrue(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.pdf").matches());
      assertFalse(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.xml").matches());
      assertTrue(Pattern.compile(Strings.globToRegex("file[0-9].txt").toString()).matcher("file1.txt").matches());
      assertFalse(Pattern.compile(Strings.globToRegex("file[!0-9].txt").toString()).matcher("file1.txt").matches());
      assertTrue(Pattern.compile(Strings.globToRegex("file[!0-9].txt").toString()).matcher("fileA.txt").matches());
   }

   /**
    * test: replace(string, replacement, start) --> no length argument
    */
   public void testReplace1() {
      String a;
      String b;

      a = "1234";
      b = "abcdefghijk";

      assertTrue(Strings.replace(a, 0, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, b).equals("1abcdefghijk"));
      assertTrue(Strings.replace(a, 2, b).equals("12abcdefghijk"));
      assertTrue(Strings.replace(a, 3, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, 4, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 5, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, b).equals("1234abcdefghijk"));

      assertTrue(Strings.replace(a, -1, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, -2, b).equals("12abcdefghijk"));
      assertTrue(Strings.replace(a, -3, b).equals("1abcdefghijk"));
      assertTrue(Strings.replace(a, -4, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, b).equals("abcdefghijk"));

      a = "1234";
      b = "a";

      assertTrue(Strings.replace(a, 0, b).equals("a"));
      assertTrue(Strings.replace(a, 1, b).equals("1a"));
      assertTrue(Strings.replace(a, 2, b).equals("12a"));
      assertTrue(Strings.replace(a, 3, b).equals("123a"));
      assertTrue(Strings.replace(a, 4, b).equals("1234a"));
      assertTrue(Strings.replace(a, 5, b).equals("1234a"));
      assertTrue(Strings.replace(a, 1000000, b).equals("1234a"));

      assertTrue(Strings.replace(a, -1, b).equals("123a"));
      assertTrue(Strings.replace(a, -2, b).equals("12a"));
      assertTrue(Strings.replace(a, -3, b).equals("1a"));
      assertTrue(Strings.replace(a, -4, b).equals("a"));
      assertTrue(Strings.replace(a, -5, b).equals("a"));
      assertTrue(Strings.replace(a, -1000000, b).equals("a"));

      a = "1234";
      b = "";

      assertTrue(Strings.replace(a, 0, b).equals(""));
      assertTrue(Strings.replace(a, 1, b).equals("1"));
      assertTrue(Strings.replace(a, 2, b).equals("12"));
      assertTrue(Strings.replace(a, 3, b).equals("123"));
      assertTrue(Strings.replace(a, 4, b).equals("1234"));
      assertTrue(Strings.replace(a, 5, b).equals("1234"));
      assertTrue(Strings.replace(a, 1000000, b).equals("1234"));

      assertTrue(Strings.replace(a, -1, b).equals("123"));
      assertTrue(Strings.replace(a, -2, b).equals("12"));
      assertTrue(Strings.replace(a, -3, b).equals("1"));
      assertTrue(Strings.replace(a, -4, b).equals(""));
      assertTrue(Strings.replace(a, -5, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, b).equals(""));

      a = "";
      b = "";

      assertTrue(Strings.replace(a, 0, b).equals(""));
      assertTrue(Strings.replace(a, 1, b).equals(""));
      assertTrue(Strings.replace(a, 2, b).equals(""));
      assertTrue(Strings.replace(a, 3, b).equals(""));
      assertTrue(Strings.replace(a, 4, b).equals(""));
      assertTrue(Strings.replace(a, 5, b).equals(""));
      assertTrue(Strings.replace(a, 1000000, b).equals(""));

      assertTrue(Strings.replace(a, -1, b).equals(""));
      assertTrue(Strings.replace(a, -2, b).equals(""));
      assertTrue(Strings.replace(a, -3, b).equals(""));
      assertTrue(Strings.replace(a, -4, b).equals(""));
      assertTrue(Strings.replace(a, -5, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, b).equals(""));

      a = "";
      b = "abcdefghijk";

      assertTrue(Strings.replace(a, 0, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 2, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 3, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 4, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 5, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, b).equals("abcdefghijk"));

      assertTrue(Strings.replace(a, -1, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -2, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -3, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -4, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, b).equals("abcdefghijk"));
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 0
    */
   public void testReplace2() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 0;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk1234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1abcdefghijk234"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12abcdefghijk34"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123abcdefghijk4"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123abcdefghijk4"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12abcdefghijk34"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1abcdefghijk234"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk1234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk1234"));
      assertTrue(Strings.replace(a, -100000, c, b).equals("abcdefghijk1234"));

      a = "1234";
      b = "a";
      c = 0;

      assertTrue(Strings.replace(a, 0, c, b).equals("a1234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1a234"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12a34"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123a4"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234a"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123a4"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12a34"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1a234"));
      assertTrue(Strings.replace(a, -4, c, b).equals("a1234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("a1234"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("a1234"));

      a = "1234";
      b = "";
      c = 0;

      assertTrue(Strings.replace(a, 0, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 2, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 3, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234"));

      assertTrue(Strings.replace(a, -1, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -2, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -4, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("1234"));

      a = "";
      b = "";
      c = 0;

      assertTrue(Strings.replace(a, 0, c, b).equals(""));
      assertTrue(Strings.replace(a, 1, c, b).equals(""));
      assertTrue(Strings.replace(a, 2, c, b).equals(""));
      assertTrue(Strings.replace(a, 3, c, b).equals(""));
      assertTrue(Strings.replace(a, 4, c, b).equals(""));
      assertTrue(Strings.replace(a, 5, c, b).equals(""));
      assertTrue(Strings.replace(a, 1000000, c, b).equals(""));

      assertTrue(Strings.replace(a, -1, c, b).equals(""));
      assertTrue(Strings.replace(a, -2, c, b).equals(""));
      assertTrue(Strings.replace(a, -3, c, b).equals(""));
      assertTrue(Strings.replace(a, -4, c, b).equals(""));
      assertTrue(Strings.replace(a, -5, c, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, c, b).equals(""));

      a = "";
      b = "abcdefghijk";
      c = 0;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk"));
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 1
    */
   public void testReplace3() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 1;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1abcdefghijk34"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12abcdefghijk4"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12abcdefghijk4"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1abcdefghijk34"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk234"));
      assertTrue(Strings.replace(a, -100000, c, b).equals("abcdefghijk234"));

      a = "1234";
      b = "a";
      c = 1;

      assertTrue(Strings.replace(a, 0, c, b).equals("a234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1a34"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12a4"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123a"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234a"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123a"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12a4"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1a34"));
      assertTrue(Strings.replace(a, -4, c, b).equals("a234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("a234"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("a234"));

      a = "1234";
      b = "";
      c = 1;

      assertTrue(Strings.replace(a, 0, c, b).equals("234"));
      assertTrue(Strings.replace(a, 1, c, b).equals("134"));
      assertTrue(Strings.replace(a, 2, c, b).equals("124"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123"));
      assertTrue(Strings.replace(a, -2, c, b).equals("124"));
      assertTrue(Strings.replace(a, -3, c, b).equals("134"));
      assertTrue(Strings.replace(a, -4, c, b).equals("234"));
      assertTrue(Strings.replace(a, -5, c, b).equals("234"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("234"));

      a = "";
      b = "";
      c = 1;

      assertTrue(Strings.replace(a, 0, c, b).equals(""));
      assertTrue(Strings.replace(a, 1, c, b).equals(""));
      assertTrue(Strings.replace(a, 2, c, b).equals(""));
      assertTrue(Strings.replace(a, 3, c, b).equals(""));
      assertTrue(Strings.replace(a, 4, c, b).equals(""));
      assertTrue(Strings.replace(a, 5, c, b).equals(""));
      assertTrue(Strings.replace(a, 1000000, c, b).equals(""));

      assertTrue(Strings.replace(a, -1, c, b).equals(""));
      assertTrue(Strings.replace(a, -2, c, b).equals(""));
      assertTrue(Strings.replace(a, -3, c, b).equals(""));
      assertTrue(Strings.replace(a, -4, c, b).equals(""));
      assertTrue(Strings.replace(a, -5, c, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, c, b).equals(""));

      a = "";
      b = "abcdefghijk";
      c = 1;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk"));
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 1000000
    */
   public void testReplace4() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 1000000;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1abcdefghijk"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12abcdefghijk"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12abcdefghijk"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1abcdefghijk"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk"));

      a = "1234";
      b = "a";
      c = 1000000;

      assertTrue(Strings.replace(a, 0, c, b).equals("a"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1a"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12a"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123a"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234a"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123a"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12a"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1a"));
      assertTrue(Strings.replace(a, -4, c, b).equals("a"));
      assertTrue(Strings.replace(a, -5, c, b).equals("a"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("a"));

      a = "1234";
      b = "";
      c = 1000000;

      assertTrue(Strings.replace(a, 0, c, b).equals(""));
      assertTrue(Strings.replace(a, 1, c, b).equals("1"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1"));
      assertTrue(Strings.replace(a, -4, c, b).equals(""));
      assertTrue(Strings.replace(a, -5, c, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, c, b).equals(""));

      a = "";
      b = "";
      c = 1000000;

      assertTrue(Strings.replace(a, 0, c, b).equals(""));
      assertTrue(Strings.replace(a, 1, c, b).equals(""));
      assertTrue(Strings.replace(a, 2, c, b).equals(""));
      assertTrue(Strings.replace(a, 3, c, b).equals(""));
      assertTrue(Strings.replace(a, 4, c, b).equals(""));
      assertTrue(Strings.replace(a, 5, c, b).equals(""));
      assertTrue(Strings.replace(a, 1000000, c, b).equals(""));

      assertTrue(Strings.replace(a, -1, c, b).equals(""));
      assertTrue(Strings.replace(a, -2, c, b).equals(""));
      assertTrue(Strings.replace(a, -3, c, b).equals(""));
      assertTrue(Strings.replace(a, -4, c, b).equals(""));
      assertTrue(Strings.replace(a, -5, c, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, c, b).equals(""));

      a = "";
      b = "abcdefghijk";
      c = 1000000;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk"));
   }

   /**
    * test: replace(string, replacement, start, length) --> length = -1
    */
   public void testReplace5() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = -1;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk4"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1abcdefghijk4"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12abcdefghijk4"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123abcdefghijk4"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123abcdefghijk4"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12abcdefghijk4"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1abcdefghijk4"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk4"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk4"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk4"));

      a = "1234";
      b = "a";
      c = -1;

      assertTrue(Strings.replace(a, 0, c, b).equals("a4"));
      assertTrue(Strings.replace(a, 1, c, b).equals("1a4"));
      assertTrue(Strings.replace(a, 2, c, b).equals("12a4"));
      assertTrue(Strings.replace(a, 3, c, b).equals("123a4"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234a"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234a"));

      assertTrue(Strings.replace(a, -1, c, b).equals("123a4"));
      assertTrue(Strings.replace(a, -2, c, b).equals("12a4"));
      assertTrue(Strings.replace(a, -3, c, b).equals("1a4"));
      assertTrue(Strings.replace(a, -4, c, b).equals("a4"));
      assertTrue(Strings.replace(a, -5, c, b).equals("a4"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("a4"));

      a = "1234";
      b = "";
      c = -1;

      assertTrue(Strings.replace(a, 0, c, b).equals("4"));
      assertTrue(Strings.replace(a, 1, c, b).equals("14"));
      assertTrue(Strings.replace(a, 2, c, b).equals("124"));
      assertTrue(Strings.replace(a, 3, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 4, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 5, c, b).equals("1234"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("1234"));

      assertTrue(Strings.replace(a, -1, c, b).equals("1234"));
      assertTrue(Strings.replace(a, -2, c, b).equals("124"));
      assertTrue(Strings.replace(a, -3, c, b).equals("14"));
      assertTrue(Strings.replace(a, -4, c, b).equals("4"));
      assertTrue(Strings.replace(a, -5, c, b).equals("4"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("4"));

      a = "";
      b = "";
      c = -1;

      assertTrue(Strings.replace(a, 0, c, b).equals(""));
      assertTrue(Strings.replace(a, 1, c, b).equals(""));
      assertTrue(Strings.replace(a, 2, c, b).equals(""));
      assertTrue(Strings.replace(a, 3, c, b).equals(""));
      assertTrue(Strings.replace(a, 4, c, b).equals(""));
      assertTrue(Strings.replace(a, 5, c, b).equals(""));
      assertTrue(Strings.replace(a, 1000000, c, b).equals(""));

      assertTrue(Strings.replace(a, -1, c, b).equals(""));
      assertTrue(Strings.replace(a, -2, c, b).equals(""));
      assertTrue(Strings.replace(a, -3, c, b).equals(""));
      assertTrue(Strings.replace(a, -4, c, b).equals(""));
      assertTrue(Strings.replace(a, -5, c, b).equals(""));
      assertTrue(Strings.replace(a, -1000000, c, b).equals(""));

      a = "";
      b = "abcdefghijk";
      c = -1;

      assertTrue(Strings.replace(a, 0, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, 1000000, c, b).equals("abcdefghijk"));

      assertTrue(Strings.replace(a, -1, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -2, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -3, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -4, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -5, c, b).equals("abcdefghijk"));
      assertTrue(Strings.replace(a, -1000000, c, b).equals("abcdefghijk"));
   }

   /**
    * test: replace(string, replacement, start, length) --> length = -1000000
    */
   public void testReplace6() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = -1000000;

      assertEquals("abcdefghijk1234", Strings.replace(a, 0, c, b));
      assertEquals("1abcdefghijk234", Strings.replace(a, 1, c, b));
      assertEquals("12abcdefghijk34", Strings.replace(a, 2, c, b));
      assertEquals("123abcdefghijk4", Strings.replace(a, 3, c, b));
      assertEquals("1234abcdefghijk", Strings.replace(a, 4, c, b));
      assertEquals("1234abcdefghijk", Strings.replace(a, 5, c, b));
      assertEquals("1234abcdefghijk", Strings.replace(a, 1000000, c, b));

      assertEquals("123abcdefghijk4", Strings.replace(a, -1, c, b));
      assertEquals("12abcdefghijk34", Strings.replace(a, -2, c, b));
      assertEquals("1abcdefghijk234", Strings.replace(a, -3, c, b));
      assertEquals("abcdefghijk1234", Strings.replace(a, -4, c, b));
      assertEquals("abcdefghijk1234", Strings.replace(a, -5, c, b));
      assertEquals("abcdefghijk1234", Strings.replace(a, -1000000, c, b));

      a = "1234";
      b = "a";
      c = -1000000;

      assertEquals("a1234", Strings.replace(a, 0, c, b));
      assertEquals("1a234", Strings.replace(a, 1, c, b));
      assertEquals("12a34", Strings.replace(a, 2, c, b));
      assertEquals("123a4", Strings.replace(a, 3, c, b));
      assertEquals("1234a", Strings.replace(a, 4, c, b));
      assertEquals("1234a", Strings.replace(a, 5, c, b));
      assertEquals("1234a", Strings.replace(a, 1000000, c, b));

      assertEquals("123a4", Strings.replace(a, -1, c, b));
      assertEquals("12a34", Strings.replace(a, -2, c, b));
      assertEquals("1a234", Strings.replace(a, -3, c, b));
      assertEquals("a1234", Strings.replace(a, -4, c, b));
      assertEquals("a1234", Strings.replace(a, -5, c, b));
      assertEquals("a1234", Strings.replace(a, -1000000, c, b));

      a = "1234";
      b = "";
      c = -1000000;

      assertEquals("1234", Strings.replace(a, 0, c, b));
      assertEquals("1234", Strings.replace(a, 1, c, b));
      assertEquals("1234", Strings.replace(a, 2, c, b));
      assertEquals("1234", Strings.replace(a, 3, c, b));
      assertEquals("1234", Strings.replace(a, 4, c, b));
      assertEquals("1234", Strings.replace(a, 5, c, b));
      assertEquals("1234", Strings.replace(a, 1000000, c, b));

      assertEquals("1234", Strings.replace(a, -1, c, b));
      assertEquals("1234", Strings.replace(a, -2, c, b));
      assertEquals("1234", Strings.replace(a, -3, c, b));
      assertEquals("1234", Strings.replace(a, -4, c, b));
      assertEquals("1234", Strings.replace(a, -5, c, b));
      assertEquals("1234", Strings.replace(a, -1000000, c, b));

      a = "";
      b = "";
      c = -1000000;

      assertEquals("", Strings.replace(a, 0, c, b));
      assertEquals("", Strings.replace(a, 1, c, b));
      assertEquals("", Strings.replace(a, 2, c, b));
      assertEquals("", Strings.replace(a, 3, c, b));
      assertEquals("", Strings.replace(a, 4, c, b));
      assertEquals("", Strings.replace(a, 5, c, b));
      assertEquals("", Strings.replace(a, 1000000, c, b));

      assertEquals("", Strings.replace(a, -1, c, b));
      assertEquals("", Strings.replace(a, -2, c, b));
      assertEquals("", Strings.replace(a, -3, c, b));
      assertEquals("", Strings.replace(a, -4, c, b));
      assertEquals("", Strings.replace(a, -5, c, b));
      assertEquals("", Strings.replace(a, -1000000, c, b));

      a = "";
      b = "abcdefghijk";
      c = -1000000;

      assertEquals("abcdefghijk", Strings.replace(a, 0, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 1, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 2, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 3, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 4, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 5, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, 1000000, c, b));

      assertEquals("abcdefghijk", Strings.replace(a, -1, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, -2, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, -3, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, -4, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, -5, c, b));
      assertEquals("abcdefghijk", Strings.replace(a, -1000000, c, b));
   }

   public void testRepleaceEach() {
      assertEquals("There was a woman with three cats.", //
         Strings.replaceEach("There was a man with seven kids.", //
            "man", "woman", //
            "seven", "three", //
            "kids", "cats" //
         ) //
      );
   }

   public void testSplitLines() {
      final String lines = "A\nB\n\nC\nD";
      assertEquals(4, Strings.splitLines(lines).length);
      assertEquals(5, Strings.splitLinesPreserveAllTokens(lines).length);
   }

   public void testSubstringBeforeIgnoreCase() {
      String a;

      a = "abcdef";
      assertEquals("ab", Strings.substringBeforeIgnoreCase(a, "c"));
      assertEquals("ab", Strings.substringBeforeIgnoreCase(a, "C"));
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "X"));

      a = null;
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "c"));
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "C"));
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "X"));

      a = "";
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "c"));
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "C"));
      assertEquals("", Strings.substringBeforeIgnoreCase(a, "X"));
   }
}
