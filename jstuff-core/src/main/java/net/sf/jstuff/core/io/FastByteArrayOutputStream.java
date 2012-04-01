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
package net.sf.jstuff.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link ByteArrayOutputStream}.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayOutputStream extends OutputStream
{
	private byte[] data;
	private int count;

	public FastByteArrayOutputStream()
	{
		this(32);
	}

	public FastByteArrayOutputStream(final int size)
	{
		Args.notNegative("size", size);

		data = new byte[size];
	}

	/**
	 * Closing a {@link FastByteArrayOutputStream} has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an {@link IOException}.
	 */
	@Override
	public void close() throws IOException
	{}

	public void reset()
	{
		count = 0;
	}

	public byte[] toByteArray()
	{
		final byte copy[] = new byte[count];
		System.arraycopy(data, 0, copy, 0, count);
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return new String(data, 0, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final byte[] buf, final int offset, final int length) throws IOException
	{
		Args.notNull("data", data);
		Args.notNegative("offset", offset);
		Args.notNegative("length", length);

		if (offset > buf.length || offset + length > buf.length) throw new IndexOutOfBoundsException();

		if (length == 0) return;

		final int newcount = count + length;
		if (newcount > data.length)
		{
			final byte copy[] = new byte[Math.max(data.length << 1, newcount)];
			System.arraycopy(data, 0, copy, 0, count);
			data = copy;
		}
		System.arraycopy(buf, offset, data, count, length);
		count = newcount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final int b) throws IOException
	{
		final int newcount = count + 1;
		if (newcount > data.length)
		{
			final byte copy[] = new byte[Math.max(data.length << 1, newcount)];
			System.arraycopy(data, 0, copy, 0, count);
			data = copy;
		}
		data[count] = (byte) b;
		count = newcount;
	}

	public void writeTo(final OutputStream out) throws IOException
	{
		out.write(data, 0, count);
	}
}
