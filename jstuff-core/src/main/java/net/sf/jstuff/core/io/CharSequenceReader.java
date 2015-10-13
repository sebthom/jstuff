/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReader extends Reader
{
	private CharSequence text;
	private int next = 0;
	private int mark = 0;

	/**
	 * Create a new CharSequence reader.
	 *
	 * @param text CharSequence providing the character stream.
	 */
	public CharSequenceReader(final CharSequence text)
	{
		this.text = text;
	}

	/**
	 * Close the stream.
	 */
	@Override
	public void close()
	{
		text = null;
	}

	/** Check to make sure that the stream has not been closed */
	private void ensureOpen() throws IOException
	{
		if (text == null) throw new IOException("Stream closed");
	}

	/**
	 * Mark the present position in the stream.  Subsequent calls to reset()
	 * will reposition the stream to this point.
	 *
	 * @param  readAheadLimit  Limit on the number of characters that may be
	 *                         read while still preserving the mark.  Because
	 *                         the stream's input comes from a string, there
	 *                         is no actual limit, so this argument must not
	 *                         be negative, but is otherwise ignored.
	 *
	 * @exception  IllegalArgumentException  If readAheadLimit is < 0
	 * @exception  IOException  If an I/O error occurs
	 */
	@Override
	public void mark(final int readAheadLimit) throws IOException
	{
		if (readAheadLimit < 0) throw new IllegalArgumentException("Read-ahead limit < 0");
		synchronized (lock)
		{
			ensureOpen();
			mark = next;
		}
	}

	/**
	 * Tell whether this stream supports the mark() operation, which it does.
	 */
	@Override
	public boolean markSupported()
	{
		return true;
	}

	/**
	 * Read a single character.
	 *
	 * @return     The character read, or -1 if the end of the stream has been
	 *             reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	@Override
	public int read() throws IOException
	{
		synchronized (lock)
		{
			ensureOpen();
			if (next >= text.length()) return IOUtils.EOF;
			return text.charAt(next++);
		}
	}

	/**
	 * Read characters into a portion of an array.
	 *
	 * @param      cbuf  Destination buffer
	 * @param      off   Offset at which to start writing characters
	 * @param      len   Maximum number of characters to read
	 *
	 * @return     The number of characters read, or -1 if the end of the
	 *             stream has been reached
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException
	{
		synchronized (lock)
		{
			ensureOpen();

			if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0)
				throw new IndexOutOfBoundsException();
			else if (len == 0) return 0;

			if (next >= text.length()) return IOUtils.EOF;

			final int n = Math.min(text.length() - next, len);

			if (text instanceof String)
				((String) text).getChars(next, next + n, cbuf, off);
			else if (text instanceof StringBuilder)
				((StringBuilder) text).getChars(next, next + n, cbuf, off);
			else if (text instanceof StringBuffer)
				((StringBuffer) text).getChars(next, next + n, cbuf, off);
			else
				for (int i = next, l = next + n; i < l; i++)
					cbuf[off + i] = text.charAt(i);
			next += n;
			return n;
		}
	}

	/**
	 * Tell whether this stream is ready to be read.
	 *
	 * @return True if the next read() is guaranteed not to block for input
	 *
	 * @exception  IOException  If the stream is closed
	 */
	@Override
	public boolean ready() throws IOException
	{
		synchronized (lock)
		{
			ensureOpen();
			return true;
		}
	}

	/**
	 * Reset the stream to the most recent mark, or to the beginning of the
	 * string if it has never been marked.
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	@Override
	public void reset() throws IOException
	{
		synchronized (lock)
		{
			ensureOpen();
			next = mark;
		}
	}

	/**
	 * Skips the specified number of characters in the stream. Returns
	 * the number of characters that were skipped.
	 *
	 * <p>The <code>ns</code> parameter may be negative, even though the
	 * <code>skip</code> method of the {@link Reader} superclass throws
	 * an exception in this case. Negative values of <code>ns</code> cause the
	 * stream to skip backwards. Negative return values indicate a skip
	 * backwards. It is not possible to skip backwards past the beginning of
	 * the string.
	 *
	 * <p>If the entire string has been read or skipped, then this method has
	 * no effect and always returns 0.
	 *
	 * @exception  IOException  If an I/O error occurs
	 */
	@Override
	public long skip(final long ns) throws IOException
	{
		synchronized (lock)
		{
			ensureOpen();
			if (next >= text.length()) return 0;
			// Bound skip by beginning and end of the source
			long n = Math.min(text.length() - next, ns);
			n = Math.max(-next, n);
			next += n;
			return n;
		}
	}
}