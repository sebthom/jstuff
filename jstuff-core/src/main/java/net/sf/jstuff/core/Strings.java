/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.CharSequenceReader;
import net.sf.jstuff.core.io.RuntimeIOException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Strings {

   public static final class ANSIState {

      public @Nullable String bgcolor;
      public boolean blink;
      public boolean bold;
      public @Nullable String fgcolor;
      public boolean underline;

      public ANSIState() {
         reset();
      }

      public ANSIState(final ANSIState copyFrom) {
         copyFrom(copyFrom);
      }

      public void copyFrom(final @Nullable ANSIState other) {
         if (other == null)
            return;
         fgcolor = other.fgcolor;
         bgcolor = other.bgcolor;
         bold = other.bold;
         underline = other.underline;
         blink = other.blink;
      }

      public boolean isActive() {
         return fgcolor != null || bgcolor != null || bold || underline || blink;
      }

      public void reset() {
         fgcolor = null;
         bgcolor = null;
         bold = false;
         underline = false;
         blink = false;
      }

      public void setGraphicModeParameter(final int param) {
         switch (param) {
            case 0:
               reset();
               break;
            case 1:
               bold = true;
               break;
            case 4:
               underline = true;
               break;
            case 5:
               blink = true;
               break;
            case 30:
               fgcolor = "black";
               break;
            case 31:
               fgcolor = "red";
               break;
            case 32:
               fgcolor = "green";
               break;
            case 33:
               fgcolor = "yellow";
               break;
            case 34:
               fgcolor = "blue";
               break;
            case 35:
               fgcolor = "magenta";
               break;
            case 36:
               fgcolor = "cyan";
               break;
            case 37:
               fgcolor = "white";
               break;
            case 40:
               bgcolor = "black";
               break;
            case 41:
               bgcolor = "red";
               break;
            case 42:
               bgcolor = "green";
               break;
            case 43:
               bgcolor = "yellow";
               break;
            case 44:
               bgcolor = "blue";
               break;
            case 45:
               bgcolor = "magenta";
               break;
            case 46:
               bgcolor = "cyan";
               break;
            case 47:
               bgcolor = "white";
               break;
         }
      }

      public String toCSS() {
         if (isActive()) {
            final var sb = new StringBuilder();
            if (fgcolor != null) {
               sb.append("color:").append(fgcolor).append(";");
            }
            if (bgcolor != null) {
               sb.append("background-color:").append(bgcolor).append(";");
            }
            if (bold) {
               sb.append("font-weight:bold;");
            }
            if (underline) {
               sb.append("text-decoration:underline;");
            }
            if (blink) {
               sb.append("text-decoration: blink;");
            }
            return sb.toString();
         }
         return EMPTY;
      }
   }

   public static final String SPACE = " ";

   public static final String EMPTY = "";

   /**
    * \r 13 Carriage Return, new line separator on Macintosh
    */
   public static final char CR = 13;

   /**
    * \n 10 Line Feed, new line separator on Unix/Linux
    */
   public static final char LF = 10;

   /**
    * \r\n new line separator on Windows
    */
   public static final String CR_LF = "" + CR + LF;

   public static final String NEW_LINE = System.lineSeparator();

   public static final char TAB = '\t';

   public static final int INDEX_NOT_FOUND = -1;

   /**
    * See {@link StringUtils#abbreviate(String, int)}
    */
   public static String abbreviate(final String str, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.abbreviate(str, maxWidth));
   }

   /**
    * See {@link StringUtils#abbreviate(String, int, int)}
    */
   public static String abbreviate(final String str, final int offset, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.abbreviate(str, offset, maxWidth));
   }

   /**
    * See {@link StringUtils#abbreviate(String, String, int)}
    */
   public static String abbreviate(final String str, final @Nullable String abbrevMarker, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.abbreviate(str, abbrevMarker, maxWidth));
   }

   /**
    * See {@link StringUtils#abbreviate(String, String, int, int)}
    */
   public static String abbreviate(final String str, final @Nullable String abbrevMarker, final int offset, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.abbreviate(str, abbrevMarker, offset, maxWidth));
   }

   /**
    * See {@link StringUtils#abbreviateMiddle(String, String, int)}
    */
   public static String abbreviateMiddle(final String str, final @Nullable String middle, final int length) {
      return asNonNullUnsafe(StringUtils.abbreviateMiddle(str, middle, length));
   }

   /**
    * See {@link StringUtils#abbreviateMiddle(String, String, int)}
    */
   public static @Nullable String abbreviateMiddleNullable(final @Nullable String str, final @Nullable String middle, final int length) {
      return StringUtils.abbreviateMiddle(str, middle, length);
   }

   /**
    * See {@link StringUtils#abbreviate(String, int)}
    */
   public static @Nullable String abbreviateNullable(final @Nullable String str, final int maxWidth) {
      return StringUtils.abbreviate(str, maxWidth);
   }

   /**
    * See {@link StringUtils#abbreviate(String, int, int)}
    */
   public static @Nullable String abbreviateNullable(final @Nullable String str, final int offset, final int maxWidth) {
      return StringUtils.abbreviate(str, offset, maxWidth);
   }

   /**
    * See {@link StringUtils#abbreviate(String, String, int)}
    */
   public static @Nullable String abbreviateNullable(final @Nullable String str, final @Nullable String abbrevMarker, final int maxWidth) {
      return StringUtils.abbreviate(str, abbrevMarker, maxWidth);
   }

   /**
    * See {@link StringUtils#abbreviate(String, String, int, int)}
    */
   public static @Nullable String abbreviateNullable(final @Nullable String str, final @Nullable String abbrevMarker, final int offset,
         final int maxWidth) {
      return StringUtils.abbreviate(str, abbrevMarker, offset, maxWidth);
   }

   public static CharSequence ansiColorsToHTML(final CharSequence txt) {
      if (isEmpty(txt))
         return txt;
      return ansiColorsToHTML(txt, new ANSIState());
   }

   public static CharSequence ansiColorsToHTML(final CharSequence txt, final @Nullable ANSIState initialState) {
      if (isEmpty(txt))
         return txt;
      Args.notNull("initialState", initialState);

      final char esc = '\u001B';
      final var sb = new StringBuilder(txt.length());

      ANSIState effectiveState;
      if (initialState == null) {
         effectiveState = new ANSIState();
      } else {
         if (initialState.isActive()) {
            sb.append("<span style=\"").append(initialState.toCSS()).append("\">");
         }
         effectiveState = new ANSIState(initialState);
      }

      final var lookAhead = new StringBuilder(8);

      for (int i = 0, txtLen = txt.length(); i < txtLen; i++) {
         final char ch = txt.charAt(i);
         if (ch == esc && i < txtLen - 1 && txt.charAt(i + 1) == '[') {
            lookAhead.setLength(0);
            final var currentState = new ANSIState(effectiveState);
            int currentGraphicModeParam = 0;
            boolean isValidEscapeSequence = false;
            for (i = i + 2; i < txtLen; i++) {
               final char ch2 = txt.charAt(i);
               lookAhead.append(ch2);
               switch (ch2) {
                  case '0':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 0;
                     break;
                  case '1':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 1;
                     break;
                  case '2':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 2;
                     break;
                  case '3':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 3;
                     break;
                  case '4':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 4;
                     break;
                  case '5':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 5;
                     break;
                  case '6':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 6;
                     break;
                  case '7':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 7;
                     break;
                  case '8':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 8;
                     break;
                  case '9':
                     currentGraphicModeParam = currentGraphicModeParam * 10 + 9;
                     break;
                  case ';':
                     currentState.setGraphicModeParameter(currentGraphicModeParam);
                     currentGraphicModeParam = 0;
                     break;
                  case 'm':
                     currentState.setGraphicModeParameter(currentGraphicModeParam);
                     if (effectiveState.isActive()) {
                        sb.append("</span>");
                     }
                     if (currentState.isActive()) {
                        sb.append("<span style=\"").append(currentState.toCSS()).append("\">");
                     }
                     effectiveState = currentState;
                     isValidEscapeSequence = true;
                     break;
                  default:
                     // invalid character found
                     isValidEscapeSequence = true;
               }
               if (isValidEscapeSequence) {
                  break;
               }
            }
            if (!isValidEscapeSequence) {
               // in case of a missing ESC sequence delimiter, we treat the whole ESC string not as an ANSI escape sequence
               sb.append(esc).append('[').append(lookAhead);
            }
         } else {
            sb.append(ch);
         }
      }

      if (effectiveState.isActive()) {
         sb.append("</span>");
      }
      return sb;
   }

   /**
    * See {@link StringUtils#appendIfMissing(String, CharSequence, CharSequence[])}
    */

   public static String appendIfMissing(final String str, final @Nullable CharSequence suffix, final CharSequence @Nullable... suffixes) {
      return asNonNullUnsafe(StringUtils.appendIfMissing(str, suffix, suffixes));
   }

   /**
    * See {@link StringUtils#appendIfMissingIgnoreCase(String, CharSequence, CharSequence[])}
    */

   public static String appendIfMissingIgnoreCase(final String str, final @Nullable CharSequence suffix,
         final CharSequence @Nullable... suffixes) {
      return asNonNullUnsafe(StringUtils.appendIfMissingIgnoreCase(str, suffix, suffixes));
   }

   /**
    * See {@link StringUtils#appendIfMissingIgnoreCase(String, CharSequence, CharSequence[])}
    */
   public static @Nullable String appendIfMissingIgnoreCaseNullable(final @Nullable String str, final @Nullable CharSequence suffix,
         final CharSequence @Nullable... suffixes) {
      return StringUtils.appendIfMissingIgnoreCase(str, suffix, suffixes);
   }

   /**
    * See {@link StringUtils#appendIfMissing(String, CharSequence, CharSequence[])}
    */
   public static @Nullable String appendIfMissingNullable(final @Nullable String str, final @Nullable CharSequence suffix,
         final CharSequence @Nullable... suffixes) {
      return StringUtils.appendIfMissing(str, suffix, suffixes);
   }

   /**
    * See {@link StringUtils#capitalize(String)}
    */
   public static String capitalize(final String str) {
      return asNonNullUnsafe(StringUtils.capitalize(str));
   }

   /**
    * See {@link StringUtils#capitalize(String)}
    */
   public static @Nullable String capitalizeNullable(final @Nullable String str) {
      return StringUtils.capitalize(str);
   }

   /**
    * See {@link StringUtils#center(String, int)}
    */
   public static String center(final String str, final int size) {
      return asNonNullUnsafe(StringUtils.center(str, size));
   }

   /**
    * See {@link StringUtils#center(String, int, char)}
    */
   public static String center(final String str, final int size, final char padChar) {
      return asNonNullUnsafe(StringUtils.center(str, size, padChar));
   }

   /**
    * See {@link StringUtils#center(String, int, String)}
    */
   public static String center(final String str, final int size, final @Nullable String padStr) {
      return asNonNullUnsafe(StringUtils.center(str, size, padStr));
   }

   /**
    * See {@link StringUtils#center(String, int)}
    */
   public static @Nullable String centerNullable(final @Nullable String str, final int size) {
      return StringUtils.center(str, size);
   }

   /**
    * See {@link StringUtils#center(String, int, char)}
    */
   public static @Nullable String centerNullable(final @Nullable String str, final int size, final char padChar) {
      return StringUtils.center(str, size, padChar);
   }

   /**
    * See {@link StringUtils#center(String, int, String)}
    */
   public static @Nullable String centerNullable(final @Nullable String str, final int size, final @Nullable String padStr) {
      return StringUtils.center(str, size, padStr);
   }

   public static char charAt(final CharSequence text, final int index, final char resultIfOutOfBound) {
      if (index < 0 || index >= text.length())
         return resultIfOutOfBound;
      return text.charAt(index);
   }

   /**
    * See {@link StringUtils#chomp(String)}
    */
   public static String chomp(final String str) {
      return asNonNullUnsafe(StringUtils.chomp(str));
   }

   /**
    * See {@link StringUtils#chomp(String)}
    */
   public static @Nullable String chompNullable(final @Nullable String str) {
      return StringUtils.chomp(str);
   }

   /**
    * See {@link StringUtils#chop(String)}
    */
   public static String chop(final String str) {
      return asNonNullUnsafe(StringUtils.chop(str));
   }

   /**
    * See {@link StringUtils#chop(String)}
    */
   public static @Nullable String chopNullable(final @Nullable String str) {
      return StringUtils.chop(str);
   }

   /**
    * See {@link StringUtils#compare(String, String)}
    */
   public static int compare(final @Nullable String str1, final @Nullable String str2) {
      return StringUtils.compare(str1, str2);
   }

   /**
    * See {@link StringUtils#compare(String, String, boolean)}
    */
   public static int compare(final @Nullable String str1, final @Nullable String str2, final boolean nullIsLess) {
      return StringUtils.compare(str1, str2, nullIsLess);
   }

   /**
    * See {@link StringUtils#compareIgnoreCase(String, String)}
    */
   public static int compareIgnoreCase(final @Nullable String str1, final @Nullable String str2) {
      return StringUtils.compareIgnoreCase(str1, str2);
   }

   /**
    * See {@link StringUtils#compareIgnoreCase(String, String, boolean)}
    */
   public static int compareIgnoreCase(final @Nullable String str1, final @Nullable String str2, final boolean nullIsLess) {
      return StringUtils.compareIgnoreCase(str1, str2, nullIsLess);
   }

   /**
    * See {@link StringUtils#contains(CharSequence, CharSequence)}
    */
   public static boolean contains(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.contains(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#contains(CharSequence, int)}
    */
   public static boolean contains(final @Nullable CharSequence searchIn, final int searchChar) {
      return StringUtils.contains(searchIn, searchChar);
   }

   /**
    * See {@link StringUtils#containsAny(CharSequence, char[])}
    */
   public static boolean containsAny(final @Nullable CharSequence searchIn, final char @Nullable... searchChars) {
      return StringUtils.containsAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#containsAny(CharSequence, char[])}
    */
   public static boolean containsAny(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchChars) {
      return StringUtils.containsAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#containsAny(CharSequence, CharSequence[])}
    */
   public static boolean containsAny(final @Nullable CharSequence searchIn, final CharSequence @Nullable... searchFor) {
      return StringUtils.containsAny(searchIn, searchFor);
   }

   /**
    * @return true if any searchIn contains ANY of the substrings in searchFor
    */
   public static boolean containsAny(final @Nullable Collection<? extends CharSequence> searchIn, final String @Nullable... searchFor) {
      if (searchIn == null)
         return false;
      for (final CharSequence s : searchIn)
         if (containsAny(s, searchFor))
            return true;
      return false;
   }

   /**
    * See {@link StringUtils#containsAnyIgnoreCase(CharSequence, CharSequence[])}
    */
   public static boolean containsAnyIgnoreCase(final @Nullable CharSequence searchIn, final CharSequence @Nullable... searchFor) {
      return StringUtils.containsAnyIgnoreCase(searchIn, searchFor);
   }

   public static boolean containsDigit(final @Nullable CharSequence searchIn) {
      if (searchIn == null || searchIn.length() == 0)
         return false;

      for (int i = 0, l = searchIn.length(); i < l; i++)
         if (Character.isDigit((int) searchIn.charAt(i)))
            return true;
      return false;
   }

   /**
    * See {@link StringUtils#containsIgnoreCase(CharSequence, CharSequence)}
    */
   public static boolean containsIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.containsAnyIgnoreCase(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#containsNone(CharSequence, char[])}
    */
   public static boolean containsNone(final @Nullable CharSequence searchIn, final char @Nullable... searchChars) {
      return StringUtils.containsNone(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#containsNone(CharSequence, String)}
    */
   public static boolean containsNone(final @Nullable CharSequence searchIn, final @Nullable String invalidChars) {
      return StringUtils.containsNone(searchIn, invalidChars);
   }

   /**
    * See {@link StringUtils#containsOnly(CharSequence, char[])}
    */
   public static boolean containsOnly(final @Nullable CharSequence searchIn, final char @Nullable... validChars) {
      return StringUtils.containsOnly(searchIn, validChars);
   }

   /**
    * See {@link StringUtils#containsOnly(CharSequence, String)}
    */
   public static boolean containsOnly(final @Nullable CharSequence searchIn, final @Nullable String validChars) {
      return StringUtils.containsOnly(searchIn, validChars);
   }

   /**
    * See {@link StringUtils#containsWhitespace(CharSequence)}
    */
   public static boolean containsWhitespace(final @Nullable CharSequence searchIn) {
      return StringUtils.containsWhitespace(searchIn);
   }

   /**
    * See {@link StringUtils#countMatches(CharSequence, char)}
    */
   public static int countMatches(final @Nullable CharSequence searchIn, final char searchFor) {
      return StringUtils.countMatches(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#countMatches(CharSequence, CharSequence)}
    */
   public static int countMatches(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.countMatches(searchIn, searchFor);
   }

   /**
    * <p>
    * Counts how many times the substring appears in the larger String starting at the given position.
    * </p>
    *
    * @param searchIn the String to check, may be null
    * @param searchFor the substring to count, may be null
    * @return the number of occurrences, 0 if either String is <code>null</code>
    */
   public static int countMatches(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int startAt) {
      if (searchIn == null || searchFor == null //
            || searchIn.length() == 0 || searchFor.length() == 0 //
            || startAt >= searchIn.length())
         return 0;

      int count = 0;
      int foundAt;
      if (startAt >= 0) {
         foundAt = startAt;
      } else if (startAt < -searchIn.length()) {
         foundAt = 0;
      } else {
         foundAt = searchIn.length() + startAt;
      }
      foundAt--;
      while ((foundAt = indexOf(searchIn, searchFor, foundAt + 1)) > -1) {
         count++;
      }
      return count;
   }

   /**
    * <p>
    * Counts how many times the substring appears in the larger String starting at the given position.
    * </p>
    *
    * @param searchIn the String to check, may be null
    * @param searchFor the substring to count
    * @return the number of occurrences, 0 if searchIn is <code>null</code>
    */
   public static int countMatches(final @Nullable String searchIn, final char searchFor) {
      return countMatches(searchIn, searchIn, 0);
   }

   /**
    * <p>
    * Counts how many times the substring appears in the larger String starting at the given position.
    * </p>
    *
    * @param searchIn the String to check, may be null
    * @param searchFor the substring to count
    * @return the number of occurrences, 0 if searchIn is <code>null</code>
    */
   public static int countMatches(final @Nullable String searchIn, final char searchFor, final int startAt) {
      if (searchIn == null || searchIn.isEmpty() //
            || startAt >= searchIn.length())
         return 0;

      int count = 0;
      int foundAt;
      if (startAt >= 0) {
         foundAt = startAt;
      } else if (startAt < -searchIn.length()) {
         foundAt = 0;
      } else {
         foundAt = searchIn.length() + startAt;
      }
      foundAt--;
      while ((foundAt = searchIn.indexOf(searchFor, foundAt + 1)) > -1) {
         count++;
      }
      return count;
   }

   /**
    * <p>
    * Counts how many times the substring appears in the larger String starting at the given position.
    * </p>
    *
    * @param searchIn the String to check, may be null
    * @param searchFor the substring to count, may be null
    * @return the number of occurrences, 0 if either String is <code>null</code>
    */
   public static int countMatches(final @Nullable String searchIn, final @Nullable String searchFor) {
      return countMatches(searchIn, searchFor, 0);
   }

   /**
    * <p>
    * Counts how many times the substring appears in the larger String starting at the given position.
    * </p>
    *
    * @param searchIn the String to check, may be null
    * @param searchFor the substring to count, may be null
    * @return the number of occurrences, 0 if either String is <code>null</code>
    */
   public static int countMatches(final @Nullable String searchIn, final @Nullable String searchFor, final int startAt) {
      if (searchIn == null || searchFor == null //
            || searchIn.isEmpty() || searchFor.isEmpty() //
            || startAt >= searchIn.length())
         return 0;

      int count = 0;
      int foundAt;
      if (startAt >= 0) {
         foundAt = startAt;
      } else if (startAt < -searchIn.length()) {
         foundAt = 0;
      } else {
         foundAt = searchIn.length() + startAt;
      }
      foundAt--;
      while ((foundAt = searchIn.indexOf(searchFor, foundAt + 1)) > -1) {
         count++;
      }
      return count;
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T defaultIfBlank(final @Nullable T str, final T defaultStr) {
      return str == null || isBlank(str) ? defaultStr : str;
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T defaultIfEmpty(final @Nullable T str, final T defaultStr) {
      return str == null || str.length() == 0 ? defaultStr : str;
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T defaultIfNull(final @Nullable T str, final T defaultStr) {
      return str == null ? defaultStr : str;
   }

   /**
    * See {@link StringUtils#deleteWhitespace(String)}
    */
   public static String deleteWhitespace(final String str) {
      return asNonNullUnsafe(StringUtils.deleteWhitespace(str));
   }

   /**
    * See {@link StringUtils#deleteWhitespace(String)}
    */
   public static @Nullable String deleteWhitespaceNullable(final @Nullable String str) {
      return StringUtils.deleteWhitespace(str);
   }

   /**
    * See {@link StringUtils#difference(String, String)}
    */
   public static String difference(final String str1, final @Nullable String str2) {
      return asNonNullUnsafe(StringUtils.difference(str1, str2));
   }

   /**
    * See {@link StringUtils#difference(String, String)}
    */
   public static @Nullable String differenceNullable(final @Nullable String str1, final @Nullable String str2) {
      return StringUtils.difference(str1, str2);
   }

   public static String emptyIfBlank(final @Nullable Object obj) {
      if (obj == null)
         return EMPTY;
      final var str = obj.toString();
      return str.isBlank() ? EMPTY : str;
   }

   public static String emptyIfNull(final @Nullable Object obj) {
      return obj == null ? EMPTY : obj.toString();
   }

   public static boolean endsWith(final @Nullable CharSequence str, final char ch) {
      return str != null && str.length() > 0 && str.charAt(str.length() - 1) == ch;
   }

   /**
    * See {@link StringUtils#endsWith(CharSequence, CharSequence)}
    */
   public static boolean endsWith(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.endsWith(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#endsWithAny(CharSequence, CharSequence[])}
    */
   public static boolean endsWithAny(final @Nullable CharSequence searchIn, final CharSequence @Nullable... searchFor) {
      return StringUtils.endsWithAny(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#endsWithIgnoreCase(CharSequence, CharSequence)}
    */
   public static boolean endsWithIgnoreCase(final @Nullable CharSequence str, final @Nullable CharSequence suffix) {
      return StringUtils.endsWithIgnoreCase(str, suffix);
   }

   /**
    * See {@link StringUtils#equals(CharSequence, CharSequence)}
    */
   public static boolean equals(final @Nullable CharSequence left, final @Nullable CharSequence right) {
      return StringUtils.equals(left, right);
   }

   public static boolean equals(final @Nullable String left, final @Nullable String right) {
      if (left == right)
         return true;

      if (left == null)
         return false;

      return left.equals(right);
   }

   /**
    * See {@link StringUtils#equalsAny(CharSequence, CharSequence[])}
    */
   public static boolean equalsAny(final @Nullable CharSequence string, final @Nullable CharSequence... searchStrings) {
      return StringUtils.equalsAny(string, searchStrings);
   }

   /**
    * See {@link StringUtils#equalsAnyIgnoreCase(CharSequence, CharSequence[])}
    */
   public static boolean equalsAnyIgnoreCase(final @Nullable CharSequence string, final @Nullable CharSequence... searchStrings) {
      return StringUtils.equalsAnyIgnoreCase(string, searchStrings);
   }

   /**
    * See {@link StringUtils#equalsAnyIgnoreCase(CharSequence, CharSequence[])}
    */
   public static boolean equalsIgnoreCase(final @Nullable CharSequence left, final @Nullable CharSequence right) {
      return StringUtils.equalsIgnoreCase(left, right);
   }

   public static boolean equalsIgnoreCase(final @Nullable String left, final @Nullable String right) {
      if (left == right)
         return true;

      if (left == null)
         return false;

      return left.equalsIgnoreCase(right);
   }

   /**
    * See {@link StringUtils#firstNonBlank(CharSequence[])}
    */
   @SafeVarargs
   public static <T extends @Nullable CharSequence> T firstNonBlank(final T @Nullable... values) {
      return StringUtils.firstNonBlank(values);
   }

   /**
    * See {@link StringUtils#firstNonEmpty(CharSequence[])}
    */
   @SafeVarargs
   public static <T extends @Nullable CharSequence> T firstNonEmpty(final T... values) {
      return StringUtils.firstNonEmpty(values);
   }

   /**
    * See {@link StringUtils#getBytes(String, Charset)}
    */
   public static byte[] getBytes(final @Nullable String string, final @Nullable Charset charset) {
      return StringUtils.getBytes(string, charset);
   }

   /**
    * See {@link StringUtils#getBytes(String, String)}
    */
   public static byte[] getBytes(final @Nullable String string, final @Nullable String charset) throws UnsupportedEncodingException {
      return StringUtils.getBytes(string, charset);
   }

   /**
    * See {@link StringUtils#getCommonPrefix(String[])}
    */
   public static String getCommonPrefix(final String @Nullable... strs) {
      return StringUtils.getCommonPrefix(strs);
   }

   /**
    * See {@link StringUtils#getDigits(String)}
    */
   public static String getDigits(final String str) {
      return asNonNullUnsafe(StringUtils.getDigits(str));
   }

   /**
    * See {@link StringUtils#getDigits(String)}
    */
   public static @Nullable String getDigitsNullable(final @Nullable String str) {
      return StringUtils.getDigits(str);
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T getIfBlank(final T str, final @NonNull Supplier<T> defaultSupplier) {
      return isBlank(str) ? defaultSupplier.get() : str;
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T getIfEmpty(final T str, final @NonNull Supplier<T> defaultSupplier) {
      return isEmpty(str) ? defaultSupplier.get() : str;
   }

   @NonNullByDefault({})
   public static <T extends CharSequence> T getIfNull(final T str, final @NonNull Supplier<T> defaultSupplier) {
      return str == null ? defaultSupplier.get() : str;
   }

   /**
    * @return null if input does not contain a new line separator.
    */
   @Nullable
   public static String getNewLineSeparator(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;
      char lastChar = 0;
      for (int i = 0, txtLen = txt.length(); i < txtLen; i++) {
         final char ch = txt.charAt(i);

         if (lastChar == '\r') {
            if (ch == '\n')
               return "\r\n";
            return "\r";
         }
         if (ch == '\n')
            return "\n";
         lastChar = ch;
      }
      return lastChar == '\r' ? "\r" : null;
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static CharSequence globToRegex(final String globPattern) {
      return asNonNullUnsafe(globToRegexNullable(globPattern));
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static @Nullable CharSequence globToRegexNullable(final @Nullable String globPattern) {
      if (globPattern == null || globPattern.length() == 0)
         return globPattern;

      final var sb = new StringBuilder();
      final char[] chars = globPattern.toCharArray();
      char chPrev = 0;
      final char escapeCHAR = '\\';
      int groupDepth = 0;
      for (int idx = 0, l = chars.length; idx < l; idx++) {
         char ch = chars[idx];
         switch (ch) {
            case escapeCHAR:
               if (chPrev == escapeCHAR) {
                  // "\\" => "\\"
                  sb.append(escapeCHAR).append(escapeCHAR);
               }
               break;
            case '/':
            case '$':
            case '.':
            case '(':
            case ')':
               sb.append(escapeCHAR).append(ch);
               break;
            case '?':
               if (chPrev == escapeCHAR) {
                  // "\?" => "\?"
                  sb.append(escapeCHAR).append('?');
               } else {
                  // "?" => "[^\\^\/]"
                  sb.append("[^\\\\^\\/]");
               }
               break;
            case '{':
               if (chPrev == escapeCHAR) {
                  // "\{" => "\{"
                  sb.append(escapeCHAR).append('{');
               } else {
                  groupDepth++;
                  sb.append('(');
               }
               break;
            case '}':
               if (chPrev == escapeCHAR) {
                  // "\}" => "\}"
                  sb.append(escapeCHAR).append('}');
               } else {
                  groupDepth--;
                  sb.append(')');
               }
               break;
            case ',':
               if (chPrev == escapeCHAR) {
                  sb.append(escapeCHAR).append(',');
               } else {
                  // "," => "|" if in group or => "," if not in group
                  sb.append(groupDepth > 0 ? '|' : ',');
               }
               break;
            case '!':
               if (chPrev == '[') {
                  sb.append('^'); // "[!" => "[^"
               } else {
                  sb.append('!');
               }
               break;
            case '*':
               if (Strings.charAt(globPattern, idx + 1, (char) 0) == '*') { // **
                  if (Strings.charAt(globPattern, idx + 2, (char) 0) == '/') // **/
                     if (Strings.charAt(globPattern, idx + 3, (char) 0) == '*') {
                        // "**/*" => ".*"
                        sb.append(".*");
                        idx = idx + 3;
                     } else {
                        // "**/" => "(.*/)?"
                        sb.append("(.*/)?");
                        idx = idx + 2;
                        ch = '/';
                     }
                  else {
                     // "**" => ".*"
                     sb.append(".*");
                     idx++;
                  }
               } else {
                  // "*" => "[^\\^\/]*"
                  sb.append("[^\\\\^\\/]*");
               }
               break;

            default:
               if (chPrev == escapeCHAR) {
                  sb.append(escapeCHAR);
               }
               sb.append(ch);
         }

         chPrev = ch;
      }
      sb.append('$');
      return sb;
   }

   public static CharSequence htmlEncode(final CharSequence text) {
      return asNonNullUnsafe(htmlEncodeNullable(text));
   }

   public static @Nullable CharSequence htmlEncodeNullable(final @Nullable CharSequence text) {
      if (text == null || text.length() == 0)
         return text;

      final int textLen = text.length();
      final var sb = new StringBuilder(textLen);
      boolean isFirstSpace = true;
      for (int i = 0; i < textLen; i++) {
         final char ch = text.charAt(i);

         switch (ch) {
            case ' ':
               if (isFirstSpace) {
                  sb.append(' ');
                  isFirstSpace = false;
               } else {
                  sb.append("&nbsp;");
               }
               break;

            case '&':
               sb.append("&amp;");
               break;

            case '"':
               sb.append("&quot;");
               break;

            case '\'':
               // http://stackoverflow.com/a/2083770
               sb.append("&#039;");
               break;

            case '<':
               sb.append("&lt;");
               break;

            case '>':
               sb.append("&gt;");
               break;

            case Strings.LF:
               sb.append("&lt;br/&gt;");
               break;

            default:
               if (ch < 128) {
                  sb.append(ch);
               } else {
                  sb.append("&#").append((int) ch).append(';');
               }
         }
         if (ch != ' ') {
            isFirstSpace = true;
         }
      }
      return sb;
   }

   public static CharSequence htmlToPlainText(final CharSequence html) {
      return asNonNullUnsafe(htmlToPlainText(html));
   }

   @SuppressWarnings("resource")
   public static @Nullable CharSequence htmlToPlainTextNullable(final @Nullable CharSequence html) {
      if (html == null || html.length() == 0)
         return html;

      final var sb = new StringBuilder();
      try {
         final var pd = new ParserDelegator();
         pd.parse(new CharSequenceReader(html), new ParserCallback() {
            @Override
            public void handleText(final char[] text, final int pos) {
               sb.append(text);
            }
         }, true);
      } catch (final IOException ex) {
         throw new RuntimeIOException(ex);
      }
      return sb;
   }

   /**
    * See {@link StringUtils#indexOf(CharSequence, CharSequence)}
    */
   public static int indexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.indexOf(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#indexOf(CharSequence, CharSequence, int)}
    */
   public static int indexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int startPos) {
      return StringUtils.indexOf(searchIn, searchFor, startPos);
   }

   /**
    * See {@link StringUtils#indexOf(CharSequence, int)}
    */
   public static int indexOf(final @Nullable CharSequence searchIn, final int searchChar) {
      return StringUtils.indexOf(searchIn, searchChar);
   }

   /**
    * See {@link StringUtils#indexOf(CharSequence, int, int)}
    */
   public static int indexOf(final @Nullable CharSequence searchIn, final int searchChar, final int startPos) {
      return StringUtils.indexOf(searchIn, searchChar, startPos);
   }

   /**
    * See {@link StringUtils#indexOfAny(CharSequence, char[])}
    */
   public static int indexOfAny(final @Nullable CharSequence searchIn, final char @Nullable... searchChars) {
      return StringUtils.indexOfAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#indexOfAny(CharSequence, CharSequence[])}
    */
   public static int indexOfAny(final @Nullable CharSequence searchIn, final CharSequence @Nullable... searchFor) {
      return StringUtils.indexOfAny(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#indexOfAny(CharSequence, String)}
    */
   public static int indexOfAny(final @Nullable CharSequence searchIn, final @Nullable String searchChars) {
      return StringUtils.indexOfAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#indexOfAnyBut(CharSequence, char[])}
    */
   public static int indexOfAnyBut(final @Nullable CharSequence searchIn, final char @Nullable... searchChars) {
      return StringUtils.indexOfAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#indexOfAnyBut(CharSequence, CharSequence)}
    */
   public static int indexOfAnyBut(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchChars) {
      return StringUtils.indexOfAny(searchIn, searchChars);
   }

   /**
    * See {@link StringUtils#indexOfDifference(CharSequence[])}
    */
   public static int indexOfDifference(final CharSequence @Nullable... css) {
      return StringUtils.indexOfDifference(css);
   }

   /**
    * See {@link StringUtils#indexOfDifference(CharSequence, CharSequence)}
    */
   public static int indexOfDifference(final @Nullable CharSequence cs1, final @Nullable CharSequence cs2) {
      return StringUtils.indexOfDifference(cs1, cs2);
   }

   /**
    * See {@link StringUtils#indexOfIgnoreCase(CharSequence, CharSequence)}
    */
   public static int indexOfIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.indexOfIgnoreCase(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#indexOfIgnoreCase(CharSequence, CharSequence, int)}
    */
   public static int indexOfIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int startPos) {
      return StringUtils.indexOfIgnoreCase(searchIn, searchFor, startPos);
   }

   /**
    * See {@link StringUtils#isAllBlank(CharSequence[])}
    */
   public static boolean isAllBlank(final CharSequence @Nullable... css) {
      return StringUtils.isAllBlank(css);
   }

   /**
    * See {@link StringUtils#isAllEmpty(CharSequence[])}
    */
   public static boolean isAllEmpty(final CharSequence @Nullable... css) {
      return StringUtils.isAllEmpty(css);
   }

   /**
    * See {@link StringUtils#isAllLowerCase(CharSequence)}
    */
   public static boolean isAllLowerCase(final @Nullable CharSequence cs) {
      return StringUtils.isAllLowerCase(cs);
   }

   /**
    * See {@link StringUtils#isAllUpperCase(CharSequence)}
    */
   public static boolean isAllUpperCase(final @Nullable CharSequence cs) {
      return StringUtils.isAllUpperCase(cs);
   }

   /**
    * See {@link StringUtils#isAlpha(CharSequence)}
    */
   public static boolean isAlpha(final @Nullable CharSequence cs) {
      return StringUtils.isAlpha(cs);
   }

   /**
    * See {@link StringUtils#isAlphanumeric(CharSequence)}
    */
   public static boolean isAlphanumeric(final @Nullable CharSequence cs) {
      return StringUtils.isAlphanumeric(cs);
   }

   /**
    * See {@link StringUtils#isAlphanumericSpace(CharSequence)}
    */
   public static boolean isAlphanumericSpace(final @Nullable CharSequence cs) {
      return StringUtils.isAlphanumericSpace(cs);
   }

   /**
    * See {@link StringUtils#isAlphaSpace(CharSequence)}
    */
   public static boolean isAlphaSpace(final @Nullable CharSequence cs) {
      return StringUtils.isAlphaSpace(cs);
   }

   /**
    * See {@link StringUtils#isAnyBlank(CharSequence[])}
    */
   public static boolean isAnyBlank(final CharSequence @Nullable... css) {
      return StringUtils.isAnyBlank(css);
   }

   /**
    * See {@link StringUtils#isAnyEmpty(CharSequence[])}
    */
   public static boolean isAnyEmpty(final CharSequence @Nullable... css) {
      return StringUtils.isAnyBlank(css);
   }

   /**
    * See {@link StringUtils#isAsciiPrintable(CharSequence)}
    */
   public static boolean isAsciiPrintable(final @Nullable CharSequence cs) {
      return StringUtils.isAsciiPrintable(cs);
   }

   /**
    * See {@link StringUtils#isBlank(CharSequence)}
    */
   public static boolean isBlank(final @Nullable CharSequence cs) {
      return StringUtils.isBlank(cs);
   }

   /**
    * See {@link StringUtils#isEmpty(CharSequence)}
    */
   public static boolean isEmpty(final @Nullable CharSequence cs) {
      return StringUtils.isEmpty(cs);
   }

   /**
    * See {@link StringUtils#isMixedCase(CharSequence)}
    */
   public static boolean isMixedCase(final @Nullable CharSequence cs) {
      return StringUtils.isMixedCase(cs);
   }

   /**
    * See {@link StringUtils#isNoneBlank(CharSequence[])}
    */
   public static boolean isNoneBlank(final CharSequence @Nullable... css) {
      return StringUtils.isNoneBlank(css);
   }

   /**
    * See {@link StringUtils#isNoneEmpty(CharSequence[])}
    */
   public static boolean isNoneEmpty(final CharSequence @Nullable... css) {
      return StringUtils.isNoneEmpty(css);
   }

   /**
    * See {@link StringUtils#isNotBlank(CharSequence)}
    */
   public static boolean isNotBlank(final @Nullable CharSequence cs) {
      return StringUtils.isNotBlank(cs);
   }

   /**
    * See {@link StringUtils#isNotEmpty(CharSequence)}
    */
   public static boolean isNotEmpty(final @Nullable CharSequence cs) {
      return StringUtils.isNotEmpty(cs);
   }

   /**
    * See {@link StringUtils#isNumeric(CharSequence)}
    */
   public static boolean isNumeric(final @Nullable CharSequence cs) {
      return StringUtils.isNumeric(cs);
   }

   /**
    * See {@link StringUtils#isNumericSpace(CharSequence)}
    */
   public static boolean isNumericSpace(final @Nullable CharSequence cs) {
      return StringUtils.isNumericSpace(cs);
   }

   /**
    * See {@link StringUtils#isWhitespace(CharSequence)}
    */
   public static boolean isWhitespace(final @Nullable CharSequence cs) {
      return StringUtils.isWhitespace(cs);
   }

   /**
    * See {@link StringUtils#join(boolean[], char)}
    */
   public static String join(final boolean[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(boolean[], char, int, int)}
    */
   public static String join(final boolean[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(byte[], char)}
    */
   public static String join(final byte[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(byte[], char, int, int)}
    */
   public static String join(final byte[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(char[], char)}
    */
   public static String join(final char[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(char[], char, int, int)}
    */
   public static String join(final char[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(double[], char)}
    */
   public static String join(final double[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(double[], char, int, int)}
    */
   public static String join(final double[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(float[], char)}
    */
   public static String join(final float[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(float[], char, int, int)}
    */
   public static String join(final float[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(int[], char)}
    */
   public static String join(final int[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(int[], char, int, int)}
    */
   public static String join(final int[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   public static String join(final Iterable<?> it) {
      return asNonNullUnsafe(StringUtils.join(it, EMPTY));
   }

   /**
    * See {@link StringUtils#join(Iterable, char)}
    */
   public static String join(final Iterable<?> it, final char separator) {
      return asNonNullUnsafe(StringUtils.join(it, separator));
   }

   public static CharSequence join(final Iterable<?> it, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.join(it, separator));
   }

   public static <T> String join(final Iterable<T> it, final char separator, final Function<T, Object> transform) {
      return asNonNullUnsafe(joinNullable(it, separator, transform));
   }

   public static <T> String join(final Iterable<T> it, final @Nullable String separator, final Function<T, Object> transform) {
      return asNonNullUnsafe(joinNullable(it, separator, transform));
   }

   /**
    * See {@link StringUtils#join(Iterator, char)}
    */
   public static String join(final Iterator<?> it, final char separator) {
      return asNonNullUnsafe(StringUtils.join(it, separator));
   }

   /**
    * See {@link StringUtils#join(Iterator, String)}
    */
   public static String join(final Iterator<?> it, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.join(it, separator));
   }

   public static <T> String join(final Iterator<T> it, final char separator, final Function<T, Object> transform) {
      return asNonNullUnsafe(joinNullable(it, separator, transform));
   }

   public static <T> String join(final Iterator<T> it, @Nullable final String separator, final Function<T, Object> transform) {
      return asNonNullUnsafe(joinNullable(it, separator, transform));
   }

   public static String join(final List<?> list, final char separator) {
      return asNonNullUnsafe(joinNullable(list, separator));
   }

   /**
    * See {@link StringUtils#join(List, char, int, int)}
    */
   public static String join(final List<?> list, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(list, separator, startIndex, endIndex));
   }

   public static String join(final List<?> list, final @Nullable String separator) {
      return asNonNullUnsafe(joinNullable(list, separator));
   }

   /**
    * See {@link StringUtils#join(List, String, int, int)}
    */
   public static String join(final List<?> list, final @Nullable String separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(list, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(long[], char)}
    */
   public static String join(final long[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(long[], char, int, int)}
    */
   public static String join(final long[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(Object[], char)}
    */
   public static String join(final Object[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(Object[], char, int, int)}
    */
   public static String join(final Object[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(Object[], String)}
    */
   public static String join(final Object[] array, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(Object[], String, int, int)}
    */
   public static String join(final Object[] array, final @Nullable String separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(short[], char)}
    */
   public static String join(final short[] array, final char separator) {
      return asNonNullUnsafe(StringUtils.join(array, separator));
   }

   /**
    * See {@link StringUtils#join(short[], char, int, int)}
    */
   public static String join(final short[] array, final char separator, final int startIndex, final int endIndex) {
      return asNonNullUnsafe(StringUtils.join(array, separator, startIndex, endIndex));
   }

   /**
    * See {@link StringUtils#join(Object...)}
    */
   @SafeVarargs
   public static <T> String join(final T... elements) {
      return asNonNullUnsafe(StringUtils.join(elements));
   }

   /**
    * See {@link StringUtils#join(boolean[], char)}
    */
   public static @Nullable String joinNullable(final boolean @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(boolean[], char, int, int)}
    */
   public static @Nullable String joinNullable(final boolean @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(byte[], char)}
    */
   public static @Nullable String joinNullable(final byte @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(byte[], char, int, int)}
    */
   public static @Nullable String joinNullable(final byte @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(char[], char)}
    */
   public static @Nullable String joinNullable(final char @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(char[], char, int, int)}
    */
   public static @Nullable String joinNullable(final char @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(double[], char)}
    */
   public static @Nullable String joinNullable(final double @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(double[], char, int, int)}
    */
   public static @Nullable String joinNullable(final double @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(float[], char)}
    */
   public static @Nullable String joinNullable(final float @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(float[], char, int, int)}
    */
   public static @Nullable String joinNullable(final float @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(int[], char)}
    */
   public static @Nullable String joinNullable(final int @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(int[], char, int, int)}
    */
   public static @Nullable String joinNullable(final int @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   public static @Nullable String joinNullable(final @Nullable Iterable<?> it) {
      return joinNullable(it, EMPTY);
   }

   /**
    * See {@link StringUtils#join(Iterable, char)}
    */
   public static @Nullable String joinNullable(final @Nullable Iterable<?> it, final char separator) {
      return StringUtils.join(it, separator);
   }

   /**
    * See {@link StringUtils#join(Iterable, String)}
    */
   public static @Nullable String joinNullable(final @Nullable Iterable<?> it, final @Nullable String separator) {
      return StringUtils.join(it, separator);
   }

   public static <T> @Nullable String joinNullable(final @Nullable Iterable<T> it, final char separator,
         final Function<T, Object> transform) {
      if (it == null)
         return null;

      return join(it.iterator(), separator, transform);
   }

   public static <T> @Nullable String joinNullable(final @Nullable Iterable<T> it, final @Nullable CharSequence separator,
         final Function<T, Object> transform) {
      if (it == null)
         return null;

      return join(it.iterator(), separator, transform);
   }

   /**
    * See {@link StringUtils#join(Iterator, char)}
    */
   public static @Nullable String joinNullable(final @Nullable Iterator<?> it, final char separator) {
      return StringUtils.join(it, separator);
   }

   /**
    * See {@link StringUtils#join(Iterator, String)}
    */
   public static @Nullable String joinNullable(final @Nullable Iterator<?> it, final @Nullable String separator) {
      return StringUtils.join(it, separator);
   }

   public static <T> @Nullable String joinNullable(final @Nullable Iterator<T> it, final char separator,
         final Function<T, Object> transform) {
      if (it == null)
         return null;
      if (!it.hasNext())
         return EMPTY;

      Args.notNull("transform", transform);

      final T first = it.next();
      if (!it.hasNext())
         return Objects.toString(transform.apply(first), EMPTY);

      final var sb = new StringBuilder(128);
      sb.append(Objects.toString(transform.apply(first), EMPTY));

      while (it.hasNext()) {
         sb.append(separator);
         final T obj = it.next();
         sb.append(Objects.toString(transform.apply(obj), EMPTY));
      }

      return sb.toString();
   }

   public static <T> @Nullable String joinNullable(final @Nullable Iterator<T> it, final @Nullable CharSequence separator,
         final Function<T, Object> transform) {
      if (it == null)
         return null;
      if (!it.hasNext())
         return EMPTY;

      Args.notNull("transform", transform);

      final T first = it.next();
      if (!it.hasNext())
         return Objects.toString(transform.apply(first), EMPTY);

      final var sb = new StringBuilder(128);
      sb.append(Objects.toString(transform.apply(first), EMPTY));

      while (it.hasNext()) {
         if (separator != null) {
            sb.append(separator);
         }
         final T obj = it.next();
         sb.append(Objects.toString(transform.apply(obj), EMPTY));
      }

      return sb.toString();
   }

   public static @Nullable String joinNullable(final @Nullable List<?> list, final char separator) {
      if (list == null)
         return null;
      return StringUtils.join(list.iterator(), separator);
   }

   /**
    * See {@link StringUtils#join(List, char, int, int)}
    */
   public static @Nullable String joinNullable(final @Nullable List<?> list, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(list, separator, startIndex, endIndex);
   }

   public static @Nullable String joinNullable(final @Nullable List<?> list, final @Nullable String separator) {
      if (list == null)
         return null;
      return StringUtils.join(list.iterator(), separator);
   }

   /**
    * See {@link StringUtils#join(List, String, int, int)}
    */
   public static @Nullable String joinNullable(final @Nullable List<?> list, final @Nullable String separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(list, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(long[], char)}
    */
   public static @Nullable String joinNullable(final long @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(long[], char, int, int)}
    */
   public static @Nullable String joinNullable(final long @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(Object[], char)}
    */
   public static @Nullable String joinNullable(final Object @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(Object[], char, int, int)}
    */
   public static @Nullable String joinNullable(final Object @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(Object[], String)}
    */
   public static @Nullable String joinNullable(final Object @Nullable [] array, final @Nullable String separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(Object[], String, int, int)}
    */
   public static @Nullable String joinNullable(final Object @Nullable [] array, final @Nullable String separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(short[], char)}
    */
   public static @Nullable String joinNullable(final short @Nullable [] array, final char separator) {
      return StringUtils.join(array, separator);
   }

   /**
    * See {@link StringUtils#join(short[], char, int, int)}
    */
   public static @Nullable String joinNullable(final short @Nullable [] array, final char separator, final int startIndex,
         final int endIndex) {
      return StringUtils.join(array, separator, startIndex, endIndex);
   }

   /**
    * See {@link StringUtils#join(Object...)}
    */
   @SafeVarargs
   public static <T> @Nullable String joinNullable(final T @Nullable... elements) {
      return StringUtils.join(elements);
   }

   /**
    * See {@link StringUtils#joinWith(String, Object...)}
    */
   public static String joinWith(final String separator, final Object... elements) {
      return asNonNullUnsafe(joinWithNullable(separator, elements));
   }

   /**
    * See {@link StringUtils#joinWith(String, Object...)}
    */
   public static @Nullable String joinWithNullable(final @Nullable String separator, final Object @Nullable... elements) {
      if (elements == null)
         return null;
      return StringUtils.joinWith(separator, elements);
   }

   /**
    * See {@link StringUtils#lastIndexOf(CharSequence, CharSequence)}
    */
   public static int lastIndexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.lastIndexOf(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#lastIndexOf(CharSequence, CharSequence, int)}
    */
   public static int lastIndexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int startPos) {
      return StringUtils.lastIndexOf(searchIn, searchFor, startPos);
   }

   /**
    * See {@link StringUtils#lastIndexOf(CharSequence, int)}
    */
   public static int lastIndexOf(final @Nullable CharSequence searchIn, final int searchChar) {
      return StringUtils.lastIndexOf(searchIn, searchChar);
   }

   /**
    * See {@link StringUtils#lastIndexOf(CharSequence, int, int)}
    */
   public static int lastIndexOf(final @Nullable CharSequence searchIn, final int searchChar, final int startPos) {
      return StringUtils.lastIndexOf(searchIn, searchChar, startPos);
   }

   /**
    * See {@link StringUtils#lastIndexOfAny(CharSequence, CharSequence[])}
    */
   public static int lastIndexOfAny(final @Nullable CharSequence searchIn, final CharSequence @Nullable... searchFor) {
      return StringUtils.lastIndexOfAny(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#lastIndexOfIgnoreCase(CharSequence, CharSequence)}
    */
   public static int lastIndexOfIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor) {
      return StringUtils.lastIndexOfIgnoreCase(searchIn, searchFor);
   }

   /**
    * See {@link StringUtils#lastIndexOfIgnoreCase(CharSequence, CharSequence, int)}
    */
   public static int lastIndexOfIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor,
         final int startPos) {
      return StringUtils.lastIndexOfIgnoreCase(searchIn, searchFor, startPos);
   }

   /**
    * See {@link StringUtils#lastOrdinalIndexOf(CharSequence, CharSequence, int)}
    */
   public static int lastOrdinalIndexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int ordinal) {
      return StringUtils.lastOrdinalIndexOf(searchIn, searchFor, ordinal);
   }

   /**
    * See {@link StringUtils#left(String, int)}
    */
   public static String left(final String str, final int len) {
      return asNonNullUnsafe(StringUtils.left(str, len));
   }

   /**
    * See {@link StringUtils#left(String, int)}
    */
   public static @Nullable String leftNullable(final @Nullable String str, final int len) {
      return StringUtils.left(str, len);
   }

   /**
    * See {@link StringUtils#leftPad(String, int)}
    */
   public static String leftPad(final String str, final int size) {
      return asNonNullUnsafe(StringUtils.leftPad(str, size));
   }

   /**
    * See {@link StringUtils#leftPad(String, int, char)}
    */
   public static String leftPad(final String str, final int size, final char padChar) {
      return asNonNullUnsafe(StringUtils.leftPad(str, size, padChar));
   }

   /**
    * See {@link StringUtils#leftPad(String, int, String)}
    */
   public static String leftPad(final String str, final int size, final @Nullable String padStr) {
      return asNonNullUnsafe(StringUtils.leftPad(str, size, padStr));
   }

   /**
    * See {@link StringUtils#leftPad(String, int)}
    */
   public static @Nullable String leftPadNullable(final @Nullable String str, final int size) {
      return StringUtils.leftPad(str, size);
   }

   /**
    * See {@link StringUtils#leftPad(String, int, char)}
    */
   public static @Nullable String leftPadNullable(final @Nullable String str, final int size, final char padChar) {
      return StringUtils.leftPad(str, size, padChar);
   }

   /**
    * See {@link StringUtils#leftPad(String, int, String)}
    */
   public static @Nullable String leftPadNullable(final @Nullable String str, final int size, final @Nullable String padStr) {
      return StringUtils.leftPad(str, size, padStr);
   }

   /**
    * See {@link StringUtils#length(CharSequence)}
    */
   public static int length(final @Nullable CharSequence cs) {
      return StringUtils.length(cs);
   }

   public static String lowerCase(final CharSequence txt) {
      return asNonNullUnsafe(lowerCaseNullable(txt));
   }

   public static String lowerCase(final Object obj) {
      return asNonNullUnsafe(lowerCaseNullable(obj));
   }

   public static String lowerCase(final Object obj, final @Nullable Locale locale) {
      return asNonNullUnsafe(lowerCaseNullable(obj, locale));
   }

   /**
    * See {@link StringUtils#lowerCase(String)}
    */
   public static String lowerCase(final String str) {
      return asNonNullUnsafe(StringUtils.lowerCase(str));
   }

   /**
    * See {@link StringUtils#lowerCase(String, Locale)}
    */
   public static String lowerCase(final String str, final @Nullable Locale locale) {
      return asNonNullUnsafe(StringUtils.lowerCase(str, locale));
   }

   /**
    * Uncapitalize the first character of the given character sequence.
    * If you need to uncapitalize all words in a string use commons-text's <code>WordUtils.uncapitalize(String)</code>
    */
   public static String lowerCaseFirstChar(final CharSequence txt) {
      return asNonNullUnsafe(lowerCaseFirstCharNullable(txt));
   }

   /**
    * Uncapitalize the first character of the given character sequence.
    * If you need to uncapitalize all words in a string use commons-text's <code>WordUtils.uncapitalize(String)</code>
    */
   public static @Nullable String lowerCaseFirstCharNullable(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;

      final int len = txt.length();
      if (len == 0)
         return EMPTY;
      final String firstChar = String.valueOf(Character.toLowerCase(txt.charAt(0)));
      if (len == 1)
         return firstChar;
      return firstChar + txt.subSequence(1, len);
   }

   public static @Nullable String lowerCaseNullable(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;
      if (txt.length() == 0)
         return EMPTY;
      if (txt instanceof String)
         return ((String) txt).toLowerCase();

      final var len = txt.length();
      final var chars = new char[len];
      for (int i = 0; i < len; i++) {
         chars[i] = Character.toLowerCase(txt.charAt(i));
      }
      return new String(chars);
   }

   public static @Nullable String lowerCaseNullable(final @Nullable Object obj) {
      if (obj == null)
         return null;
      return StringUtils.lowerCase(obj.toString());
   }

   public static @Nullable String lowerCaseNullable(final @Nullable Object obj, final @Nullable Locale locale) {
      if (obj == null)
         return null;
      return StringUtils.lowerCase(obj.toString(), locale);
   }

   public static @Nullable String lowerCaseNullable(final @Nullable String str) {
      return StringUtils.lowerCase(str);
   }

   /**
    * See {@link StringUtils#lowerCase(String, Locale)}
    */
   public static @Nullable String lowerCaseNullable(final @Nullable String str, final @Nullable Locale locale) {
      return StringUtils.lowerCase(str, locale);
   }

   /**
    * See {@link StringUtils#mid(String, int, int)}
    */
   public static String mid(final String str, final int pos, final int len) {
      return asNonNullUnsafe(StringUtils.mid(str, pos, len));
   }

   /**
    * See {@link StringUtils#mid(String, int, int)}
    */
   public static @Nullable String midNullable(final @Nullable String str, final int pos, final int len) {
      return StringUtils.mid(str, pos, len);
   }

   /**
    * See {@link StringUtils#normalizeSpace(String)}
    */
   public static String normalizeSpace(final String str) {
      return asNonNullUnsafe(StringUtils.normalizeSpace(str));
   }

   /**
    * See {@link StringUtils#normalizeSpace(String)}
    */
   public static @Nullable String normalizeSpaceNullable(final @Nullable String str) {
      return StringUtils.normalizeSpace(str);
   }

   @Nullable
   public static <T extends CharSequence> T nullIfBlank(final @Nullable T txt) {
      return isBlank(txt) ? null : txt;
   }

   @Nullable
   public static <T extends CharSequence> T nullIfEmpty(final @Nullable T txt) {
      return isEmpty(txt) ? null : txt;
   }

   /**
    * See {@link StringUtils#ordinalIndexOf(CharSequence, CharSequence, int)}
    */
   public static int ordinalIndexOf(final @Nullable CharSequence searchIn, final @Nullable CharSequence searchFor, final int ordinal) {
      return StringUtils.ordinalIndexOf(searchIn, searchFor, ordinal);
   }

   /**
    * See {@link StringUtils#overlay(String, String, int, int)}
    */
   public static String overlay(final String str, final @Nullable String overlay, final int start, final int end) {
      return asNonNullUnsafe(StringUtils.overlay(str, overlay, start, end));
   }

   /**
    * See {@link StringUtils#overlay(String, String, int, int)}
    */
   public static @Nullable String overlayNullable(final @Nullable String str, final @Nullable String overlay, final int start,
         final int end) {
      return StringUtils.overlay(str, overlay, start, end);
   }

   public static String pluralize(final int count, final String singluar, final String plural) {
      return count == 1 ? singluar : plural;
   }

   /**
    * See {@link StringUtils#prependIfMissing(String, CharSequence, CharSequence[])}
    */
   public static String prependIfMissing(final String str, final @Nullable CharSequence prefix, final CharSequence @Nullable... prefixes) {
      return asNonNullUnsafe(StringUtils.prependIfMissing(str, prefix, prefixes));
   }

   /**
    * See {@link StringUtils#prependIfMissingIgnoreCase(String, CharSequence, CharSequence[])}
    */
   public static String prependIfMissingIgnoreCase(final String str, final @Nullable CharSequence prefix,
         final CharSequence @Nullable... prefixes) {
      return asNonNullUnsafe(StringUtils.prependIfMissingIgnoreCase(str, prefix, prefixes));
   }

   /**
    * See {@link StringUtils#prependIfMissingIgnoreCase(String, CharSequence, CharSequence[])}
    */
   public static @Nullable String prependIfMissingIgnoreCaseNullable(final @Nullable String str, final @Nullable CharSequence prefix,
         final CharSequence @Nullable... prefixes) {
      return StringUtils.prependIfMissingIgnoreCase(str, prefix, prefixes);
   }

   /**
    * See {@link StringUtils#prependIfMissing(String, CharSequence, CharSequence[])}
    */
   public static @Nullable String prependIfMissingNullable(final @Nullable String str, final @Nullable CharSequence prefix,
         final CharSequence @Nullable... prefixes) {
      return StringUtils.prependIfMissing(str, prefix, prefixes);
   }

   public static CharSequence prependLines(final CharSequence multiLineText, final CharSequence prefix) {
      if (prefix.length() == 0)
         return multiLineText;
      if (multiLineText.length() == 0)
         return prefix.toString();

      final var sb = new StringBuilder(multiLineText.length() + prefix.length());
      sb.append(prefix);
      final var prevCh = new char[1];
      multiLineText.chars().forEach(value -> {
         final char ch = (char) value;
         if (prevCh[0] == '\r' && ch != '\n') {
            sb.append(prefix);
         }
         sb.append(ch);
         if (ch == '\n') {
            sb.append(prefix);
         }
         prevCh[0] = ch;
      });
      if (prevCh[0] == '\r') {
         sb.append(prefix);
      }
      return sb;
   }

   /**
    * @See {@link StringUtils#wrap(String, char)}
    */
   public static String quote(final String str, final char quoteWith) {
      return asNonNullUnsafe(StringUtils.wrap(str, quoteWith));
   }

   /**
    * @See {@link StringUtils#wrap(String, String)}
    */
   public static String quote(final String str, final @Nullable String quoteWith) {
      return asNonNullUnsafe(StringUtils.wrap(str, quoteWith));
   }

   /**
    * @See {@link StringUtils#wrapIfMissing(String, char)}
    */
   public static String quoteIfMissing(final String str, final char quoteWith) {
      return asNonNullUnsafe(StringUtils.wrapIfMissing(str, quoteWith));
   }

   /**
    * @See {@link StringUtils#wrapIfMissing(String, String)}
    */
   public static String quoteIfMissing(final String str, final @Nullable String quoteWith) {
      return asNonNullUnsafe(StringUtils.wrapIfMissing(str, quoteWith));
   }

   /**
    * @See {@link StringUtils#wrapIfMissing(String, char)}
    */
   public static @Nullable String quoteIfMissingNullable(final @Nullable String str, final char quoteWith) {
      return StringUtils.wrapIfMissing(str, quoteWith);
   }

   /**
    * @See {@link StringUtils#wrapIfMissing(String, String)}
    */
   public static @Nullable String quoteIfMissingNullable(final @Nullable String str, final @Nullable String quoteWith) {
      return StringUtils.wrapIfMissing(str, quoteWith);
   }

   /**
    * @See {@link StringUtils#wrap(String, char)}
    */
   public static @Nullable String quoteNullable(final @Nullable String str, final char quoteWith) {
      return StringUtils.wrap(str, quoteWith);
   }

   /**
    * @See {@link StringUtils#wrap(String, String)}
    */
   public static @Nullable String quoteNullable(final @Nullable String str, final @Nullable String quoteWith) {
      return StringUtils.wrap(str, quoteWith);
   }

   /**
    * See {@link String#regionMatches(boolean, int, String, int, int)}
    */
   public static boolean regionMatches(final @Nullable CharSequence searchIn, final boolean ignoreCase, final int searchInOffset,
         @Nullable final CharSequence searchFor, final int searchForOffset, int length) {
      if (searchIn == null || searchFor == null)
         return false;

      if (searchIn instanceof String && searchFor instanceof String)
         return ((String) searchIn).regionMatches(ignoreCase, searchInOffset, (String) searchFor, searchForOffset, length);

      if (searchInOffset < 0 || searchForOffset < 0 //
            || searchInOffset > searchIn.length() - length //
            || searchForOffset > searchFor.length() - length)
         return false;

      int searchInIndex = searchInOffset;
      int searchForIndex = searchForOffset;

      while (length-- > 0) {
         final char c1 = searchIn.charAt(searchInIndex++);
         final char c2 = searchFor.charAt(searchForIndex++);
         if (c1 == c2) {
            continue;
         }
         if (ignoreCase) {
            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 == u2 || Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
               continue;
            }
         }
         return false;
      }
      return true;
   }

   /**
    * See {@link StringUtils#remove(String, char)}
    */
   public static String remove(final String str, final char remove) {
      return asNonNullUnsafe(StringUtils.remove(str, remove));
   }

   /**
    * See {@link StringUtils#remove(String, String)}
    */
   public static String remove(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.remove(str, remove));
   }

   public static String remove(final String str, final @Nullable List<String> remove) {
      return asNonNullUnsafe(removeNullable(str, remove));
   }

   public static String remove(final String str, final String @Nullable... remove) {
      return asNonNullUnsafe(removeNullable(str, remove));
   }

   /**
    * See {@link StringUtils#removeEnd(String, String)}
    */
   public static String removeEnd(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.removeEnd(str, remove));
   }

   /**
    * See {@link StringUtils#removeEndIgnoreCase(String, String)}
    */
   public static String removeEndIgnoreCase(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.removeEndIgnoreCase(str, remove));
   }

   /**
    * See {@link StringUtils#removeEndIgnoreCase(String, String)}
    */
   public static @Nullable String removeEndIgnoreCaseNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.removeEndIgnoreCase(str, remove);
   }

   /**
    * See {@link StringUtils#removeEnd(String, String)}
    */
   public static @Nullable String removeEndNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.removeEnd(str, remove);
   }

   /**
    * See {@link StringUtils#removeIgnoreCase(String, String)}
    */
   public static String removeIgnoreCase(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.removeIgnoreCase(str, remove));
   }

   /**
    * See {@link StringUtils#removeIgnoreCase(String, String)}
    */
   public static @Nullable String removeIgnoreCaseNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.removeIgnoreCase(str, remove);
   }

   /**
    * See {@link StringUtils#remove(String, char)}
    */
   public static @Nullable String removeNullable(final @Nullable String str, final char remove) {
      return StringUtils.remove(str, remove);
   }

   /**
    * See {@link StringUtils#remove(String, String)}
    */
   public static @Nullable String removeNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.remove(str, remove);
   }

   public static @Nullable String removeNullable(final @Nullable String str, final @Nullable List<String> remove) {
      if (str == null || remove == null || remove.isEmpty() || str.isEmpty())
         return str;

      var result = str;
      for (final var r : remove) {
         result = StringUtils.remove(str, r);
      }
      return result;
   }

   public static @Nullable String removeNullable(final @Nullable String str, final String @Nullable... remove) {
      if (str == null || remove == null || remove.length == 0 || str.isEmpty())
         return str;

      var result = str;
      for (final var r : remove) {
         result = StringUtils.remove(str, r);
      }
      return result;
   }

   /**
    * See {@link StringUtils#removeStart(String, String)}
    */
   public static String removeStart(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.removeStart(str, remove));
   }

   /**
    * See {@link StringUtils#removeStartIgnoreCase(String, String)}
    */
   public static String removeStartIgnoreCase(final String str, final @Nullable String remove) {
      return asNonNullUnsafe(StringUtils.removeStartIgnoreCase(str, remove));
   }

   /**
    * See {@link StringUtils#removeStartIgnoreCase(String, String)}
    */
   public static @Nullable String removeStartIgnoreCaseNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.removeStartIgnoreCase(str, remove);
   }

   /**
    * See {@link StringUtils#removeStart(String, String)}
    */
   public static @Nullable String removeStartNullable(final @Nullable String str, final @Nullable String remove) {
      return StringUtils.removeStart(str, remove);
   }

   /**
    * See {@link StringUtils#repeat(char, int)}
    */
   public static String repeat(final char ch, final int repeat) {
      return StringUtils.repeat(ch, repeat);
   }

   /**
    * <p>
    * Duplicates text <code>repeat</code> times to form a new {@link CharSequence}.
    * </p>
    */
   public static CharSequence repeat(final CharSequence text, final int repeat) {
      return asNonNullUnsafe(repeatNullable(text, repeat));
   }

   /**
    * See {@link StringUtils#repeat(String, int)}
    */
   public static String repeat(final String str, final int repeat) {
      return asNonNullUnsafe(StringUtils.repeat(str, repeat));
   }

   /**
    * See {@link StringUtils#repeat(String, String, int)}
    */
   public static String repeat(final String str, final @Nullable String separator, final int repeat) {
      return asNonNullUnsafe(StringUtils.repeat(str, separator, repeat));
   }

   /**
    * <p>
    * Duplicates text <code>repeat</code> times to form a new {@link CharSequence}.
    * </p>
    */
   public static @Nullable CharSequence repeatNullable(final @Nullable CharSequence text, final int repeat) {
      if (text == null || text.length() == 0)
         return text;

      if (repeat < 1)
         return EMPTY;

      final var sb = new StringBuilder(text.length() * repeat);
      for (int i = 0; i < repeat; i++) {
         sb.append(text);
      }
      return sb;
   }

   /**
    * See {@link StringUtils#repeat(String, int)}
    */
   public static @Nullable String repeatNullable(final @Nullable String str, final int repeat) {
      return StringUtils.repeat(str, repeat);
   }

   /**
    * See {@link StringUtils#repeat(String, String, int)}
    */
   public static @Nullable String repeatNullable(final @Nullable String str, final @Nullable String separator, final int repeat) {
      return StringUtils.repeat(str, separator, repeat);
   }

   /**
    * See {@link StringUtils#replaceChars(String, char, char)}
    */
   public static String replace(final String searchIn, final char searchFor, final char replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceChars(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replace(String, String, String)}
    */
   public static String replace(final String searchIn, final @Nullable String searchFor, final @Nullable String replaceWith) {
      return asNonNullUnsafe(StringUtils.replace(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replace(String, String, String, int)}
    */
   public static String replace(final String searchIn, final @Nullable String searchFor, final @Nullable String replaceWith,
         final int max) {
      return asNonNullUnsafe(StringUtils.replace(searchIn, searchFor, replaceWith, max));
   }

   /**
    * Replace all occurrences of searchFor in searchIn with replaceWith.
    */
   public static void replace(final @Nullable StringBuffer searchIn, final @Nullable String searchFor, final @Nullable String replaceWith) {
      if (searchIn == null || searchFor == null || replaceWith == null)
         return;
      final int searchForLen = searchFor.length();
      final int replaceWithLen = replaceWith.length();
      int index = searchIn.indexOf(searchFor);
      while (index != -1) {
         searchIn.replace(index, index + searchForLen, replaceWith);
         index = searchIn.indexOf(searchFor, index + replaceWithLen);
      }
   }

   /**
    * Replace all occurrences of searchFor in searchIn with replaceWith.
    */
   public static void replace(final @Nullable StringBuilder searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      if (searchIn == null || searchFor == null || replaceWith == null)
         return;
      final int searchForLen = searchFor.length();
      final int replaceWithLen = replaceWith.length();
      int index = searchIn.indexOf(searchFor);
      while (index != -1) {
         searchIn.replace(index, index + searchForLen, replaceWith);
         index = searchIn.indexOf(searchFor, index + replaceWithLen);
      }
   }

   /**
    * Replaces the substring starting at <code>startAt</code> with the string <code>replaceWith</code>.<br/>
    * <br/>
    * <b>startAt:</b><br/>
    * If startAt is positive, the replacing will begin at the startAt'th offset into searchIn.<br/>
    * If startAt is negative, the replacing will begin at the startAt'th character from the end of searchIn.<br/>
    * <br/>
    * Behavior is based on PHP's substr_replace function http://www.php.net/manual/en/function.substr-replace.php<br/>
    *
    * @param startAt position where to insert the text (0=before 1st character, 1=after 1st character, 2=after 2nd character)
    */
   public static CharSequence replaceAt(final CharSequence searchIn, final int startAt, final CharSequence replaceWith) {
      return asNonNullUnsafe(replaceAtNullable(searchIn, startAt, replaceWith));
   }

   /**
    * Replaces the substring starting at <code>startAt</code> having a length of <code>length</code> with the string <code>replaceWith</code>.<br/>
    * <br/>
    * <b>startAt:</b><br/>
    * If startAt is positive, the replacing will begin at the startAt'th offset into searchIn.<br/>
    * If startAt is negative, the replacing will begin at the startAt'th character from the end of searchIn.<br/>
    * <br/>
    * <b>length:</b><br/>
    * If length is positive, it represents the length of the portion of searchIn which is to be replaced.<br/>
    * If length is negative, it represents the number of characters from the end of searchIn at which to stop replacing.<br/>
    * If length is 0 the text will be inserted at the given position.<br/>
    * <br/>
    * Behavior is based on PHP's substr_replace function http://www.php.net/manual/en/function.substr-replace.php<br/>
    *
    * @param startAt position where to insert the text (0=before 1st character, 1=after 1st character, 2=after 2nd character)
    * @param length number of characters to replace
    */
   public static CharSequence replaceAt(final @Nullable CharSequence searchIn, final int startAt, final int length,
         final CharSequence replaceWith) {
      return asNonNullUnsafe(replaceAtNullable(searchIn, startAt, length, replaceWith));
   }

   /**
    * Replaces the substring starting at <code>startAt</code> with the string <code>replaceWith</code>.<br/>
    * <br/>
    * <b>startAt:</b><br/>
    * If startAt is positive, the replacing will begin at the startAt'th offset into searchIn.<br/>
    * If startAt is negative, the replacing will begin at the startAt'th character from the end of searchIn.<br/>
    * <br/>
    * Behavior is based on PHP's substr_replace function http://www.php.net/manual/en/function.substr-replace.php<br/>
    *
    * @param startAt position where to insert the text (0=before 1st character, 1=after 1st character, 2=after 2nd character)
    */
   public static @Nullable CharSequence replaceAtNullable(final @Nullable CharSequence searchIn, final int startAt,
         final @Nullable CharSequence replaceWith) {
      if (searchIn == null)
         return searchIn;
      return replaceAtNullable(searchIn, startAt, searchIn.length(), replaceWith);
   }

   /**
    * Replaces the substring starting at <code>startAt</code> having a length of <code>length</code> with the string <code>replaceWith</code>.<br/>
    * <br/>
    * <b>startAt:</b><br/>
    * If startAt is positive, the replacing will begin at the startAt'th offset into searchIn.<br/>
    * If startAt is negative, the replacing will begin at the startAt'th character from the end of searchIn.<br/>
    * <br/>
    * <b>length:</b><br/>
    * If length is positive, it represents the length of the portion of searchIn which is to be replaced.<br/>
    * If length is negative, it represents the number of characters from the end of searchIn at which to stop replacing.<br/>
    * If length is 0 the text will be inserted at the given position.<br/>
    * <br/>
    * Behavior is based on PHP's substr_replace function http://www.php.net/manual/en/function.substr-replace.php<br/>
    *
    * @param startAt position where to insert the text (0=before 1st character, 1=after 1st character, 2=after 2nd character)
    * @param length number of characters to replace
    */
   public static @Nullable CharSequence replaceAtNullable(final @Nullable CharSequence searchIn, int startAt, int length,
         final @Nullable CharSequence replaceWith) {
      if (searchIn == null || replaceWith == null)
         return searchIn;

      final int stringLength = searchIn.length();

      if (startAt < 0) {
         startAt = stringLength + startAt;
         if (startAt < 0) {
            startAt = 0;
         }
      } else if (startAt > stringLength) {
         startAt = stringLength;
      }

      if (length < 0) {
         length = stringLength + length;
         if (length < startAt) {
            length = 0;
         } else {
            length -= startAt;
         }
      }

      final int end = startAt + length > stringLength ? stringLength : startAt + length;
      final var sb = new StringBuilder();
      sb.append(searchIn, 0, startAt);
      sb.append(replaceWith);
      sb.append(searchIn, end, stringLength);
      return sb;
   }

   /**
    * See {@link StringUtils#replaceChars(String, String, String)}
    */
   public static String replaceChars(final String searchIn, final @Nullable String searchChars, final @Nullable String replaceChars) {
      return asNonNullUnsafe(StringUtils.replaceChars(searchIn, searchChars, replaceChars));
   }

   /**
    * See {@link StringUtils#replaceChars(String, String, String)}
    */
   public static @Nullable String replaceCharsNullable(final @Nullable String searchIn, final @Nullable String searchChars,
         final @Nullable String replaceChars) {
      return StringUtils.replaceChars(searchIn, searchChars, replaceChars);
   }

   /**
    * @param tokens e.g. {"searchFor1", "replaceWith1", searchFor2", "replaceWith2", ...}
    */
   public static String replaceEach(final String searchIn, final String... tokens) {
      return asNonNullUnsafe(replaceEachNullable(searchIn, tokens));
   }

   /**
    * See {@link StringUtils#replaceEach(String, String[], String[])}
    */
   public static String replaceEach(final String searchIn, final String @Nullable [] searchFor, final String @Nullable [] replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceEach(searchIn, searchFor, replaceWith));
   }

   public static CharSequence replaceEachGroup(final @Nullable Pattern regex, final CharSequence searchIn, final int groupToReplace,
         final @Nullable String replaceWith) {
      return asNonNullUnsafe(replaceEachGroupNullable(regex, searchIn, groupToReplace, replaceWith));
   }

   public static CharSequence replaceEachGroup(final @Nullable String regex, final CharSequence searchIn, final int groupToReplace,
         final @Nullable String replaceWith) {
      return asNonNullUnsafe(replaceEachGroupNullable(regex, searchIn, groupToReplace, replaceWith));
   }

   public static @Nullable CharSequence replaceEachGroupNullable(final @Nullable Pattern regex, final @Nullable CharSequence searchIn,
         final int groupToReplace, final @Nullable String replaceWith) {
      if (regex == null || searchIn == null || replaceWith == null)
         return searchIn;
      final var m = regex.matcher(searchIn);
      final var sb = new StringBuilder(searchIn);
      while (m.find()) {
         sb.replace(m.start(groupToReplace), m.end(groupToReplace), replaceWith);
      }
      return sb;
   }

   public static @Nullable CharSequence replaceEachGroupNullable(final @Nullable String regex, final @Nullable CharSequence searchIn,
         final int groupToReplace, final @Nullable String replaceWith) {
      if (regex == null)
         return searchIn;
      return replaceEachGroupNullable(Pattern.compile(regex), searchIn, groupToReplace, replaceWith);
   }

   /**
    * @param tokens e.g. {"searchFor1", "replaceWith1", searchFor2", "replaceWith2", ...}
    */
   public static @Nullable String replaceEachNullable(final @Nullable String searchIn, final String @Nullable... tokens) {
      if (searchIn == null || tokens == null)
         return searchIn;
      final var searchFor = new String[tokens.length / 2];
      final var replaceWith = new String[tokens.length / 2];

      boolean isNextTokenSearchKey = true;
      int idx = 0;
      for (final String token : tokens)
         if (isNextTokenSearchKey) {
            searchFor[idx] = token;
            isNextTokenSearchKey = false;
         } else {
            replaceWith[idx] = token;
            idx++;
            isNextTokenSearchKey = true;
         }
      return replaceEach(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replaceEach(String, String[], String[])}
    */
   public static @Nullable String replaceEachNullable(final @Nullable String searchIn, final String @Nullable [] searchFor,
         final String @Nullable [] replaceWith) {
      return StringUtils.replaceEach(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replaceEachRepeatedly(String, String[], String[])}
    */
   public static String replaceEachRepeatedly(final String searchIn, final String @Nullable [] searchFor,
         final String @Nullable [] replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceEachRepeatedly(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replaceEachRepeatedly(String, String[], String[])}
    */
   public static @Nullable String replaceEachRepeatedlyNullable(final @Nullable String searchIn, final String @Nullable [] searchFor,
         final String @Nullable [] replaceWith) {
      return StringUtils.replaceEachRepeatedly(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replaceOnce(String, String, String)}
    */
   public static String replaceFirst(final String searchIn, final @Nullable String searchFor, final @Nullable String replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceOnce(searchIn, searchFor, replaceWith));
   }

   /**
    * Replace the first occurrence of searchFor in searchIn with replaceWith.
    */
   public static void replaceFirst(final @Nullable StringBuffer searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      if (searchIn == null || searchFor == null || replaceWith == null)
         return;
      final int index = searchIn.indexOf(searchFor);
      if (index != -1) {
         searchIn.replace(index, index + searchFor.length(), replaceWith);
      }
   }

   /**
    * Replace the first occurrence of searchFor in searchIn with replaceWith.
    */
   public static void replaceFirst(final @Nullable StringBuilder searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      if (searchIn == null || searchFor == null || replaceWith == null)
         return;
      final int index = searchIn.indexOf(searchFor);
      if (index != -1) {
         searchIn.replace(index, index + searchFor.length(), replaceWith);
      }
   }

   /**
    * See {@link StringUtils#replaceOnceIgnoreCase(String, String, String)}
    */
   public static String replaceFirstIgnoreCase(final String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceOnceIgnoreCase(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replaceOnceIgnoreCase(String, String, String)}
    */
   public static @Nullable String replaceFirstIgnoreCaseNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      return StringUtils.replaceOnceIgnoreCase(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replaceOnce(String, String, String)}
    */
   public static @Nullable String replaceFirstNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      return StringUtils.replaceOnce(searchIn, searchFor, replaceWith);
   }

   /**
    * Replace all occurrences of the search string with the replacement string.
    * This method is case insensitive.
    *
    * @param searchIn The string to search
    * @param searchFor The string to find
    * @param replaceWith The string to replace searchFor with.
    * @return Returns searchIn with all occurrences of searchFor replaced with replaceWith. If any parameter is null, searchIn will be returned.
    */
   public static CharSequence replaceIgnoreCase(final String searchIn, @Nullable final String searchFor,
         @Nullable final CharSequence replaceWith) {
      return asNonNullUnsafe(replaceIgnoreCaseNullable(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replaceIgnoreCase(String, String, String)}
    */
   public static String replaceIgnoreCase(final String searchIn, final @Nullable String searchFor, final @Nullable String replaceWith) {
      return asNonNullUnsafe(StringUtils.replaceIgnoreCase(searchIn, searchFor, replaceWith));
   }

   /**
    * See {@link StringUtils#replaceIgnoreCase(String, String, String, int)}
    */
   public static String replaceIgnoreCase(final String searchIn, final @Nullable String searchFor, final @Nullable String replaceWith,
         final int max) {
      return asNonNullUnsafe(StringUtils.replaceIgnoreCase(searchIn, searchFor, replaceWith, max));
   }

   /**
    * Replace all occurrences of the search string with the replacement string.
    * This method is case insensitive.
    *
    * @param searchIn The string to search
    * @param searchFor The string to find
    * @param replaceWith The string to replace searchFor with.
    * @return Returns searchIn with all occurrences of searchFor replaced with replaceWith. If any parameter is null, searchIn will be returned.
    */
   public static @Nullable CharSequence replaceIgnoreCaseNullable(final @Nullable String searchIn, @Nullable String searchFor,
         @Nullable final CharSequence replaceWith) {
      if (searchIn == null || searchFor == null || replaceWith == null)
         return searchIn;
      final int searchInLen = searchIn.length();
      if (searchInLen == 0)
         return searchIn;

      final int searchForLen = searchFor.length();
      final var out = new StringBuilder();

      int startSearchAt = 0;
      int foundAt = 0;

      searchFor = searchFor.toLowerCase();
      final String searchInLowerCase = searchIn.toLowerCase();

      while ((foundAt = searchInLowerCase.indexOf(searchFor, startSearchAt)) >= 0) {
         out.append(searchIn, startSearchAt, foundAt);
         out.append(replaceWith);
         startSearchAt = foundAt + searchForLen;
      }
      out.append(searchIn, startSearchAt, searchInLen);
      return out;
   }

   /**
    * See {@link StringUtils#replaceIgnoreCase(String, String, String)}
    */
   public static @Nullable String replaceIgnoreCaseNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      return StringUtils.replaceIgnoreCase(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replaceIgnoreCase(String, String, String, int)}
    */
   public static @Nullable String replaceIgnoreCaseNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith, final int max) {
      return StringUtils.replaceIgnoreCase(searchIn, searchFor, replaceWith, max);
   }

   /**
    * See {@link StringUtils#replaceChars(String, char, char)}
    */
   public static @Nullable String replaceNullable(final @Nullable String searchIn, final char searchFor, final char replaceWith) {
      return StringUtils.replaceChars(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replace(String, String, String)}
    */
   public static @Nullable String replaceNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith) {
      return StringUtils.replace(searchIn, searchFor, replaceWith);
   }

   /**
    * See {@link StringUtils#replace(String, String, String, int)}
    */
   public static @Nullable String replaceNullable(final @Nullable String searchIn, final @Nullable String searchFor,
         final @Nullable String replaceWith, final int max) {
      return StringUtils.replace(searchIn, searchFor, replaceWith, max);
   }

   /**
    * See {@link StringUtils#reverse(String)}
    */
   public static String reverse(final String str) {
      return asNonNullUnsafe(StringUtils.reverse(str));
   }

   /**
    * See {@link StringUtils#reverseDelimited(String, char)}
    */
   public static String reverseDelimited(final String str, final char separatorChar) {
      return asNonNullUnsafe(StringUtils.reverseDelimited(str, separatorChar));
   }

   /**
    * See {@link StringUtils#reverseDelimited(String, char)}
    */
   public static @Nullable String reverseDelimitedNullable(final @Nullable String str, final char separatorChar) {
      return StringUtils.reverseDelimited(str, separatorChar);
   }

   /**
    * See {@link StringUtils#reverse(String)}
    */
   public static @Nullable String reverseNullable(final @Nullable String str) {
      return StringUtils.reverse(str);
   }

   /**
    * See {@link StringUtils#right(String, int)}
    */
   public static String right(final String str, final int len) {
      return asNonNullUnsafe(StringUtils.right(str, len));
   }

   /**
    * See {@link StringUtils#right(String, int)}
    */
   public static @Nullable String rightNullable(final @Nullable String str, final int len) {
      return StringUtils.right(str, len);
   }

   /**
    * See {@link StringUtils#rightPad(String, int)}
    */
   public static String rightPad(final String str, final int size) {
      return asNonNullUnsafe(StringUtils.rightPad(str, size));
   }

   /**
    * See {@link StringUtils#rightPad(String, int, char)}
    */
   public static String rightPad(final String str, final int size, final char padChar) {
      return asNonNullUnsafe(StringUtils.rightPad(str, size, padChar));
   }

   /**
    * See {@link StringUtils#rightPad(String, int, String)}
    */
   public static String rightPad(final String str, final int size, final @Nullable String padStr) {
      return asNonNullUnsafe(StringUtils.rightPad(str, size, padStr));
   }

   /**
    * See {@link StringUtils#rightPad(String, int)}
    */
   public static @Nullable String rightPadNullable(final @Nullable String str, final int size) {
      return StringUtils.rightPad(str, size);
   }

   /**
    * See {@link StringUtils#rightPad(String, int, char)}
    */
   public static @Nullable String rightPadNullable(final @Nullable String str, final int size, final char padChar) {
      return StringUtils.rightPad(str, size, padChar);
   }

   /**
    * See {@link StringUtils#rightPad(String, int, String)}
    */
   public static @Nullable String rightPadNullable(final @Nullable String str, final int size, final @Nullable String padStr) {
      return StringUtils.rightPad(str, size, padStr);
   }

   /**
    * See {@link StringUtils#rotate(String, int)}
    */
   public static String rotate(final String str, final int shift) {
      return asNonNullUnsafe(StringUtils.rotate(str, shift));
   }

   /**
    * See {@link StringUtils#rotate(String, int)}
    */
   public static @Nullable String rotateNullable(final @Nullable String str, final int shift) {
      return StringUtils.rotate(str, shift);
   }

   /**
    * See {@link StringUtils#split(String)}
    */
   public static @NonNull String[] split(final String str) {
      return asNonNullUnsafe(StringUtils.split(str));
   }

   /**
    * See {@link StringUtils#split(String, char)}
    */
   public static @NonNull String[] split(final String str, final char separatorChar) {
      return asNonNullUnsafe(StringUtils.split(str, separatorChar));
   }

   /**
    * See {@link StringUtils#split(String, String)}
    */
   public static @NonNull String[] split(final String str, final @Nullable String separatorChars) {
      return asNonNullUnsafe(StringUtils.split(str, separatorChars));
   }

   /**
    * See {@link StringUtils#split(String, String, int)}
    */
   public static @NonNull String[] split(final String str, final @Nullable String separatorChars, final int max) {
      return asNonNullUnsafe(StringUtils.split(str, separatorChars, max));
   }

   /**
    * Empty tokens are not preserved.
    */
   public static Iterable<String> splitAsIterable(final String text, final char separator) {
      return () -> new Iterator<>() {
         private int searchAt = 0;
         private String nextPart = "";

         @Override
         public boolean hasNext() {
            while (searchAt < text.length()) {
               final int foundAt = text.indexOf(separator, searchAt);
               if (foundAt == -1) {
                  nextPart = text.substring(searchAt);
                  searchAt = text.length();
               } else {
                  nextPart = text.substring(searchAt, foundAt);
                  searchAt = foundAt + 1;
               }

               if (!nextPart.isEmpty())
                  return true;

            }
            return false;
         }

         @Override
         public String next() {
            if (nextPart.isEmpty() && !hasNext())
               throw new NoSuchElementException();
            final String result = nextPart;
            nextPart = "";
            return result;
         }
      };
   }

   /**
    * Empty tokens are not preserved.
    */
   public static @Nullable Iterable<String> splitAsIterableNullable(final @Nullable String text, final char separator) {
      return text == null ? null : splitAsIterable(text, separator);
   }

   /**
    * Empty tokens are not preserved.
    */
   public static List<String> splitAsList(final String text, final char separator) {
      final int len = text.length();
      if (text.length() == 0)
         return Collections.emptyList();

      if (len == 1) {
         if (text.charAt(0) == separator)
            return Collections.emptyList();
         return List.of(text);
      }

      final var tokens = new ArrayList<String>(len < 5 ? 2 : 4);
      int searchAt = 0;
      int foundAt;
      while ((foundAt = text.indexOf(separator, searchAt)) != Strings.INDEX_NOT_FOUND) {
         if (searchAt < foundAt) {
            tokens.add(text.substring(searchAt, foundAt));
         }
         searchAt = foundAt + 1;
      }

      if (searchAt == 0) { // separator not found
         tokens.add(text);
         return tokens;
      }

      if (searchAt < len) {
         tokens.add(text.substring(searchAt, len));
      }
      return tokens;
   }

   /**
    * Empty tokens are not preserved.
    */
   public static @Nullable List<String> splitAsListNullable(final @Nullable String text, final char separator) {
      return text == null ? null : splitAsList(text, separator);
   }

   /**
    * Empty tokens are not preserved.
    */
   public static Set<String> splitAsSet(final String text, final char separator) {
      final int len = text.length();
      if (text.length() == 0)
         return Collections.emptySet();

      if (len == 1) {
         if (text.charAt(0) == separator)
            return Collections.emptySet();
         return Set.of(text);
      }

      final var tokens = new HashSet<String>(len < 5 ? 2 : 4);
      int searchAt = 0;
      int foundAt;
      while ((foundAt = text.indexOf(separator, searchAt)) != Strings.INDEX_NOT_FOUND) {
         if (searchAt < foundAt) {
            tokens.add(text.substring(searchAt, foundAt));
         }
         searchAt = foundAt + 1;
      }

      if (searchAt == 0) { // separator not found
         tokens.add(text);
         return tokens;
      }

      if (searchAt < len) {
         tokens.add(text.substring(searchAt, len));
      }
      return tokens;
   }

   /**
    * Empty tokens are not preserved.
    */
   public static @Nullable Set<String> splitAsSetNullable(final @Nullable String text, final char separator) {
      return text == null ? null : splitAsSet(text, separator);
   }

   /**
    * Empty tokens are not preserved.
    */
   public static Stream<String> splitAsStream(final String text, final char separator) {
      return StreamSupport.stream(new Spliterators.AbstractSpliterator<String>(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL) {
         private int searchAt = 0;

         @Override
         public boolean tryAdvance(final Consumer<? super String> action) {
            while (searchAt < text.length()) {
               final int foundAt = text.indexOf(separator, searchAt);
               final String part;
               if (foundAt == -1) {
                  part = text.substring(searchAt);
                  searchAt = text.length();
               } else {
                  part = text.substring(searchAt, foundAt);
                  searchAt = foundAt + 1;
               }

               if (!part.isEmpty()) {
                  action.accept(part);
                  return true;
               }
            }
            return false;
         }
      }, false);
   }

   /**
    * Empty tokens are not preserved.
    */
   public static @Nullable Stream<String> splitAsStreamNullable(final @Nullable String text, final char separator) {
      return text == null ? null : splitAsStream(text, separator);
   }

   /**
    * See {@link StringUtils#splitByCharacterType(String)}
    */
   public static @NonNull String[] splitByCharacterType(final String str) {
      return asNonNullUnsafe(StringUtils.splitByCharacterType(str));
   }

   /**
    * See {@link StringUtils#splitByCharacterTypeCamelCase(String)}
    */
   public static @NonNull String[] splitByCharacterTypeCamelCase(final String str) {
      return asNonNullUnsafe(StringUtils.splitByCharacterTypeCamelCase(str));
   }

   /**
    * See {@link StringUtils#splitByCharacterTypeCamelCase(String)}
    */
   public static @NonNull String @Nullable [] splitByCharacterTypeCamelCaseNullable(final @Nullable String str) {
      return StringUtils.splitByCharacterTypeCamelCase(str);
   }

   /**
    * See {@link StringUtils#splitByCharacterType(String)}
    */
   public static @NonNull String @Nullable [] splitByCharacterTypeNullable(final @Nullable String str) {
      return StringUtils.splitByCharacterType(str);
   }

   /**
    * See {@link StringUtils#splitByWholeSeparator(String, String)}
    */
   public static @NonNull String[] splitByWholeSeparator(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.splitByWholeSeparator(str, separator));
   }

   /**
    * See {@link StringUtils#splitByWholeSeparator(String, String, int)}
    */
   public static @NonNull String[] splitByWholeSeparator(final String str, final @Nullable String separator, final int max) {
      return asNonNullUnsafe(StringUtils.splitByWholeSeparator(str, separator, max));
   }

   /**
    * See {@link StringUtils#splitByWholeSeparator(String, String)}
    */
   public static @NonNull String @Nullable [] splitByWholeSeparatorNullable(final @Nullable String str, final @Nullable String separator) {
      return StringUtils.splitByWholeSeparator(str, separator);
   }

   /**
    * See {@link StringUtils#splitByWholeSeparator(String, String, int)}
    */
   public static @NonNull String @Nullable [] splitByWholeSeparatorNullable(final @Nullable String str, final @Nullable String separator,
         final int max) {
      return StringUtils.splitByWholeSeparator(str, separator, max);
   }

   /**
    * See {@link StringUtils#splitByWholeSeparatorPreserveAllTokens(String, String)}
    */
   public static @NonNull String[] splitByWholeSeparatorPreserveAllTokens(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator));
   }

   /**
    * See {@link StringUtils#splitByWholeSeparatorPreserveAllTokens(String, String)}
    */
   public static @NonNull String[] splitByWholeSeparatorPreserveAllTokens(final String str, final @Nullable String separator,
         final int max) {
      return asNonNullUnsafe(StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max));
   }

   /**
    * See {@link StringUtils#splitByWholeSeparatorPreserveAllTokens(String, String)}
    */
   public static @NonNull String @Nullable [] splitByWholeSeparatorPreserveAllTokensNullable(final @Nullable String str,
         final @Nullable String separator) {
      return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator);
   }

   /**
    * See {@link StringUtils#splitByWholeSeparatorPreserveAllTokens(String, String)}
    */
   public static @NonNull String @Nullable [] splitByWholeSeparatorPreserveAllTokensNullable(final @Nullable String str,
         final @Nullable String separator, final int max) {
      return StringUtils.splitByWholeSeparatorPreserveAllTokens(str, separator, max);
   }

   /**
    * Splits the command string using shell-like syntax.
    *
    * <li>Inspired by https://docs.python.org/3/library/shlex.html#shlex.split
    * <li>Based on https://gist.github.com/raymyers/8077031
    */
   public static List<String> splitLikeShell(final CharSequence command) {
      final List<String> args = new ArrayList<>();
      boolean isNextCharEscaped = false;
      char quoteChar = 0;
      boolean isQuoting = false;
      int lastClosingQuoteIdx = Integer.MIN_VALUE;
      final var arg = new StringBuilder();
      char prevChar = ' ';
      for (int i = 0; i < command.length(); i++) {
         final char ch = command.charAt(i);
         if (isNextCharEscaped) {
            arg.append(ch);
            isNextCharEscaped = false;
         } else if (ch == '\\' && !(isQuoting && quoteChar == '\'')) {
            isNextCharEscaped = true;
         } else if (isQuoting && ch == quoteChar) {
            isQuoting = false;
            lastClosingQuoteIdx = i;
         } else if (!isQuoting && ch == '#' && Character.isWhitespace(prevChar)) {
            // ignore trailing comment
            break;
         } else if (!isQuoting && (ch == '\'' || ch == '"')) {
            isQuoting = true;
            quoteChar = ch;
         } else if (!isQuoting && Character.isWhitespace(ch)) {
            if (lastClosingQuoteIdx == i - 1 || arg.length() > 0) {
               args.add(arg.toString());
               arg.setLength(0);
            }
         } else {
            arg.append(ch);
         }
         prevChar = ch;
      }
      if (arg.length() > 0 || lastClosingQuoteIdx == command.length() - 1) {
         args.add(arg.toString());
      }
      return args;
   }

   public static @NonNull String[] splitLines(final String text, final boolean preserveEmptyLines) {
      return asNonNullUnsafe(splitLinesNullable(text, preserveEmptyLines));
   }

   public static @NonNull String @Nullable [] splitLinesNullable(final @Nullable String text, final boolean preserveEmptyLines) {
      if (text == null)
         return null;

      if (preserveEmptyLines) {
         if (text.indexOf(Strings.NEW_LINE) > -1)
            return splitByWholeSeparatorPreserveAllTokens(text, Strings.NEW_LINE);
         if (text.indexOf(Strings.CR_LF) > -1)
            return splitByWholeSeparatorPreserveAllTokens(text, Strings.CR_LF);
         if (text.indexOf(Strings.LF) > -1)
            return splitPreserveAllTokens(text, Strings.LF);
         if (text.indexOf(Strings.CR) > -1)
            return splitPreserveAllTokens(text, Strings.CR);
      } else {
         if (text.indexOf(Strings.NEW_LINE) > -1)
            return split(text, Strings.NEW_LINE);
         if (text.indexOf(Strings.CR_LF) > -1)
            return splitByWholeSeparator(text, Strings.CR_LF);
         if (text.indexOf(Strings.LF) > -1)
            return split(text, Strings.LF);
         if (text.indexOf(Strings.CR) > -1)
            return split(text, Strings.CR);
      }

      final var result = new String[] {text};
      return asNonNullUnsafe(result);
   }

   /**
    * See {@link StringUtils#split(String)}
    */
   public static @NonNull String @Nullable [] splitNullable(final @Nullable String str) {
      return StringUtils.split(str);
   }

   /**
    * See {@link StringUtils#split(String, char)}
    */
   public static @NonNull String @Nullable [] splitNullable(final @Nullable String str, final char separatorChar) {
      return StringUtils.split(str, separatorChar);
   }

   /**
    * See {@link StringUtils#split(String, String)}
    */
   public static @NonNull String @Nullable [] splitNullable(final @Nullable String str, final @Nullable String separatorChars) {
      return StringUtils.split(str, separatorChars);
   }

   /**
    * See {@link StringUtils#split(String, String, int)}
    */
   public static @NonNull String @Nullable [] splitNullable(final @Nullable String str, final @Nullable String separatorChars,
         final int max) {
      return StringUtils.split(str, separatorChars, max);
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String)}
    */
   public static @NonNull String[] splitPreserveAllTokens(final String str) {
      return asNonNullUnsafe(StringUtils.splitPreserveAllTokens(str));
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, char)}
    */
   public static @NonNull String[] splitPreserveAllTokens(final String str, final char separatorChar) {
      return asNonNullUnsafe(StringUtils.splitPreserveAllTokens(str, separatorChar));
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, String)}
    */
   public static @NonNull String[] splitPreserveAllTokens(final String str, final @Nullable String separatorChars) {
      return asNonNullUnsafe(StringUtils.splitPreserveAllTokens(str, separatorChars));
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, String, int)}
    */
   public static @NonNull String[] splitPreserveAllTokens(final String str, final @Nullable String separatorChars, final int max) {
      return asNonNullUnsafe(StringUtils.splitPreserveAllTokens(str, separatorChars, max));
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String)}
    */
   public static @NonNull String @Nullable [] splitPreserveAllTokensNullable(final @Nullable String str) {
      return StringUtils.splitPreserveAllTokens(str);
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, char)}
    */
   public static @NonNull String @Nullable [] splitPreserveAllTokensNullable(final @Nullable String str, final char separatorChar) {
      return StringUtils.splitPreserveAllTokens(str, separatorChar);
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, String)}
    */
   public static @NonNull String @Nullable [] splitPreserveAllTokensNullable(final @Nullable String str,
         final @Nullable String separatorChars) {
      return StringUtils.splitPreserveAllTokens(str, separatorChars);
   }

   /**
    * See {@link StringUtils#splitPreserveAllTokens(String, String, int)}
    */
   public static @NonNull String @Nullable [] splitPreserveAllTokensNullable(final @Nullable String str,
         final @Nullable String separatorChars, final int max) {
      return StringUtils.splitPreserveAllTokens(str, separatorChars, max);
   }

   /**
    * Empty tokens are not preserved.
    *
    * @deprecated use {@link #splitAsList(String, char)}
    */
   @Deprecated
   public static List<String> splitToList(final String text, final char separator) {
      return splitAsList(text, separator);
   }

   /**
    * Empty tokens are not preserved.
    *
    * @deprecated use {@link #splitAsListNullable(String, char)}
    */
   @Deprecated
   public static @Nullable List<String> splitToListNullable(final @Nullable String text, final char separator) {
      return text == null ? null : splitAsList(text, separator);
   }

   public static boolean startsWith(final @Nullable CharSequence str, final char ch) {
      return str != null && str.length() > 0 && str.charAt(0) == ch;
   }

   /**
    * See {@link StringUtils#startsWith(CharSequence, CharSequence)}
    */
   public static boolean startsWith(final @Nullable CharSequence searchIn, final @Nullable CharSequence prefix) {
      return StringUtils.startsWith(searchIn, prefix);
   }

   /**
    * See {@link StringUtils#startsWithAny(CharSequence, CharSequence[])}
    */
   public static boolean startsWithAny(final @Nullable CharSequence searchIn, final CharSequence @Nullable... prefixes) {
      return StringUtils.startsWithAny(searchIn, prefixes);
   }

   /**
    * See {@link StringUtils#startsWithIgnoreCase(CharSequence, CharSequence)}
    */
   public static boolean startsWithIgnoreCase(final @Nullable CharSequence searchIn, final @Nullable CharSequence prefix) {
      return StringUtils.startsWithIgnoreCase(searchIn, prefix);
   }

   /**
    * See {@link StringUtils#strip(String)}
    */
   public static String strip(final String str) {
      return asNonNullUnsafe(StringUtils.strip(str));
   }

   /**
    * See {@link StringUtils#strip(String, String)}
    */
   public static String strip(final String str, final @Nullable String stripChars) {
      return asNonNullUnsafe(StringUtils.strip(str, stripChars));
   }

   /**
    * See {@link StringUtils#stripAccents(String)}
    */
   public static String stripAccents(final String str) {
      return asNonNullUnsafe(StringUtils.stripAccents(str));
   }

   /**
    * See {@link StringUtils#stripAccents(String)}
    */
   public static @Nullable String stripAccentsNullable(final @Nullable String str) {
      return StringUtils.stripAccents(str);
   }

   /**
    * See {@link StringUtils#stripAll(String[])}
    */
   public static String[] stripAll(final String @Nullable... strs) {
      return asNonNullUnsafe(StringUtils.stripAll(strs));
   }

   /**
    * See {@link StringUtils#stripAll(String[])}
    */
   public static String[] stripAll(final String @Nullable [] strs, final @Nullable String stripChars) {
      return asNonNullUnsafe(StringUtils.stripAll(strs, stripChars));
   }

   /**
    * See {@link StringUtils#stripAll(String[])}
    */
   public static String @Nullable [] stripAllNullable(final String @Nullable... strs) {
      return StringUtils.stripAll(strs);
   }

   /**
    * See {@link StringUtils#stripAll(String[])}
    */
   public static String @Nullable [] stripAllNullable(final String @Nullable [] strs, final @Nullable String stripChars) {
      return StringUtils.stripAll(strs, stripChars);
   }

   public static String stripAnsiEscapeSequences(final String text) {
      return asNonNullUnsafe(stripAnsiEscapeSequencesNullable(text));
   }

   public static @Nullable String stripAnsiEscapeSequencesNullable(final @Nullable String text) {
      if (text == null)
         return null;
      return text.replaceAll("\u001B\\[[;\\d]*m", EMPTY);
   }

   /**
    * See {@link StringUtils#stripEnd(String, String)}
    */
   public static String stripEnd(final String str, final @Nullable String stripChars) {
      return asNonNullUnsafe(StringUtils.stripEnd(str, stripChars));
   }

   /**
    * See {@link StringUtils#stripEnd(String, String)}
    */
   public static @Nullable String stripEndNullable(final @Nullable String str, final @Nullable String stripChars) {
      return StringUtils.stripEnd(str, stripChars);
   }

   /**
    * See {@link StringUtils#strip(String)}
    */
   public static @Nullable String stripNullable(final @Nullable String str) {
      return StringUtils.strip(str);
   }

   /**
    * See {@link StringUtils#strip(String, String)}
    */
   public static @Nullable String stripNullable(final @Nullable String str, final @Nullable String stripChars) {
      return StringUtils.strip(str, stripChars);
   }

   /**
    * See {@link StringUtils#stripStart(String, String)}
    */
   public static String stripStart(final String str, final @Nullable String stripChars) {
      return asNonNullUnsafe(StringUtils.stripStart(str, stripChars));
   }

   /**
    * See {@link StringUtils#stripStart(String, String)}
    */
   public static @Nullable String stripStartNullable(final @Nullable String str, final @Nullable String stripChars) {
      return StringUtils.stripStart(str, stripChars);
   }

   /**
    * See {@link StringUtils#stripToEmpty(String)}
    */
   public static String stripToEmpty(final String str) {
      return asNonNullUnsafe(StringUtils.stripToEmpty(str));
   }

   /**
    * See {@link StringUtils#stripToEmpty(String)}
    */
   public static String stripToEmptyNullable(final @Nullable String str) {
      return asNonNullUnsafe(StringUtils.stripToEmpty(str));
   }

   /**
    * See {@link StringUtils#stripToNull(String)}
    */
   public static String stripToNull(final String str) {
      return asNonNullUnsafe(StringUtils.stripToNull(str));
   }

   /**
    * See {@link StringUtils#stripToNull(String)}
    */
   public static @Nullable String stripToNullNullable(final @Nullable String str) {
      return StringUtils.stripToNull(str);
   }

   /**
    * See {@link StringUtils#substring(String, int)}
    */
   public static String substring(final String str, final int start) {
      return asNonNullUnsafe(StringUtils.substring(str, start));
   }

   /**
    * See {@link StringUtils#substring(String, int, int)}
    */
   public static String substring(final String str, final int start, final int end) {
      return asNonNullUnsafe(StringUtils.substring(str, start, end));
   }

   /**
    * Searches a string from left to right and returns the rightmost characters of the string.
    *
    * substringBefore("this is a test", "s") -> "thi"
    *
    * @param searchIn The string whose leftmost characters you want to find.
    * @param searchFor A substring of searchIn.
    *           right.
    * @return The rightmost characters in searchIn.
    *         The number of characters returned is determined by searchFor.
    *         Returns an empty string if searchFor is not part of searchIn.
    */
   public static String substringAfter(final String searchIn, final char searchFor) {
      return asNonNullUnsafe(substringAfterNullable(searchIn, searchFor));
   }

   /**
    * See {@link StringUtils#substringAfter(String, int)}
    */
   public static String substringAfter(final String str, final int separator) {
      return asNonNullUnsafe(StringUtils.substringAfter(str, separator));
   }

   /**
    * See {@link StringUtils#substringAfter(String, String)}
    */
   public static String substringAfter(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.substringAfter(str, separator));
   }

   /**
    * See {@link StringUtils#substringAfterLast(String, int)}
    */
   public static String substringAfterLast(final String str, final int separator) {
      return asNonNullUnsafe(StringUtils.substringAfterLast(str, separator));
   }

   /**
    * See {@link StringUtils#substringAfterLast(String, String)}
    */
   public static String substringAfterLast(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.substringAfterLast(str, separator));
   }

   /**
    * See {@link StringUtils#substringAfterLast(String, int)}
    */
   public static @Nullable String substringAfterLastNullable(final @Nullable String str, final int separator) {
      return StringUtils.substringAfterLast(str, separator);
   }

   /**
    * See {@link StringUtils#substringAfterLast(String, String)}
    */
   public static @Nullable String substringAfterLastNullable(final @Nullable String str, final @Nullable String separator) {
      return StringUtils.substringAfterLast(str, separator);
   }

   /**
    * Searches a string from left to right and returns the rightmost characters of the string.
    *
    * substringBefore("this is a test", "s") -> "thi"
    *
    * @param searchIn The string whose leftmost characters you want to find.
    * @param searchFor A substring of searchIn.
    *           right.
    * @return The rightmost characters in searchIn.
    *         The number of characters returned is determined by searchFor.
    *         Returns an empty string if searchFor is not part of searchIn.
    */
   public static @Nullable String substringAfterNullable(final @Nullable String searchIn, final char searchFor) {
      if (searchIn == null || searchIn.isEmpty())
         return searchIn;

      final int pos = searchIn.indexOf(searchFor);
      if (pos < 0)
         return EMPTY;

      return searchIn.substring(pos + 1);
   }

   /**
    * See {@link StringUtils#substringAfter(String, int)}
    */
   public static @Nullable String substringAfterNullable(final @Nullable String str, final int separator) {
      return StringUtils.substringAfter(str, separator);
   }

   /**
    * See {@link StringUtils#substringAfter(String, String)}
    */
   public static @Nullable String substringAfterNullable(final @Nullable String str, final @Nullable String separator) {
      return StringUtils.substringAfter(str, separator);
   }

   /**
    * Searches a string from left to right and returns the leftmost characters of the string.
    *
    * substringBefore("this is a test", "s") -> "thi"
    *
    * @param searchIn The string whose leftmost characters you want to find.
    * @param searchFor A substring of searchIn.
    * @return The leftmost characters in searchIn.
    *         The number of characters returned is determined by searchFor.
    *         Returns searchIn if searchFor is not part of searchIn.
    */
   public static String substringBefore(final String searchIn, final char searchFor) {
      return asNonNullUnsafe(substringBeforeNullable(searchIn, searchFor));
   }

   /**
    * See {@link StringUtils#substringBefore(String, int)}
    */
   public static String substringBefore(final String str, final int separator) {
      return asNonNullUnsafe(StringUtils.substringBefore(str, separator));
   }

   /**
    * See {@link StringUtils#substringBefore(String, String)}
    */
   public static String substringBefore(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.substringBefore(str, separator));
   }

   public static String substringBeforeIgnoreCase(final String searchIn, final @Nullable String searchFor) {
      return asNonNullUnsafe(substringBeforeIgnoreCaseNullable(searchIn, searchFor));
   }

   /**
    * Searches a string from left to right and returns the leftmost characters of the string.
    *
    * substringBeforeIgnoreCare("this is a test", "s") -> "thi"
    *
    * @param searchIn The string whose leftmost characters you want to find.
    * @param searchFor A substring of searchIn. Left returns the characters to the left of searchFor. It finds searchFor by searching searchIn from left to
    *           right.
    * @return The leftmost characters in searchIn.
    *         The number of characters returned is determined by searchFor.
    *         Returns "" if searchFor is not part of searchIn.
    *         Returns "" if searchIn is null.
    */
   public static @Nullable String substringBeforeIgnoreCaseNullable(final @Nullable String searchIn, final @Nullable String searchFor) {
      if (searchIn == null || searchFor == null || searchIn.isEmpty())
         return searchIn;

      if (searchFor.isEmpty())
         return EMPTY;

      final int pos = searchIn.toLowerCase().indexOf(searchFor.toLowerCase());
      return pos < 0 //
            ? EMPTY
            : searchIn.substring(0, pos);
   }

   /**
    * See {@link StringUtils#substringBeforeLast(String, String)}
    */
   public static String substringBeforeLast(final String str, final @Nullable String separator) {
      return asNonNullUnsafe(StringUtils.substringBeforeLast(str, separator));
   }

   /**
    * See {@link StringUtils#substringBeforeLast(String, String)}
    */
   public static @Nullable String substringBeforeLastNullable(final @Nullable String str, final @Nullable String separator) {
      return StringUtils.substringBeforeLast(str, separator);
   }

   /**
    * Searches a string from left to right and returns the leftmost characters of the string.
    *
    * substringBefore("this is a test", "s") -> "thi"
    *
    * @param searchIn The string whose leftmost characters you want to find.
    * @param searchFor A substring of searchIn.
    * @return The leftmost characters in searchIn.
    *         The number of characters returned is determined by searchFor.
    *         Returns searchIn if searchFor is not part of searchIn.
    */
   public static @Nullable String substringBeforeNullable(final @Nullable String searchIn, final char searchFor) {
      if (searchIn == null || searchIn.isEmpty())
         return searchIn;

      final int pos = searchIn.indexOf(searchFor);
      if (pos < 0)
         return searchIn;

      return searchIn.substring(0, pos);
   }

   /**
    * See {@link StringUtils#substringBefore(String, int)}
    */
   public static @Nullable String substringBeforeNullable(final @Nullable String str, final int separator) {
      return StringUtils.substringBefore(str, separator);
   }

   /**
    * See {@link StringUtils#substringBefore(String, String)}
    */
   public static @Nullable String substringBeforeNullable(final @Nullable String str, final @Nullable String separator) {
      return StringUtils.substringBefore(str, separator);
   }

   /**
    * See {@link StringUtils#substringBetween(String, String)}
    */
   public static String substringBetween(final String str, final @Nullable String tag) {
      return asNonNullUnsafe(StringUtils.substringBetween(str, tag));
   }

   /**
    * See {@link StringUtils#substringBetween(String, String, String)}
    */
   public static String substringBetween(final String str, final @Nullable String open, final @Nullable String close) {
      return asNonNullUnsafe(StringUtils.substringBetween(str, open, close));
   }

   /**
    * See {@link StringUtils#substringBetween(String, String)}
    */
   public static @Nullable String substringBetweenNullable(final @Nullable String str, final @Nullable String tag) {
      return StringUtils.substringBetween(str, tag);
   }

   /**
    * See {@link StringUtils#substringBetween(String, String, String)}
    */
   public static @Nullable String substringBetweenNullable(final @Nullable String str, final @Nullable String open,
         final @Nullable String close) {
      return StringUtils.substringBetween(str, open, close);
   }

   /**
    * See {@link StringUtils#substring(String, int)}
    */
   public static @Nullable String substringNullable(final @Nullable String str, final int start) {
      return StringUtils.substring(str, start);
   }

   /**
    * See {@link StringUtils#substring(String, int, int)}
    */
   public static @Nullable String substringNullable(final @Nullable String str, final int start, final int end) {
      return StringUtils.substring(str, start, end);
   }

   /**
    * See {@link StringUtils#substringsBetween(String, String, String)}
    */
   public static String[] substringsBetween(final String str, final @Nullable String open, final @Nullable String close) {
      return asNonNullUnsafe(StringUtils.substringsBetween(str, open, close));
   }

   /**
    * See {@link StringUtils#substringsBetween(String, String, String)}
    */
   public static String @Nullable [] substringsBetweenNullable(final @Nullable String str, final @Nullable String open,
         final @Nullable String close) {
      return StringUtils.substringsBetween(str, open, close);
   }

   /**
    * See {@link StringUtils#swapCase(String)}
    */
   public static String swapCase(final String str) {
      return asNonNullUnsafe(StringUtils.swapCase(str));
   }

   /**
    * See {@link StringUtils#swapCase(String)}
    */
   public static @Nullable String swapCaseNullable(final @Nullable String str) {
      return StringUtils.swapCase(str);
   }

   public static char[] toCharArray(final CharSequence text) {
      return asNonNullUnsafe(toCharArrayNullable(text));
   }

   public static char @Nullable [] toCharArrayNullable(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;
      if (txt.length() == 0)
         return ArrayUtils.EMPTY_CHAR_ARRAY;

      if (txt instanceof String)
         return ((String) txt).toCharArray();

      final int txtLen = txt.length();
      final var chars = new char[txtLen];

      if (txt instanceof StringBuilder) {
         ((StringBuilder) txt).getChars(0, txtLen, chars, 0);
      } else if (txt instanceof StringBuffer) {
         ((StringBuffer) txt).getChars(0, txtLen, chars, 0);
      } else {
         for (int i = 0; i < txtLen - 1; i++) {
            chars[i] = txt.charAt(i);
         }
      }
      return chars;
   }

   /**
    * See {@link StringUtils#toCodePoints(CharSequence)}
    */
   public static int[] toCodePoints(final CharSequence cs) {
      return asNonNullUnsafe(StringUtils.toCodePoints(cs));
   }

   /**
    * See {@link StringUtils#toCodePoints(CharSequence)}
    */
   public static int @Nullable [] toCodePointsNullable(final @Nullable CharSequence cs) {
      return StringUtils.toCodePoints(cs);
   }

   /**
    * See {@link StringUtils#toEncodedString(byte[], Charset)}
    */
   public static String toEncodedString(final byte @Nullable [] bytes, final @Nullable Charset charset) {
      return asNonNullUnsafe(StringUtils.toEncodedString(bytes, charset));
   }

   /**
    * See {@link StringUtils#toEncodedString(byte[], Charset)}
    */
   public static @Nullable String toEncodedStringNullable(final byte @Nullable [] bytes, final @Nullable Charset charset) {
      if (bytes == null)
         return null;
      return StringUtils.toEncodedString(bytes, charset);
   }

   /**
    * @See {@link StringUtils#toRootLowerCase(String)}
    */
   public static String toRootLowerCase(final String source) {
      return asNonNullUnsafe(StringUtils.toRootLowerCase(source));
   }

   /**
    * @See {@link StringUtils#toRootLowerCase(String)}
    */
   public static @Nullable String toRootLowerCaseNullable(final @Nullable String source) {
      return StringUtils.toRootLowerCase(source);
   }

   /**
    * @See {@link StringUtils#toRootUpperCase(String)}
    */
   public static String toRootUpperCase(final String source) {
      return asNonNullUnsafe(StringUtils.toRootUpperCase(source));
   }

   /**
    * @See {@link StringUtils#toRootUpperCase(String)}
    */
   public static @Nullable String toRootUpperCaseNullable(final @Nullable String source) {
      return StringUtils.toRootUpperCase(source);
   }

   public static String toString(final @Nullable Object object) {
      if (object == null)
         return "null";
      return ToStringBuilder.reflectionToString(object, ToStringStyle.DEFAULT_STYLE);
   }

   /**
    * @return "{SimpleClassName}@{HexIdentityHashCode}[{fieldName}={fieldValue},...]"
    */
   public static String toString(final @Nullable Object object, final @NonNullByDefault({}) Object... fieldAndValues) {
      if (object == null)
         return "null";
      final var sb = new StringBuilder(object.getClass().getSimpleName());
      sb.append('@');
      sb.append(Integer.toHexString(System.identityHashCode(object)));
      if (fieldAndValues != null && fieldAndValues.length > 0) {
         sb.append('[');
         boolean isFieldName = true;
         for (final Object item : fieldAndValues) {
            if (isFieldName) {
               sb.append(item).append('=');
            } else {
               if (item instanceof String) {
                  sb.append('"').append(item).append('"');
               } else {
                  sb.append(item);
               }
               sb.append(',');
            }
            isFieldName = !isFieldName;
         }
         if (isFieldName) {
            // remove last ','
            sb.setLength(sb.length() - 1);
         }
         sb.append(']');
      }
      return sb.toString();
   }

   /**
    * @See {@link StringUtils#trimToEmpty(String)}
    */
   public static String trim(final String str) {
      return asNonNullUnsafe(StringUtils.trim(str));
   }

   public static CharSequence trimIndent(final CharSequence multiLineString, final int tabSize) {
      final int effetiveTabSize = Math.max(1, tabSize);

      abstract class CharConsumer implements IntConsumer {

         char prevCh = 0;

         @Override
         public void accept(final int value) {
            final char ch = (char) value;
            onChar(ch);
            prevCh = ch;
         }

         abstract void onChar(char ch);
      }

      /*
       * determine common indentation of all lines
       */
      final class IndentDetector extends CharConsumer implements IntPredicate {
         int indentToRemove = Integer.MAX_VALUE;
         int indentOfLine = 0;
         boolean skipToLineEnd = false;
         int lineCount = 1;

         @Override
         public void onChar(final char ch) {
            if (ch == '\r' && prevCh != '\n' || ch == '\n' && prevCh != '\r') {
               lineCount++;
               skipToLineEnd = false;
               indentToRemove = Math.min(indentOfLine, indentToRemove);
               indentOfLine = 0;
               return;
            }

            if (!skipToLineEnd) {
               if (ch == '\t') {
                  indentOfLine += effetiveTabSize;
               } else if (Character.isWhitespace(ch)) {
                  indentOfLine++;
               } else {
                  skipToLineEnd = true;
               }
            }
         }

         @Override
         public boolean test(final int value) {
            return indentToRemove > 0;
         }
      }

      final var indentDetector = new IndentDetector();
      multiLineString.chars().takeWhile(indentDetector).forEach(indentDetector);

      final var indentToRemove = Math.min(indentDetector.indentOfLine, indentDetector.indentToRemove);
      if (indentToRemove == 0)
         return multiLineString;

      /*
       * remove common indentation of all lines
       */
      final var sb = new StringBuilder(Math.max(0, multiLineString.length() - indentDetector.lineCount * indentToRemove));
      final class IdentRemover extends CharConsumer {
         int removedIndentOfLine = 0;

         @Override
         public void onChar(final char ch) {
            if (ch == '\r' || ch == '\n') {
               sb.append(ch);
               removedIndentOfLine = 0;
               return;
            }
            if (removedIndentOfLine >= indentToRemove) {
               sb.append(ch);
            } else {
               if (ch == '\t') {
                  removedIndentOfLine += effetiveTabSize;
               } else {
                  removedIndentOfLine++;
               }
            }
         }
      }

      multiLineString.chars().forEach(new IdentRemover());
      return sb;
   }

   /**
    * Trims all lines
    */
   public static @Nullable String trimLines(final @Nullable String text) {
      return asNonNullUnsafe(trimLinesNullable(text));
   }

   /**
    * Trims all lines
    */
   public static @Nullable String trimLinesNullable(final @Nullable String text) {
      if (text == null)
         return null;
      if (text.length() == 0)
         return text;

      final var lines = Strings.splitLines(text, true);
      for (int i = 0; i < lines.length; i++) {
         lines[i] = lines[i].trim();
      }
      return join(lines, Strings.NEW_LINE);
   }

   /**
    * @See {@link StringUtils#trimToEmpty(String)}
    */
   public static @Nullable String trimNullable(final @Nullable String str) {
      return StringUtils.trim(str);
   }

   /**
    * @See {@link StringUtils#trimToEmpty(String)}
    */
   public static String trimToEmpty(final String str) {
      return asNonNullUnsafe(StringUtils.trimToEmpty(str));
   }

   /**
    * @See {@link StringUtils#trimToEmpty(String)}
    */
   public static String trimToEmptyNullable(final @Nullable String str) {
      return asNonNullUnsafe(StringUtils.trimToEmpty(str));
   }

   /**
    * @See {@link StringUtils#trimToNull(String)}
    */
   public static String trimToNull(final String str) {
      return asNonNullUnsafe(StringUtils.trimToNull(str));
   }

   /**
    * @See {@link StringUtils#trimToNull(String)}
    */
   public static @Nullable String trimToNullNullable(final @Nullable String str) {
      return StringUtils.trimToNull(str);
   }

   /**
    * @See {@link StringUtils#truncate(String, int)}
    */
   public static String truncate(final String str, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.truncate(str, maxWidth));
   }

   /**
    * @See {@link StringUtils#truncate(String, int, int)}
    */
   public static String truncate(final String str, final int offset, final int maxWidth) {
      return asNonNullUnsafe(StringUtils.truncate(str, offset, maxWidth));
   }

   /**
    * @See {@link StringUtils#truncate(String, int)}
    */
   public static @Nullable String truncateNullable(final @Nullable String str, final int maxWidth) {
      return StringUtils.truncate(str, maxWidth);
   }

   /**
    * @See {@link StringUtils#truncate(String, int, int)}
    */
   public static @Nullable String truncateNullable(final @Nullable String str, final int offset, final int maxWidth) {
      return StringUtils.truncate(str, offset, maxWidth);
   }

   /**
    * @See {@link StringUtils#uncapitalize(String)}
    */
   public static String uncapitalize(final String str) {
      return asNonNullUnsafe(StringUtils.uncapitalize(str));
   }

   /**
    * @See {@link StringUtils#uncapitalize(String)}
    */
   public static @Nullable String uncapitalizeNullable(final @Nullable String str) {
      return StringUtils.uncapitalize(str);
   }

   /**
    * @See {@link StringUtils#unwrap(String, char)}
    */
   public static String unquote(final String str, final char quotedWith) {
      return asNonNullUnsafe(StringUtils.unwrap(str, quotedWith));
   }

   /**
    * @See {@link StringUtils#unwrap(String, String)}
    */
   public static String unquote(final String str, final @Nullable String quotedWith) {
      return asNonNullUnsafe(StringUtils.unwrap(str, quotedWith));
   }

   /**
    * @See {@link StringUtils#unwrap(String, char)}
    */
   public static @Nullable String unquoteNullable(final @Nullable String str, final char quotedWith) {
      return StringUtils.unwrap(str, quotedWith);
   }

   /**
    * @See {@link StringUtils#unwrap(String, String)}
    */
   public static @Nullable String unquoteNullable(final @Nullable String str, final @Nullable String quotedWith) {
      return StringUtils.unwrap(str, quotedWith);
   }

   public static String upperCase(final CharSequence txt) {
      return asNonNullUnsafe(upperCaseNullable(txt));
   }

   public static String upperCase(final Object obj) {
      return asNonNullUnsafe(upperCaseNullable(obj));
   }

   public static String upperCase(final Object obj, final @Nullable Locale locale) {
      return asNonNullUnsafe(upperCaseNullable(obj, locale));
   }

   /**
    * @See {@link StringUtils#upperCase(String)}
    */
   public static String upperCase(final String str) {
      return asNonNullUnsafe(StringUtils.upperCase(str));
   }

   /**
    * @See {@link StringUtils#upperCase(String, Locale)}
    */
   public static String upperCase(final String str, final @Nullable Locale locale) {
      return asNonNullUnsafe(StringUtils.upperCase(str, locale));
   }

   /**
    * Capitalize the first character of the given character sequence.
    * If you need to capitalize all words in a string use commons-text's <code>WordUtils.capitalize(String)</code>
    */
   public static String upperCaseFirstChar(final CharSequence txt) {
      return asNonNullUnsafe(upperCaseFirstCharNullable(txt));
   }

   /**
    * Capitalize the first character of the given character sequence.
    * If you need to capitalize all words in a string use commons-text's <code>WordUtils.capitalize(String)</code>
    */
   public static @Nullable String upperCaseFirstCharNullable(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;

      final int len = txt.length();
      if (len == 0)
         return EMPTY;
      final String firstChar = String.valueOf(Character.toUpperCase(txt.charAt(0)));
      if (len == 1)
         return firstChar;
      return firstChar + txt.subSequence(1, len);
   }

   public static @Nullable String upperCaseNullable(final @Nullable CharSequence txt) {
      if (txt == null)
         return null;
      if (txt.length() == 0)
         return EMPTY;
      if (txt instanceof String)
         return ((String) txt).toUpperCase();

      final var len = txt.length();
      final var chars = new char[len];
      for (int i = 0; i < len; i++) {
         chars[i] = Character.toUpperCase(txt.charAt(i));
      }
      return new String(chars);
   }

   public static @Nullable String upperCaseNullable(final @Nullable Object obj) {
      if (obj == null)
         return null;
      return StringUtils.upperCase(obj.toString());
   }

   public static @Nullable String upperCaseNullable(final @Nullable Object obj, final @Nullable Locale locale) {
      if (obj == null)
         return null;
      return StringUtils.upperCase(obj.toString(), locale);
   }

   /**
    * @See {@link StringUtils#upperCase(String)}
    */
   public static @Nullable String upperCaseNullable(final @Nullable String str) {
      return StringUtils.upperCase(str);
   }

   /**
    * @See {@link StringUtils#upperCase(String, Locale)}
    */
   public static @Nullable String upperCaseNullable(final @Nullable String str, final @Nullable Locale locale) {
      return StringUtils.upperCase(str, locale);
   }

   public static String urlDecode(final String text) {
      return asNonNullUnsafe(urlDecodeNullable(text));
   }

   public static @Nullable String urlDecodeNullable(final @Nullable String text) {
      if (text == null || text.isEmpty())
         return text;
      return URLDecoder.decode(text, StandardCharsets.UTF_8);
   }

   /**
    * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme.
    *
    * @param text the string to be translated
    * @return the translated String
    */
   public static String urlEncode(final String text) {
      return asNonNullUnsafe(urlEncodeNullable(text));
   }

   /**
    * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme.
    *
    * @param text the string to be translated
    * @return the translated String
    */
   public static @Nullable String urlEncodeNullable(final @Nullable String text) {
      if (text == null || text.isEmpty())
         return text;
      return URLEncoder.encode(text, StandardCharsets.UTF_8);
   }

   /**
    * @See {@link StringUtils#valueOf(char[])}
    */
   public static String valueOf(final char[] value) {
      return asNonNullUnsafe(StringUtils.valueOf(value));
   }

   /**
    * @See {@link StringUtils#valueOf(char[])}
    */
   public static @Nullable String valueOfNullable(final char @Nullable [] value) {
      return StringUtils.valueOf(value);
   }
}
