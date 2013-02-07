/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.concurrent.ThreadUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class IOUtils extends org.apache.commons.io.IOUtils
{
	private static final Logger LOG = Logger.create();

	public static final int EOF = -1;

	public static int copyAndClose(final InputStream is, final OutputStream os) throws IOException
	{
		final int bytesCopied = copy(is, os);
		closeQuietly(is);
		closeQuietly(os);
		return bytesCopied;
	}

	@SuppressWarnings("resource")
	public static byte[] readBytes(final InputStream is) throws IOException
	{
		final FastByteArrayOutputStream os = new FastByteArrayOutputStream();
		copy(is, os);
		return os.toByteArray();
	}

	/**
	 * Reads <code>len</code> bytes of data from the input stream into
	 * an array of bytes. This method blocks until the given number of bytes could be read.
	 * @param b   the buffer into which the data is read.
	 * @param off the start offset in array <code>b</code> at which the data is written.
	 * @param len the exact number of bytes to read.
	 */
	public static void readBytes(final InputStream is, final byte[] b, final int off, final int len) throws IOException
	{
		Args.notNull("b", b);
		Args.inRange("off", off, 0, b.length - 1);
		Args.inRange("len", len, 0, b.length - off);

		int currentOffset = off;
		int bytesMissing = len;
		while (bytesMissing > 0)
		{
			final int bytesRead = is.read(b, currentOffset, bytesMissing);
			if (bytesRead == IOUtils.EOF) throw new EOFException("Unexpected end of input stream reached.");
			currentOffset += bytesRead;
			bytesMissing -= bytesRead;
		}
	}

	/**
	 * Reads <code>len</code> bytes of data from the input stream into
	 * an array of bytes. This method blocks until the given number of bytes could be read.
	 * @param len the exact number of bytes to read.
	 */
	public static byte[] readBytes(final InputStream is, final int len) throws IOException
	{
		final byte[] bytes = new byte[len];
		readBytes(is, bytes, 0, len);
		return bytes;
	}

	@SuppressWarnings("resource")
	public static byte[] readBytesAndClose(final InputStream is) throws IOException
	{
		final FastByteArrayOutputStream os = new FastByteArrayOutputStream();
		try
		{
			copy(is, os);
			return os.toByteArray();
		}
		finally
		{
			closeQuietly(is);
		}
	}

	public static String readChunkAsString(final InputStream is, final int maxSize) throws IOException
	{
		if (is.available() == 0) return "";
		final byte[] buff = new byte[maxSize];
		final int readLen = is.read(buff);
		if (readLen == IOUtils.EOF) return "";
		return new String(buff, 0, readLen);
	}

	/**
	 * Reads an int value from the given input stream using the same way as {@link DataInputStream#readInt()}
	 */
	public static int readInt(final InputStream is) throws IOException
	{
		final int ch1 = is.read();
		final int ch2 = is.read();
		final int ch3 = is.read();
		final int ch4 = is.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) throw new EOFException("Unexpected end of input stream reached.");
		return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
	}

	public static List<String> readLines(final InputStream in) throws IOException
	{
		return readLines(new InputStreamReader(in));
	}

	public static List<String> readLines(final Reader reader) throws IOException
	{
		@SuppressWarnings("resource")
		final BufferedReader bf = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
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

	/**
	 * Writes the given value to the output stream the same way as {@link DataOutputStream#writeInt(int)}
	 */
	public static void writeInt(final OutputStream os, final int value) throws IOException
	{
		os.write(value >>> 24 & 0xFF);
		os.write(value >>> 16 & 0xFF);
		os.write(value >>> 8 & 0xFF);
		os.write(value >>> 0 & 0xFF);
	}

	protected IOUtils()
	{
		super();
	}
}