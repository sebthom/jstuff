/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.lang3.ArrayUtils;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * A non-thread-safe input stream filter that performs on-the fly zip decompression using an {@link Inflater}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockInputStream extends FilterInputStream {
   /**
    * Reusable buffer for the compressed data read from the underlying input stream
    */
   private byte[] blockCompressed = ArrayUtils.EMPTY_BYTE_ARRAY;

   /**
    * Reusable buffer holding the uncompressed data
    */
   private byte[] block = ArrayUtils.EMPTY_BYTE_ARRAY;

   /**
    * Read position marker if the <code>block</code> byte array
    */
   private int blockOffset;

   /**
    * Number of bytes currently held in the <code>block</code> byte array (may be less than the actual size of the array)
    */
   private int blockSize;

   private final Inflater decompressor = new Inflater();

   private boolean isClosed;
   private boolean isEOF;

   private InputStream inputStream;

   @SuppressWarnings("resource")
   public ZippedBlockInputStream(final InputStream is) {
      super(is);
      Args.notNull("is", is);
      inputStream = is;
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
         block = ArrayUtils.EMPTY_BYTE_ARRAY;
         blockCompressed = ArrayUtils.EMPTY_BYTE_ARRAY;
         super.close();
      }
   }

   public boolean isClosed() {
      return isClosed;
   }

   public boolean isEOF() {
      return isEOF;
   }

   @Override
   public synchronized void mark(final int readlimit) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int read() throws IOException {
      assertIsOpen();

      if (blockOffset >= blockSize) {
         try {
            readBlockAndDecompress();
         } catch (final EOFException ex) {
            isEOF = true;
            return IOUtils.EOF;
         }
      }

      return block[blockOffset++] & 0xFF;
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
         if (blockOffset >= blockSize) {
            try {
               // abort if reading would result in a blocking read operation on the underlying input stream and we could already read some data
               if (bytesRead > 0 && inputStream.available() == 0)
                  return bytesRead;

               readBlockAndDecompress();
            } catch (final EOFException ex) {
               isEOF = true;
               return bytesRead == 0 ? IOUtils.EOF : bytesRead;
            }
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
      final int blockCompressedSize = IOUtils.readInt(inputStream);

      // read the size of the uncompressed data
      blockSize = IOUtils.readInt(inputStream);

      // adjust the size of the 'blockCompressed' byte array if necessary
      if (blockCompressedSize > blockCompressed.length) {
         blockCompressed = new byte[blockCompressedSize];
      }

      // adjust the size of the 'block' byte array if necessary
      if (blockSize > block.length) {
         block = new byte[blockSize];
      }

      // fill the 'blockCompressed' byte array
      IOUtils.readBytes(inputStream, blockCompressed, 0, blockCompressedSize);

      // decompress the data
      try {
         decompressor.setInput(blockCompressed, 0, blockCompressedSize);
         decompressor.inflate(block);
         decompressor.reset();
      } catch (final DataFormatException ex) {
         throw new IOException(ex);
      }

      blockOffset = 0;
   }

   @Override
   public synchronized void reset() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public long skip(final long n) throws IOException {
      Args.min("n", n, 0);

      assertIsOpen();

      if (blockOffset >= blockSize) {
         try {
            readBlockAndDecompress();
         } catch (final EOFException ex) {
            isEOF = true;
            return IOUtils.EOF;
         }
      }

      final int skipMax = (int) n; // maximum number of bytes requested to be skipped
      final int skipable = blockSize - blockOffset; // maximum number of unread bytes in the current block buffer
      final int skipped = Math.min(skipable, skipMax);
      blockOffset += skipped;
      return skipped;

   }
}
