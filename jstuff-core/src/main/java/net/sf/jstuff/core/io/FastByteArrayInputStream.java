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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link ByteArrayInputStream}.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayInputStream extends InputStream
{
	private final byte[] data;
	private int pos;
	private final int count;
	private int mark;

	public FastByteArrayInputStream(final byte[] data)
	{
		this(data, 0, data.length);
	}

	public FastByteArrayInputStream(final byte[] data, final int offset, final int length)
	{
		Args.notNull("data", data);
		Args.notNegative("offset", offset);
		Args.notNegative("length", length);

		this.data = data;
		pos = offset;
		count = Math.min(offset + length, data.length);
		mark = offset;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int available() throws IOException
	{
		return count - pos;
	}

	/**
	 * Closing a {@link FastByteArrayInputStream} has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an {@link IOException}.
	 */
	@Override
	public void close()
	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mark(final int readLimit)
	{
		mark = pos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean markSupported()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException
	{
		return pos < count ? data[pos++] & 0xff : IOUtils.EOF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(final byte buf[], final int offset, int length) throws IOException
	{
		Args.notNull("buf", buf);
		Args.notNegative("offset", offset);
		Args.notNegative("length", length);

		if (offset > buf.length || offset + length > buf.length) throw new IndexOutOfBoundsException();

		if (count < pos) return IOUtils.EOF;

		if (pos + length > count) length = count - pos;
		if (length <= 0) return 0;

		System.arraycopy(data, pos, buf, offset, length);
		pos += length;
		return length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() throws IOException
	{
		pos = mark;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long skip(long n) throws IOException
	{
		if (pos + n > count) n = count - pos;
		if (n < 0) return 0;
		pos += n;
		return n;
	}
}
