/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
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
   @SuppressWarnings("unused")
   public void testDefaultIf() {
      assertThat(Strings.defaultIfBlank("sometext", "DEFAULT")).isEqualTo("sometext");
      assertThat(Strings.defaultIfBlank("  ", "DEFAULT")).isEqualTo("DEFAULT");
      assertThat(Strings.defaultIfBlank("", "DEFAULT")).isEqualTo("DEFAULT");
      assertThat(Strings.defaultIfBlank(null, "DEFAULT")).isEqualTo("DEFAULT");
      assertThat(Strings.defaultIfEmpty("sometext", "DEFAULT")).isEqualTo("sometext");
      assertThat(Strings.defaultIfEmpty("  ", "DEFAULT")).isEqualTo("  ");
      assertThat(Strings.defaultIfEmpty("", "DEFAULT")).isEqualTo("DEFAULT");
      assertThat(Strings.defaultIfBlank(null, "DEFAULT")).isEqualTo("DEFAULT");
      assertThat(Strings.defaultIfNull("sometext", "DEFAULT")).isEqualTo("sometext");
      assertThat(Strings.defaultIfNull("  ", "DEFAULT")).isEqualTo("  ");
      assertThat(Strings.defaultIfNull("", "DEFAULT")).isEmpty();
      assertThat(Strings.defaultIfNull(null, "DEFAULT")).isEqualTo("DEFAULT");

      // test type interference
      final @Nullable String defaultIfBlank0 = Strings.defaultIfBlank("", null);
      final @NonNull CharSequence defaultIfBlank2 = Strings.defaultIfBlank((CharSequence) "", "");
      final @NonNull CharSequence defaultIfBlank3 = Strings.defaultIfBlank("", (CharSequence) "");
      final @NonNull CharSequence defaultIfBlank4 = Strings.defaultIfBlank("", new StringBuilder());
      final @NonNull String defaultIfBlank5 = Strings.defaultIfBlank(null, "");
      final @NonNull CharSequence defaultIfBlank6 = Strings.defaultIfBlank((CharSequence) null, "");
      final @NonNull CharSequence defaultIfBlank7 = Strings.defaultIfBlank(null, (CharSequence) "");
      final @NonNull StringBuilder defaultIfBlank8 = Strings.defaultIfBlank(null, new StringBuilder());

      final @Nullable String defaultIfEmpty0 = Strings.defaultIfEmpty("", null);
      final @NonNull String defaultIfEmpty1 = Strings.defaultIfEmpty("", "");
      final @NonNull CharSequence defaultIfEmpty2 = Strings.defaultIfEmpty((CharSequence) "", "");
      final @NonNull CharSequence defaultIfEmpty3 = Strings.defaultIfEmpty("", (CharSequence) "");
      final @NonNull CharSequence defaultIfEmpty4 = Strings.defaultIfEmpty("", new StringBuilder());
      final @NonNull String defaultIfEmpty5 = Strings.defaultIfEmpty(null, "");
      final @NonNull CharSequence defaultIfEmpty6 = Strings.defaultIfEmpty((CharSequence) null, "");
      final @NonNull CharSequence defaultIfEmpty7 = Strings.defaultIfEmpty(null, (CharSequence) "");
      final @NonNull StringBuilder defaultIfEmpty8 = Strings.defaultIfEmpty(null, new StringBuilder());

      final @Nullable String defaultIfNull0 = Strings.defaultIfNull("", null);
      final @NonNull String defaultIfNull1 = Strings.defaultIfNull("", "");
      final @NonNull CharSequence defaultIfNull2 = Strings.defaultIfNull((CharSequence) "", "");
      final @NonNull CharSequence defaultIfNull3 = Strings.defaultIfNull("", (CharSequence) "");
      final @NonNull CharSequence defaultIfNull4 = Strings.defaultIfNull("", new StringBuilder());
      final @NonNull String defaultIfNull5 = Strings.defaultIfNull(null, "");
      final @NonNull CharSequence defaultIfNull6 = Strings.defaultIfNull((CharSequence) null, "");
      final @NonNull CharSequence defaultIfNull7 = Strings.defaultIfNull(null, (CharSequence) "");
      final @NonNull StringBuilder defaultIfNull8 = Strings.defaultIfNull(null, new StringBuilder());
   }

   @Test
   @SuppressWarnings("unused")
   public void testNullIf() {
      assertThat(Strings.nullIfBlank("a")).isEqualTo("a");
      assertThat(Strings.nullIfBlank(" ")).isNull();
      assertThat(Strings.nullIfBlank("")).isNull();
      assertThat((String) Strings.nullIfBlank(null)).isNull();
      assertThat(Strings.nullIfEmpty("a")).isEqualTo("a");
      assertThat(Strings.nullIfEmpty(" ")).isEqualTo(" ");
      assertThat(Strings.nullIfEmpty("")).isNull();
      assertThat((String) Strings.nullIfEmpty(null)).isNull();

      // test type interference
      final @Nullable String nullIfBlank1 = Strings.nullIfBlank("a");
      final @Nullable CharSequence nullIfBlank2 = Strings.nullIfBlank((CharSequence) "a");
      final @Nullable StringBuilder nullIfBlank3 = Strings.nullIfBlank(new StringBuilder());
      final @Nullable String nullIfBlank4 = Strings.nullIfBlank(null);
      final @Nullable CharSequence nullIfBlank5 = Strings.nullIfBlank((CharSequence) null);
      final @Nullable StringBuilder nullIfBlank6 = Strings.nullIfBlank((StringBuilder) null);

      final @Nullable String nullIfEmpty1 = Strings.nullIfEmpty("a");
      final @Nullable CharSequence nullIfEmpty2 = Strings.nullIfEmpty((CharSequence) "a");
      final @Nullable StringBuilder nullIfEmpty3 = Strings.nullIfEmpty(new StringBuilder());
      final @Nullable String nullIfEmpty4 = Strings.nullIfEmpty(null);
      final @Nullable CharSequence nullIfEmpty5 = Strings.nullIfEmpty((CharSequence) null);
      final @Nullable StringBuilder nullIfEmpty6 = Strings.nullIfEmpty((StringBuilder) null);
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

   @Test
   public void testPrependLines() {
      assertThat(Strings.prependLines("", "foo")).asString().isEqualTo("foo");
      assertThat(Strings.prependLines("\n", "foo")).asString().isEqualTo("foo\nfoo");
      assertThat(Strings.prependLines("\r", "foo")).asString().isEqualTo("foo\rfoo");
      assertThat(Strings.prependLines("\r\n", "foo")).asString().isEqualTo("foo\r\nfoo");

      assertThat(Strings.prependLines("bar\nbar", "foo")).asString().isEqualTo("foobar\nfoobar");
      assertThat(Strings.prependLines("bar\rbar", "foo")).asString().isEqualTo("foobar\rfoobar");
      assertThat(Strings.prependLines("bar\r\nbar", "foo")).asString().isEqualTo("foobar\r\nfoobar");
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

      assertThat(Strings.replaceAt(a, 0, b)).asString().isEqualTo("abcdefghijk");
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

   @Test
   public void testTrimIndent() {
      assertThat(Strings.trimIndent("\t\t", 2)).asString().isEmpty();
      assertThat(Strings.trimIndent("foo  ", 2)).asString().isEqualTo("foo  ");
      assertThat(Strings.trimIndent(" \t foo", 2)).asString().isEqualTo("foo");
      assertThat(Strings.trimIndent(" foo\n bar", 2)).asString().isEqualTo("foo\nbar");
      assertThat(Strings.trimIndent("  foo\n\tbar", 2)).asString().isEqualTo("foo\nbar");
      assertThat(Strings.trimIndent(" foo\n\tbar", 2)).asString().isEqualTo("foo\nbar");
      assertThat(Strings.trimIndent("\tfoo\n\t\tbar", 2)).asString().isEqualTo("foo\n\tbar");
      assertThat(Strings.trimIndent("\tfoo\n  \tbar", 2)).asString().isEqualTo("foo\n\tbar");
   }
}
