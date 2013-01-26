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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import net.sf.jstuff.core.validation.Args;

/**
 * A non-thread-safe output stream filter that performs on-the fly zip compression using a {@link Deflater}.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockOutputStream extends FilterOutputStream
{
	private final Deflater compressor;

	private final boolean isUseDefaultCompressor;

	/**
	 * Reusable buffer for input data
	 */
	private final byte[] block;

	/**
	 * Reusable buffer for compressed data to be written to the underlying output stream
	 */
	private final byte[] blockCompressed;

	/**
	 * Number of bytes currently held by the input buffer
	 */
	private int blockSize = 0;

	private boolean isClosed = false;

	/**
	 * @param os the underlying output stream to send the data in compressed form to
	 * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying stream
	 */
	public ZippedBlockOutputStream(final OutputStream os, final int blockSize)
	{
		this(os, blockSize, Deflater.DEFAULT_COMPRESSION);
	}

	/**
	 * @param os the underlying output stream to send the data in compressed form to
	 * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying stream
	 */
	public ZippedBlockOutputStream(final OutputStream os, final int blockSize, final Deflater compressor)
	{
		super(os);

		Args.notNull("os", os);
		Args.minSize("blockSize", blockSize, 1);

		this.compressor = compressor == null ? new Deflater() : compressor;
		isUseDefaultCompressor = false;
		block = new byte[blockSize];
		blockCompressed = new byte[blockSize * 2]; // using larger buffer in case of negative compression ratio
	}

	/**
	 * @param os the underlying output stream to send the data in compressed form to
	 * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying stream
	 * @param compressionLevel the java.util.zip.Deflater compression level (0-9)
	 */
	public ZippedBlockOutputStream(final OutputStream os, final int blockSize, final int compressionLevel)
	{
		super(os);

		Args.notNull("os", os);
		Args.minSize("blockSize", blockSize, 1);
		Args.inRange("compressionLevel", compressionLevel, 0, 9);

		compressor = new Deflater(compressionLevel);
		isUseDefaultCompressor = true;
		block = new byte[blockSize];
		blockCompressed = new byte[blockSize * 2]; // using larger buffer in case of negative compression ratio
	}

	protected void assertIsOpen() throws IOException
	{
		if (isClosed) throw new IOException("Stream closed");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException
	{
		if (!isClosed)
		{
			flush();
			if (isUseDefaultCompressor) compressor.end();
			out.close();
			isClosed = true;
		}
	}

	/**
	 * Compresses the data currently in the <code>block</code> byte array into the <code>blockCompressed</code>
	 * byte array and writes it to the underlying output stream
	 */
	protected void compressBlockAndWrite() throws IOException
	{
		// anything to do?
		if (blockSize > 0)
		{
			// compress the current input data
			compressor.setInput(block, 0, blockSize);
			compressor.finish();
			final int compressedSize = compressor.deflate(blockCompressed);
			System.out.println(block.length + " - " + blockSize + " / " + blockCompressed.length + " - " + compressedSize);
			// write the size of the compressed data
			IOUtils.writeInt(out, compressedSize);

			// write the size of the uncompressed data
			IOUtils.writeInt(out, blockSize);

			// write the compressed data
			out.write(blockCompressed, 0, compressedSize);

			// flush and reset the buffer
			out.flush();
			blockSize = 0;
			compressor.reset();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() throws IOException
	{
		assertIsOpen();

		compressBlockAndWrite();
	}

	public Deflater getCompressor()
	{
		return compressor;
	}

	public boolean isClosed()
	{
		return isClosed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException
	{
		Args.notNull("b", b);
		Args.inRange("off", off, 0, b.length - 1);
		Args.inRange("len", len, 0, b.length - off);

		if (len == 0) return;

		assertIsOpen();

		int remainingSize = len;
		int remainingOffset = off;

		// process the b array in chunks if it's content does not fit into the block array
		while (blockSize + remainingSize > block.length)
		{
			// calculate the number of bytes that can be written in this pass
			final int writeSize = block.length - blockSize;
			System.arraycopy(b, remainingOffset, block, blockSize, writeSize);
			blockSize += writeSize;
			compressBlockAndWrite();

			// adjust remaining offset and length
			remainingOffset += writeSize;
			remainingSize -= writeSize;
		}

		System.arraycopy(b, remainingOffset, block, blockSize, remainingSize);
		blockSize += remainingSize;

		// if the block array is full process it
		if (blockSize == block.length) compressBlockAndWrite();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final int b) throws IOException
	{
		assertIsOpen();

		// add the byte to the input data buffer
		block[blockSize] = (byte) b;
		blockSize++;

		// if the input data buffer is full compress
		if (blockSize == block.length) compressBlockAndWrite();
	}
}