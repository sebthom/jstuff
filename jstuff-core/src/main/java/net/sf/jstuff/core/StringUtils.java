/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.CharSequenceReader;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StringUtils extends org.apache.commons.lang3.StringUtils
{
	public static final class ANSIState
	{
		public String bgcolor;
		public boolean blink;
		public boolean bold;
		public String fgcolor;
		public boolean underline;

		public ANSIState()
		{
			reset();
		}

		public boolean isActive()
		{
			return fgcolor != null || bgcolor != null || bold || underline || blink;
		}

		public void reset()
		{
			fgcolor = null;
			bgcolor = null;
			bold = false;
			underline = false;
			blink = false;
		}

		public void setGraphicModeParameter(final int param)
		{
			switch (param)
			{
				case 0 :
					reset();
					break;
				case 1 :
					bold = true;
					break;
				case 4 :
					underline = true;
					break;
				case 5 :
					blink = true;
					break;
				case 30 :
					fgcolor = "black";
					break;
				case 31 :
					fgcolor = "red";
					break;
				case 32 :
					fgcolor = "green";
					break;
				case 33 :
					fgcolor = "yellow";
					break;
				case 34 :
					fgcolor = "blue";
					break;
				case 35 :
					fgcolor = "magenta";
					break;
				case 36 :
					fgcolor = "cyan";
					break;
				case 37 :
					fgcolor = "white";
					break;
				case 40 :
					bgcolor = "black";
					break;
				case 41 :
					bgcolor = "red";
					break;
				case 42 :
					bgcolor = "green";
					break;
				case 43 :
					bgcolor = "yellow";
					break;
				case 44 :
					bgcolor = "blue";
					break;
				case 45 :
					bgcolor = "magenta";
					break;
				case 46 :
					bgcolor = "cyan";
					break;
				case 47 :
					bgcolor = "white";
					break;
			}
		}

		public String toCSS()
		{
			if (isActive())
			{
				final StringBuilder sb = new StringBuilder();
				if (fgcolor != null) sb.append("color:").append(fgcolor).append(";");
				if (bgcolor != null) sb.append("background-color:").append(bgcolor).append(";");
				if (bold) sb.append("font-weight:bold;");
				if (underline) sb.append("text-decoration:underline;");
				if (blink) sb.append("text-decoration: blink;");
				return sb.toString();
			}
			return "";
		}
	}

	public static final char CR = 13;

	public static final char LF = 10;

	public static final String CR_LF = "" + CR + LF;
	public static final String NEW_LINE = System.getProperty("line.separator");

	public static CharSequence ansiColorsToHTML(final CharSequence txt)
	{
		if (isEmpty(txt)) return txt;
		return ansiColorsToHTML(txt, new ANSIState());
	}

	public static CharSequence ansiColorsToHTML(final CharSequence txt, final ANSIState initialState)
	{
		if (isEmpty(txt)) return txt;
		Args.notNull("initialState", initialState);

		final char ESCAPE = '\u001B';

		final StringBuilder sb = new StringBuilder(txt.length());
		final StringBuilder lookAhead = new StringBuilder(8);

		if (initialState.isActive()) sb.append("<span style=\"").append(initialState.toCSS()).append("\">");

		for (int i = 0, l = txt.length(); i < l; i++)
		{
			final char ch = txt.charAt(i);
			if (ch == ESCAPE && i < l && txt.charAt(i + 1) == '[')
			{
				lookAhead.setLength(0);
				int currentGraphicModeParam = 0;
				boolean done = false;
				final boolean isActiveOld = initialState.isActive();
				for (i = i + 2; i < l; i++)
				{
					final char ch2 = txt.charAt(i);
					lookAhead.append(ch2);
					switch (ch2)
					{
						case '0' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 0;
							break;
						case '1' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 1;
							break;
						case '2' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 2;
							break;
						case '3' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 3;
							break;
						case '4' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 4;
							break;
						case '5' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 5;
							break;
						case '6' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 6;
							break;
						case '7' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 7;
							break;
						case '8' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 8;
							break;
						case '9' :
							currentGraphicModeParam = currentGraphicModeParam * 10 + 9;
							break;
						case ';' :
							initialState.setGraphicModeParameter(currentGraphicModeParam);
							currentGraphicModeParam = 0;
							break;
						case 'm' :
							initialState.setGraphicModeParameter(currentGraphicModeParam);
							currentGraphicModeParam = 0;
							if (isActiveOld) sb.append("</span>");
							if (initialState.isActive()) sb.append("<span style=\"").append(initialState.toCSS()).append("\">");
							done = true;
							break;
						default :
							// in case an unexpected value has been found we know this is not a graphic mode setting sequence
							sb.append(ESCAPE).append('[').append(lookAhead);
							done = true;
					}
					if (done) break;
				}
				if (!done) sb.append(ESCAPE).append('[').append(lookAhead); // in case of unexpected ending of string
			}
			else
				sb.append(ch);
		}

		if (initialState.isActive()) sb.append("</span>");
		return sb;
	}

	/**
	 * @return true if searchIn contains ANY of the substrings in searchFor
	 */
	public static boolean containsAny(final CharSequence searchIn, final String... searchFor)
	{
		if (isEmpty(searchIn) || ArrayUtils.isEmpty(searchFor)) return false;

		if (searchIn instanceof String)
		{
			final String searchIn2 = (String) searchIn;
			for (final String sf : searchFor)
				if (searchIn2.indexOf(sf) > -1) return true;
		}
		else if (searchIn instanceof StringBuffer)
		{
			final StringBuffer searchIn2 = (StringBuffer) searchIn;
			for (final String sf : searchFor)
				if (searchIn2.indexOf(sf) > -1) return true;
		}
		else if (searchIn instanceof StringBuilder)
		{
			final StringBuilder searchIn2 = (StringBuilder) searchIn;
			for (final String sf : searchFor)
				if (searchIn2.indexOf(sf) > -1) return true;
		}
		else
		{
			final String searchIn2 = searchIn.toString();
			for (final String sf : searchFor)
				if (searchIn2.indexOf(sf) > -1) return true;
		}
		return false;
	}

	/**
	 * @return true if any searchIn contains ANY of the substrings in searchFor
	 */
	public static boolean containsAny(final Collection< ? extends CharSequence> searchIn, final String... searchFor)
	{
		for (final CharSequence s : searchIn)
			if (containsAny(s, searchFor)) return true;
		return false;
	}

	public static boolean containsDigit(final String searchIn)
	{
		if (searchIn == null || searchIn.length() == 0) return false;

		for (int i = 0, l = searchIn.length(); i < l; i++)
			if (Character.isDigit((int) searchIn.charAt(i))) return true;
		return false;
	}

	/**
	 * <p>Counts how many times the substring appears in the larger String starting at the given position.</p>
	 *
	 * @param searchIn the String to check, may be null
	 * @param searchFor the substring to count, may be null
	 * @param startAt
	 * @return the number of occurrences, 0 if either String is <code>null</code>
	 */
	public static int countMatches(final String searchIn, final String searchFor, final int startAt)
	{
		if (isEmpty(searchIn) || isEmpty(searchFor) || startAt >= searchIn.length() || startAt < 0) return 0;

		int count = 0;
		int foundAt = startAt > -1 ? startAt - 1 : 0;
		while ((foundAt = searchIn.indexOf(searchFor, foundAt + 1)) > -1)
			count++;
		return count;
	}

	public static <T extends CharSequence> T emptyToNull(final T txt)
	{
		return txt == null ? null : txt.length() == 0 ? null : txt;
	}

	public static boolean endsWith(final CharSequence str, final char ch)
	{
		return isEmpty(str) ? false : str.charAt(str.length() - 1) == ch;
	}

	public static CharSequence htmlEncode(final CharSequence text)
	{
		final int textLen = text.length();
		final StringBuilder sb = new StringBuilder(textLen);

		boolean isFirstSpace = true;

		for (int i = 0; i < textLen; i++)
		{
			final char ch = text.charAt(i);

			if (ch == ' ')
			{
				if (isFirstSpace)
				{
					sb.append(' ');
					isFirstSpace = false;
				}
				else
					sb.append("&nbsp;");
				continue;
			}
			if (ch == '<')
				sb.append("&lt;");
			else if (ch == '>')
				sb.append("&gt;");
			else if (ch == '\'')
				sb.append("&apos;");
			else if (ch == '"')
				sb.append("&quot;");
			else if (ch == '&')
				sb.append("&amp;");
			else if (ch == LF)
				sb.append("&lt;br/&gt;");
			else if (ch < 160)
				sb.append(ch);
			else
				sb.append("&#").append((int) ch).append(';');
			isFirstSpace = true;
		}
		return sb;
	}

	@SuppressWarnings("resource")
	public static CharSequence htmlToPlainText(final CharSequence html)
	{
		Args.notNull("html", html);

		final StringBuilder sb = new StringBuilder();

		try
		{
			final ParserDelegator pd = new ParserDelegator();
			pd.parse(new CharSequenceReader(html), new ParserCallback()
				{
					@Override
					public void handleText(final char[] text, final int pos)
					{
						sb.append(text);
					}
				}, true);
		}
		catch (final IOException ex)
		{
			throw new RuntimeException(ex);
		}
		return sb;
	}

	public static String join(final Iterable< ? > iterable)
	{
		if (iterable == null) return null;
		return join(iterable.iterator(), null);
	}

	/**
	 * Capitalize the first character of the given character sequence.
	 * If you need to capitalize all words in a string use {@link WordUtils#uncapitalize(String)}
	 */
	public static String lowerCaseFirstChar(final CharSequence txt)
	{
		if (txt == null) return null;

		final int len = txt.length();
		if (len == 0) return "";
		final String firstChar = String.valueOf(Character.toLowerCase(txt.charAt(0)));
		if (len == 1) return firstChar;
		return firstChar + txt.subSequence(1, len);
	}

	public static String nullToEmpty(final Object txt)
	{
		return txt == null ? "" : txt instanceof String ? (String) txt : txt.toString();
	}

	/**
	 * <p>Repeat a String <code>repeat</code> times to form a new String.</p>
	 */
	public static CharSequence repeat(final CharSequence text, final int repeat)
	{
		Assert.isTrue(repeat > 1, "Argument [repeat] cannot be negative");

		final StringBuilder sb = new StringBuilder(text.length() * repeat);

		for (int i = 0; i < repeat; i++)
			sb.append(text);
		return sb;
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
	public static String replace(final String searchIn, final int startAt, final CharSequence replaceWith)
	{
		return replace(searchIn, startAt, searchIn.length(), replaceWith);
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
	public static String replace(final String searchIn, int startAt, int length, final CharSequence replaceWith)
	{
		if (searchIn == null || replaceWith == null) return searchIn;

		final int stringLength = searchIn.length();

		if (startAt < 0)
		{
			startAt = stringLength + startAt;
			if (startAt < 0) startAt = 0;
		}
		else if (startAt > stringLength) startAt = stringLength;

		if (length < 0)
		{
			length = stringLength + length;
			if (length < startAt)
				length = 0;
			else
				length -= startAt;
		}

		final int end = startAt + length > stringLength ? stringLength : startAt + length;

		return searchIn.substring(0, startAt) + replaceWith + searchIn.substring(end);
	}

	/**
	 * Replace all occurrences of searchFor in searchIn with replaceWith.
	 */
	public static void replace(final StringBuilder searchIn, final String searchFor, final String replaceWith)
	{
		final int searchForLen = searchFor.length();
		final int replaceWithLen = replaceWith.length();
		int index = searchIn.indexOf(searchFor);
		while (index != -1)
		{
			searchIn.replace(index, index + searchForLen, replaceWith);
			index = searchIn.indexOf(searchFor, index + replaceWithLen);
		}
	}

	/**
	 * Replace all occurrences of the search string with the replacement string.
	 * This method is case insensitive.
	 *
	 * Example:
	 *
	 *    String result = replaceSubString ("Kiss my ass.", "ASS", "lips");
	 *    System.out.println(result);
	 *
	 * This prints the string "Kiss my lips." to the console.
	 *
	 * @param searchIn The string to search
	 * @param searchFor The string to find
	 * @param replaceWith The string to replace searchFor with.
	 * @return Returns searchIn with all occurrences of searchFor replaced with replaceWith. If any parameter is null, searchIn will be returned.
	 */
	public static CharSequence replaceIgnoreCase(final String searchIn, String searchFor, CharSequence replaceWith)
	{
		if (searchIn == null || searchFor == null) return searchIn;
		final int searchInLen = searchIn.length();
		if (searchInLen == 0) return searchIn;
		if (replaceWith == null) replaceWith = "";

		final int searchForLen = searchFor.length();
		final StringBuilder out = new StringBuilder();

		int startSearchAt = 0;
		int foundAt = 0;

		searchFor = searchFor.toLowerCase();
		final String searchInLowerCase = searchIn.toLowerCase();

		while ((foundAt = searchInLowerCase.indexOf(searchFor, startSearchAt)) >= 0)
		{
			out.append(searchIn.substring(startSearchAt, foundAt));
			out.append(replaceWith);
			startSearchAt = foundAt + searchForLen;
		}
		out.append(searchIn.substring(startSearchAt, searchInLen));
		return out;
	}

	public static String[] splitLines(final String text)
	{
		if (text.indexOf(NEW_LINE) > -1) return split(text, StringUtils.NEW_LINE);
		if (text.indexOf(CR_LF) > -1) return splitByWholeSeparatorPreserveAllTokens(text, CR_LF);
		if (text.indexOf(LF) > -1) return split(text, LF);
		if (text.indexOf(CR) > -1) return split(text, CR);
		return new String[]{text};
	}

	public static String[] splitLinesPreserveAllTokens(final String text)
	{
		if (text.indexOf(NEW_LINE) > -1) return splitByWholeSeparatorPreserveAllTokens(text, StringUtils.NEW_LINE);
		if (text.indexOf(CR_LF) > -1) return splitByWholeSeparatorPreserveAllTokens(text, CR_LF);
		if (text.indexOf(LF) > -1) return splitPreserveAllTokens(text, LF);
		if (text.indexOf(CR) > -1) return splitPreserveAllTokens(text, CR);
		return new String[]{text};
	}

	public static boolean startsWith(final CharSequence str, final char ch)
	{
		return isEmpty(str) ? false : str.charAt(0) == ch;
	}

	public static String stripAnsiEscapeSequences(final String in)
	{
		if (in == null) return null;
		return in.replaceAll("\u001B\\[[;\\d]*m", "");
	}

	/**
	 * Searches a string from left to right and returns the leftmost characters of the string.
	 *
	 * substringBefore("this is a test", "s") -> "thi"
	 *
	 * @param searchIn The string whose leftmost characters you want to find.
	 * @param searchFor A substring of searchIn. Left returns the characters to the left of searchFor. It finds searchFor by searching searchIn from left to right.
	 * @return The leftmost characters in searchIn.
	 *         The number of characters returned is determined by searchFor.
	 *         Returns "" if searchFor is not part of searchIn.
	 *         Returns "" if searchIn is null.
	 */
	public static String substringBefore(final String searchIn, final char searchFor)
	{
		if (isEmpty(searchIn)) return searchIn;

		final int pos = searchIn.indexOf(searchFor);
		if (pos < 0) return "";

		return searchIn.substring(0, pos);
	}

	/**
	 * Searches a string from left to right and returns the leftmost characters of the string.
	 *
	 * substringBeforeIgnoreCare("this is a test", "s") -> "thi"
	 *
	 * @param searchIn The string whose leftmost characters you want to find.
	 * @param searchFor A substring of searchIn. Left returns the characters to the left of searchFor. It finds searchFor by searching searchIn from left to right.
	 * @return The leftmost characters in searchIn.
	 *         The number of characters returned is determined by searchFor.
	 *         Returns "" if searchFor is not part of searchIn.
	 *         Returns "" if searchIn is null.
	 */
	public static String substringBeforeIgnoreCase(final String searchIn, final String searchFor)
	{
		if (searchIn == null) return "";

		final int pos = searchIn.toLowerCase().indexOf(searchFor.toLowerCase());

		if (pos < 0) return "";

		return searchIn.substring(0, pos);
	}

	public static char[] toCharArray(final CharSequence txt)
	{
		if (isEmpty(txt)) return ArrayUtils.EMPTY_CHAR_ARRAY;

		if (txt instanceof String) return ((String) txt).toCharArray();

		final int txtLen = txt.length();
		final char[] chars = new char[txtLen];

		if (txt instanceof StringBuilder)
			((StringBuilder) txt).getChars(0, txtLen, chars, 0);

		else if (txt instanceof StringBuffer)
			((StringBuffer) txt).getChars(0, txtLen, chars, 0);

		else
			for (int i = 0; i < txtLen - 1; i++)
				chars[i] = txt.charAt(i);

		return chars;
	}

	public static String toString(final Object object)
	{
		if (object == null) return "null";
		return ToStringBuilder.reflectionToString(object);
	}

	/**
	 * Trims all lines
	 * @param text
	 */
	public static String trimLines(final String text)
	{
		if (text == null) return null;
		if (text.length() == 0) return text;

		final String[] lines = splitLinesPreserveAllTokens(text);
		for (int i = 0; i < lines.length; i++)
			lines[i] = lines[i].trim();
		return join(lines, NEW_LINE);
	}

	public static String truncate(final String text, final int maxLength)
	{
		if (text == null) return null;
		if (text.length() <= maxLength) return text;

		return text.substring(0, maxLength - 1);
	}

	/**
	 * Capitalize the first character of the given character sequence.
	 * If you need to capitalize all words in a string use {@link WordUtils#capitalize(String)}
	 */
	public static String upperCaseFirstChar(final CharSequence txt)
	{
		if (txt == null) return null;

		final int len = txt.length();
		if (len == 0) return "";
		final String firstChar = String.valueOf(Character.toUpperCase(txt.charAt(0)));
		if (len == 1) return firstChar;
		return firstChar + txt.subSequence(1, len);
	}

	public static String urlDecode(final String text)
	{
		if (isEmpty(text)) return text;

		try
		{
			return URLDecoder.decode(text, "UTF-8");
		}
		catch (final UnsupportedEncodingException ex)
		{
			throw new RuntimeException("UTF-8 not supported", ex);
		}
	}

	/**
	 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme.
	 *
	 * @param text the string to be translated
	 * @return the translated String
	 */
	public static String urlEncode(final String text)
	{
		if (isEmpty(text)) return text;

		try
		{
			return URLEncoder.encode(text, "UTF-8");
		}
		catch (final UnsupportedEncodingException ex)
		{
			throw new RuntimeException("UTF-8 not supported", ex);
		}
	}

	/**
	 * {@link  WordUtils#wrap(String, int)}
	 */
	public static String wrap(final String str, final int wrapLength)
	{
		return WordUtils.wrap(str, wrapLength);
	}

	/**
	 * {@link  WordUtils#wrap(String, int)}
	 */
	public static String wrap(final String str, final int wrapLength, final String newLineStr)
	{
		return WordUtils.wrap(str, wrapLength, newLineStr, false);
	}
}
