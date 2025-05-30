/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * A non-thread-safe output stream filter that performs on-the fly zip compression using a {@link Deflater}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockOutputStream extends FilterOutputStream {

   private static final Logger LOG = Logger.create();

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
   private int blockSize;

   private boolean isClosed;

   /**
    * @param os the underlying output stream to send the data in compressed form to
    * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying
    *           stream
    */
   public ZippedBlockOutputStream(final OutputStream os, final int blockSize) {
      this(os, blockSize, Deflater.DEFAULT_COMPRESSION);
   }

   /**
    * @param os the underlying output stream to send the data in compressed form to
    * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying
    *           stream
    */
   @SuppressWarnings("resource")
   public ZippedBlockOutputStream(final OutputStream os, final int blockSize, final @Nullable Deflater compressor) {
      super(os);

      Args.notNull("os", os);
      Args.min("blockSize", blockSize, 1);

      this.compressor = compressor == null ? new Deflater() : compressor;
      isUseDefaultCompressor = false;
      block = new byte[blockSize];
      blockCompressed = new byte[blockSize * 2]; // using larger buffer in case of negative compression ratio
   }

   /**
    * @param os the underlying output stream to send the data in compressed form to
    * @param blockSize the number of bytes that need to be written to the stream before the data is compressed and send to the underlying
    *           stream
    * @param compressionLevel the java.util.zip.Deflater compression level (0-9)
    */
   @SuppressWarnings("resource")
   public ZippedBlockOutputStream(final OutputStream os, final int blockSize, final int compressionLevel) {
      super(os);

      Args.notNull("os", os);
      Args.min("blockSize", blockSize, 1);
      Args.inRange("compressionLevel", compressionLevel, Deflater.DEFAULT_COMPRESSION, Deflater.BEST_COMPRESSION);

      compressor = new Deflater(compressionLevel);
      isUseDefaultCompressor = true;
      block = new byte[blockSize];
      blockCompressed = new byte[blockSize * 2]; // using larger buffer in case of negative compression ratio
   }

   protected void assertIsOpen() throws IOException {
      if (isClosed)
         throw new IOException("Stream closed");
   }

   @Override
   public void close() throws IOException {
      if (!isClosed) {
         flush();
         if (isUseDefaultCompressor) {
            compressor.end();
         }
         out.close();
         isClosed = true;
      }
   }

   /**
    * Compresses the data currently in the <code>block</code> byte array into the <code>blockCompressed</code>
    * byte array and writes it to the underlying output stream
    */
   protected void compressBlockAndWrite() throws IOException {
      // anything to do?
      if (blockSize > 0) {
         // compress the current input data
         compressor.setInput(block, 0, blockSize);
         compressor.finish();
         final int compressedSize = compressor.deflate(blockCompressed);
         if (LOG.isDebugEnabled()) {
            LOG.debug(block.length + " - " + blockSize + " / " + blockCompressed.length + " - " + compressedSize);
         }

         @SuppressWarnings("resource")
         final var out = asNonNullUnsafe(this.out);

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

   @Override
   public void flush() throws IOException {
      assertIsOpen();

      compressBlockAndWrite();
   }

   public Deflater getCompressor() {
      return compressor;
   }

   public boolean isClosed() {
      return isClosed;
   }

   @Override
   public void write(final byte[] b, final int off, final int len) throws IOException {
      Args.inRange("off", off, 0, b.length - 1);
      Args.inRange("len", len, 0, b.length - off);

      if (len == 0)
         return;

      assertIsOpen();

      int remainingSize = len;
      int remainingOffset = off;

      // process the b array in chunks if it's content does not fit into the block array
      while (blockSize + remainingSize > block.length) {
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
      if (blockSize == block.length) {
         compressBlockAndWrite();
      }
   }

   @Override
   public void write(final int b) throws IOException {
      assertIsOpen();

      // add the byte to the input data buffer
      block[blockSize] = (byte) b;
      blockSize++;

      // if the input data buffer is full compress
      if (blockSize == block.length) {
         compressBlockAndWrite();
      }
   }
}
