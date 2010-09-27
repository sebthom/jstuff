/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.ThreadUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOUtils extends org.apache.commons.io.IOUtils
{
	private static final Logger LOG = Logger.get();

	public static String readChunkAsString(final InputStream is, final int blockSize) throws IOException
	{
		if (is.available() == 0) return "";
		final byte[] buff = new byte[blockSize];
		final int readLen = is.read(buff);
		if (readLen == -1) return "";
		return new String(buff, 0, readLen);
	}

	public static List<String> readLines(final InputStream in) throws IOException
	{
		return readLines(new InputStreamReader(in));
	}

	public static List<String> readLines(final Reader reader) throws IOException
	{
		final BufferedReader bf = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(
				reader);
		final List<String> lines = new ArrayList<String>();
		String line = bf.readLine();
		while (line != null)
		{
			lines.add(line);
			line = bf.readLine();
		}

		LOG.trace("Lines read from reader %s: %s", reader, lines);
		return lines;
	}

	/**
	 * @param searchFor single string or an array of strings where one must match
	 */
	public static CharSequence readUntilContainsAny(final InputStream is, final String... searchFor) throws IOException
	{
		LOG.trace("Reading from stream %s until it contains any of %s", is, searchFor);
		final StringBuilder result = new StringBuilder();
		while (true)
		{
			final String output = readChunkAsString(is, 4096);
			LOG.trace("Current output from stream %s: %s", is, output);
			result.append(output);
			if (StringUtils.containsAny(result, searchFor))
			{
				LOG.trace("One of %s was found in stream %s", searchFor, is);
				break;
			}
			ThreadUtils.sleep(100);
		}
		return result;
	}

	protected IOUtils()
	{
		super();
	}
}