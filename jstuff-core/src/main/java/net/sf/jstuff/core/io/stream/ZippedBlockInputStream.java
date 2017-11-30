/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.core.io.stream;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.io.IOExceptionWithCause;

/**
 * A non-thread-safe input stream filter that performs on-the fly zip decompression using an {@link Inflater}.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockInputStream extends FilterInputStream {
    /**
     * Reusable buffer for the compressed data read from the underlying input stream
     */
    private byte[] blockCompressed;

    /**
     * Number of bytes currently held in the <code>blockCompressed</code> byte array (may be less than the actual size of the array)
     */
    private int blockCompressedSize;

    /**
     * Reusable buffer holding the uncompressed data
     */
    private byte[] block;

    /**
     * Read position marker if the <code>block</code> byte array
     */
    private int blockOffset;

    /**
     * Number of bytes currently held in the <code>block</code> byte array (may be less than the actual size of the array)
     */
    private int blockSize;

    private final Inflater decompressor = new Inflater();

    private boolean isClosed = false;
    private boolean isEOF = false;

    public ZippedBlockInputStream(final InputStream is) {
        super(is);
        Args.notNull("is", is);
    }

    protected void assertIsOpen() throws IOException {
        if (isClosed())
            throw new IOException("Stream closed");
    }

    /**
     * Returns 0 after EOF has been reached, otherwise always return 1.
     * <p>
     * Programs should not count on this method to return the actual number
     * of bytes that could be read without blocking.
     *
     * @return 1 before EOF and 0 after EOF.
     */
    @Override
    public int available() throws IOException {
        assertIsOpen();

        return isEOF ? 0 : 1;
    }

    @Override
    public void close() throws IOException {
        if (!isClosed()) {
            isClosed = true;
            decompressor.end();
            block = null;
            blockCompressed = null;
            super.close();
        }
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isEOF() {
        return isEOF;
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public synchronized void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        assertIsOpen();

        if (blockOffset >= blockSize)
            try {
            readBlockAndDecompress();
        } catch (final EOFException ex) {
            isEOF = true;
            return IOUtils.EOF;
        }

        return block[blockOffset++];
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        Args.notNull("b", b);
        Args.inRange("off", off, 0, b.length - 1);
        Args.inRange("len", len, 0, b.length - off);

        if (len == 0)
            return 0;

        assertIsOpen();

        int bytesRead = 0;
        int writeOffset = off;
        while (bytesRead < len) {
            // end of current 'block' reached?
            if (blockOffset >= blockSize)
                try {
                // abort if reading would result in a blocking read operation on the underlying input stream and we could already read some data
                if (bytesRead > 0 && in.available() == 0)
                    return bytesRead;

                readBlockAndDecompress();
            } catch (final EOFException ex) {
                isEOF = true;
                return bytesRead == 0 ? IOUtils.EOF : bytesRead;
            }

            final int readSize = Math.min(blockSize - blockOffset, len - bytesRead);
            System.arraycopy(block, blockOffset, b, writeOffset, readSize);
            blockOffset += readSize;
            bytesRead += readSize;
            writeOffset += readSize;
        }

        return bytesRead;
    }

    protected void readBlockAndDecompress() throws IOException {
        // read the size of the compressed data
        blockCompressedSize = IOUtils.readInt(in);

        // read the size of the uncompressed data
        blockSize = IOUtils.readInt(in);

        // adjust the size of the 'blockCompressed' byte array if necessary
        if (blockCompressed == null || blockCompressedSize > blockCompressed.length)
            blockCompressed = new byte[blockCompressedSize];

        // adjust the size of the 'block' byte array if necessary
        if (block == null || blockSize > block.length)
            block = new byte[blockSize];

        // fill the 'blockCompressed' byte array
        IOUtils.readBytes(in, blockCompressed, 0, blockCompressedSize);

        // decompress the data
        try {
            decompressor.setInput(blockCompressed, 0, blockCompressedSize);
            decompressor.inflate(block);
            decompressor.reset();
        } catch (final DataFormatException ex) {
            throw new IOExceptionWithCause(ex);
        }

        blockOffset = 0;
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long skip(final long n) throws IOException {
        Args.min("n", n, 0);

        assertIsOpen();

        if (blockOffset >= blockSize)
            try {
            readBlockAndDecompress();
        } catch (final EOFException ex) {
            isEOF = true;
            return IOUtils.EOF;
        }

        final int skipMax = (int) n; // maximum number of bytes requested to be skipped
        final int skipable = blockSize - blockOffset; // maximum number of unread bytes in the current block buffer
        final int skipped = Math.min(skipable, skipMax);
        blockOffset += skipped;
        return skipped;

    }
}