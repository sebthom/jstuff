/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringsTest {
   @Test
   public void testAnsiToHTML() {
      assertThat(Strings.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?")).hasToString(
         "<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>");

      assertThat(Strings.ansiColorsToHTML("\u001B[33;40mHello World!\u001B[40;42mHow are you?\u001B[0m")).hasToString(
         "<span style=\"color:yellow;background-color:black;\">Hello World!</span><span style=\"color:yellow;background-color:green;\">How are you?</span>");

      assertThat(Strings.ansiColorsToHTML("\u001B[0m\u001B[0m")).isEmpty();
   }

   @Test
   public void testContainsAny() {
      assertThat(Strings.containsAny("abcdef", "abcdef")).isTrue();
      assertThat(Strings.containsAny("abcdef", "123", "bc")).isTrue();
      assertThat(Strings.containsAny("abcdef", "123", "456")).isFalse();

      assertThat(Strings.containsAny("abcdef", "")).isFalse();
      assertThat(Strings.containsAny("abcdef", (String) null)).isFalse();

      assertThat(Strings.containsAny(List.of("abc", "def"), "de", "456")).isTrue();
      assertThat(Strings.containsAny(List.of("abc", "def"), "123", "456")).isFalse();
   }

   @Test
   public void testCountMatches() {
      assertThat(Strings.countMatches("1234512345", "1", 0)).isEqualTo(2);
      assertThat(Strings.countMatches("1234512345", "1", 1)).isEqualTo(1);
      assertThat(Strings.countMatches("1234512345", "1", 5)).isEqualTo(1);
      assertThat(Strings.countMatches("1234512345", "1", 6)).isZero();
      assertThat(Strings.countMatches("1234512345", "1", 100)).isZero();
      assertThat(Strings.countMatches("1234512345", "1", -4)).isZero();
      assertThat(Strings.countMatches("1234512345", "1", -5)).isEqualTo(1);
      assertThat(Strings.countMatches("1234512345", "1", -9)).isEqualTo(1);
      assertThat(Strings.countMatches("1234512345", "1", -10)).isEqualTo(2);
      assertThat(Strings.countMatches("1234512345", "1", -100)).isEqualTo(2);
      assertThat(Strings.countMatches(null, "1", 1)).isZero();
      assertThat(Strings.countMatches("1", null, 1)).isZero();
   }

   @Test
   public void testGetNewLineSeparator() {
      assertThat(Strings.getNewLineSeparator("abc\ndef")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("abc\n")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("\n")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("abc\rdef")).isEqualTo("\r");
      assertThat(Strings.getNewLineSeparator("abc\r")).isEqualTo("\r");
      assertThat(Strings.getNewLineSeparator("\r")).isEqualTo("\r");
      assertThat(Strings.getNewLineSeparator("abc\r\ndef")).isEqualTo("\r\n");
      assertThat(Strings.getNewLineSeparator("abc\r\n")).isEqualTo("\r\n");
      assertThat(Strings.getNewLineSeparator("\r\n")).isEqualTo("\r\n");
      assertThat(Strings.getNewLineSeparator("abc\n\rdef")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("abc\n\r")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("\n\r")).isEqualTo("\n");
      assertThat(Strings.getNewLineSeparator("abcdef")).isNull();
   }

   @Test
   public void testGlobToRegEx() {
      assertThat(Pattern.compile(Strings.globToRegex("**/file?.txt").toString()).matcher("aa/bb/file1.txt").matches()).isTrue();
      assertThat(Pattern.compile(Strings.globToRegex("*.txt").toString()).matcher("file.txt").matches()).isTrue();
      assertThat(Pattern.compile(Strings.globToRegex("*.txt").toString()).matcher("file.pdf").matches()).isFalse();
      assertThat(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.txt").matches()).isTrue();
      assertThat(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.pdf").matches()).isTrue();
      assertThat(Pattern.compile(Strings.globToRegex("*.{pdf,txt}").toString()).matcher("file.xml").matches()).isFalse();
      assertThat(Pattern.compile(Strings.globToRegex("file[0-9].txt").toString()).matcher("file1.txt").matches()).isTrue();
      assertThat(Pattern.compile(Strings.globToRegex("file[!0-9].txt").toString()).matcher("file1.txt").matches()).isFalse();
      assertThat(Pattern.compile(Strings.globToRegex("file[!0-9].txt").toString()).matcher("fileA.txt").matches()).isTrue();
   }

   /**
    * test: replace(string, replacement, start) --> no length argument
    */
   @Test
   public void testReplaceAt1() {
      String a;
      String b;

      a = "1234";
      b = "abcdefghijk";

      assertThat(Strings.replaceAt(a, 0, b)).asString().asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, b)).asString().isEqualTo("1abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, b)).asString().isEqualTo("12abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, b)).asString().isEqualTo("12abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, b)).asString().isEqualTo("1abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, b)).asString().isEqualTo("abcdefghijk");

      a = "1234";
      b = "a";

      assertThat(Strings.replaceAt(a, 0, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, 1, b)).asString().isEqualTo("1a");
      assertThat(Strings.replaceAt(a, 2, b)).asString().isEqualTo("12a");
      assertThat(Strings.replaceAt(a, 3, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, 4, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, -2, b)).asString().isEqualTo("12a");
      assertThat(Strings.replaceAt(a, -3, b)).asString().isEqualTo("1a");
      assertThat(Strings.replaceAt(a, -4, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, -5, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, -1_000_000, b)).asString().isEqualTo("a");

      a = "1234";
      b = "";

      assertThat(Strings.replaceAt(a, 0, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, b)).asString().isEqualTo("1");
      assertThat(Strings.replaceAt(a, 2, b)).asString().isEqualTo("12");
      assertThat(Strings.replaceAt(a, 3, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, 4, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, -2, b)).asString().isEqualTo("12");
      assertThat(Strings.replaceAt(a, -3, b)).asString().isEqualTo("1");
      assertThat(Strings.replaceAt(a, -4, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, b)).isEmpty();

      a = "";
      b = "";

      assertThat(Strings.replaceAt(a, 0, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, b)).isEmpty();

      a = "";
      b = "abcdefghijk";

      assertThat(Strings.replaceAt(a, 0, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, b)).asString().isEqualTo("abcdefghijk");
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 0
    */
   @Test
   public void testReplaceAt2() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 0;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1abcdefghijk234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12abcdefghijk34");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12abcdefghijk34");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1abcdefghijk234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, -100000, c, b)).asString().isEqualTo("abcdefghijk1234");

      a = "1234";
      b = "a";
      c = 0;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1a234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12a34");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12a34");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1a234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("a1234");

      a = "1234";
      b = "";
      c = 0;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("1234");

      a = "";
      b = "";
      c = 0;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "abcdefghijk";
      c = 0;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 1
    */
   @Test
   public void testReplaceAt3() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1abcdefghijk34");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12abcdefghijk4");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12abcdefghijk4");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1abcdefghijk34");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk234");
      assertThat(Strings.replaceAt(a, -100000, c, b)).asString().isEqualTo("abcdefghijk234");

      a = "1234";
      b = "a";
      c = 1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("a234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1a34");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12a4");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12a4");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1a34");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("a234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("a234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("a234");

      a = "1234";
      b = "";
      c = 1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("134");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("124");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("124");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("134");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("234");

      a = "";
      b = "";
      c = 1;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "abcdefghijk";
      c = 1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");
   }

   /**
    * test: replace(string, replacement, start, length) --> length = 1_000_000
    */
   @Test
   public void testReplaceAt4() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = 1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      a = "1234";
      b = "a";
      c = 1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1a");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12a");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123a");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12a");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1a");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("a");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("a");

      a = "1234";
      b = "";
      c = 1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1");
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "";
      c = 1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "abcdefghijk";
      c = 1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");
   }

   /**
    * test: replace(string, replacement, start, length) --> length = -1
    */
   @Test
   public void testReplaceAt5() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = -1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk4");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1abcdefghijk4");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12abcdefghijk4");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12abcdefghijk4");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1abcdefghijk4");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk4");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk4");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk4");

      a = "1234";
      b = "a";
      c = -1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("a4");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1a4");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12a4");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12a4");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1a4");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("a4");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("a4");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("a4");

      a = "1234";
      b = "";
      c = -1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("4");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("14");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("124");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("124");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("14");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("4");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("4");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("4");

      a = "";
      b = "";
      c = -1;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "abcdefghijk";
      c = -1;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");
   }

   /**
    * test: replace(string, replacement, start, length) --> length = -1_000_000
    */
   @Test
   public void testReplaceAt6() {
      String a;
      String b;
      int c;

      a = "1234";
      b = "abcdefghijk";
      c = -1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1abcdefghijk234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12abcdefghijk34");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123abcdefghijk4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12abcdefghijk34");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1abcdefghijk234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk1234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk1234");

      a = "1234";
      b = "a";
      c = -1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1a234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("12a34");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234a");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234a");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("123a4");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("12a34");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1a234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("a1234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("a1234");

      a = "1234";
      b = "";
      c = -1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("1234");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("1234");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("1234");

      a = "";
      b = "";
      c = -1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).isEmpty();

      assertThat(Strings.replaceAt(a, -1, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -2, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -3, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -4, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -5, c, b)).isEmpty();
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).isEmpty();

      a = "";
      b = "abcdefghijk";
      c = -1_000_000;

      assertThat(Strings.replaceAt(a, 0, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, 1_000_000, c, b)).asString().isEqualTo("abcdefghijk");

      assertThat(Strings.replaceAt(a, -1, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -2, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -3, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -4, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -5, c, b)).asString().isEqualTo("abcdefghijk");
      assertThat(Strings.replaceAt(a, -1_000_000, c, b)).asString().isEqualTo("abcdefghijk");
   }

   @Test
   public void testRepleaceEach() {
      assertThat(Strings.replaceEach("There was a man with seven kids.", //
         "man", "woman", //
         "seven", "three", //
         "kids", "cats" //
      )).isEqualTo("There was a woman with three cats.");
   }

   @Test
   public void testSplitLines() {
      final String lines = "A\nB\n\nC\nD";
      assertThat(Strings.splitLines(lines, true)).hasSize(5);
      assertThat(Strings.splitLines(lines, false)).hasSize(4);
   }

   @Test
   public void testSplitToList() {
      assertThat(Strings.splitToListNullable(null, '.')).isNull();
      assertThat(Strings.splitToList("", '.')).isEmpty();
      assertThat(Strings.splitToList(".", '.')).isEmpty();
      assertThat(Strings.splitToList("...", '.')).isEmpty();
      assertThat(Strings.splitToList("f", '.')).isEqualTo(Arrays.asList("f"));
      assertThat(Strings.splitToList("foo", '.')).isEqualTo(Arrays.asList("foo"));
      assertThat(Strings.splitToList(".foo.", '.')).isEqualTo(Arrays.asList("foo"));
      assertThat(Strings.splitToList("foo.bar", '.')).isEqualTo(Arrays.asList("foo", "bar"));
      assertThat(Strings.splitToList(".foo..bar..", '.')).isEqualTo(Arrays.asList("foo", "bar"));
   }

   @Test
   public void testSubstringBeforeIgnoreCase() {
      String a;

      a = "abcdef";
      assertThat(Strings.substringBeforeIgnoreCase(a, "c")).isEqualTo("ab");
      assertThat(Strings.substringBeforeIgnoreCase(a, "C")).isEqualTo("ab");
      assertThat(Strings.substringBeforeIgnoreCase(a, "X")).isEmpty();

      a = null;
      assertThat(Strings.substringBeforeIgnoreCaseNullable(a, "c")).isNull();

      a = "";
      assertThat(Strings.substringBeforeIgnoreCase(a, "c")).isEmpty();
   }
}
