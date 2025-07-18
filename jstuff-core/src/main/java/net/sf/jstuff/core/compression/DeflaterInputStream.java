/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * Creates an InputStream around the given InputStream that compresses the data on the fly.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DeflaterInputStream extends FilterInputStream {

   protected @Nullable Deflater compressor;
   private final byte[] bufUncompressed;
   private byte[] bufCompressed = new byte[1];

   private boolean isInternalCompressor;
   private boolean isSourceEOF;

   public DeflaterInputStream(final InputStream source) {
      this(source, new Deflater());
      isInternalCompressor = true;
   }

   public DeflaterInputStream(final InputStream source, final Deflater deflater) {
      this(source, deflater, 512);
   }

   @SuppressWarnings("resource")
   public DeflaterInputStream(final InputStream source, final Deflater deflater, final int bufSize) {
      super(source);

      Args.notNull("source", source);
      Args.notNull("deflater", deflater);
      Args.greaterThan("bufSize", bufSize, 0);

      compressor = deflater;
      bufUncompressed = new byte[bufSize];
   }

   @Override
   public int available() throws IOException {
      if (in == null)
         throw new IOException("Source InputStream is closed!");

      if (isSourceEOF)
         return 0;
      return 1;
   }

   @Override
   public void close() throws IOException {
      if (in == null)
         return;

      try {
         if (isInternalCompressor) {
            if (compressor != null) {
               compressor.end();
            }
            compressor = null;
         }

         in.close();
      } finally {
         in = null;
      }
   }

   @Override
   @SuppressWarnings("sync-override")
   public void mark(final int limit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int read() throws IOException {
      final int len = read(bufCompressed, 0, 1);
      if (len <= 0)
         return IOUtils.EOF;
      return bufCompressed[0] & 0xFF;
   }

   @Override
   public int read(final byte[] bufCompressed, int off, int bytesToRead) throws IOException {
      final var compressor = this.compressor;
      if (in == null || compressor == null)
         throw new IOException("Source InputStream is closed!");

      if (off < 0 || bytesToRead < 0 || bytesToRead > bufCompressed.length - off)
         throw new IndexOutOfBoundsException();

      if (bytesToRead == 0)
         return 0;

      int totalBytesRead = 0;
      while (bytesToRead > 0 && !compressor.finished()) {
         int bytesRead;

         if (compressor.needsInput()) {
            bytesRead = in.read(bufUncompressed, 0, bufUncompressed.length);
            if (bytesRead > 0) {
               compressor.setInput(bufUncompressed, 0, bytesRead);
            } else if (bytesRead <= IOUtils.EOF) {
               compressor.finish();
            }
         }

         bytesRead = compressor.deflate(bufCompressed, off, bytesToRead);
         totalBytesRead += bytesRead;
         off += bytesRead;
         bytesToRead -= bytesRead;
      }

      if (compressor.finished()) {
         isSourceEOF = true;
         if (totalBytesRead == 0) {
            totalBytesRead = IOUtils.EOF;
         }
      }

      return totalBytesRead;
   }

   @Override
   @SuppressWarnings("sync-override")
   public void reset() {
      throw new UnsupportedOperationException();
   }

   @Override
   public long skip(final long totalBytesToSkip) throws IOException {
      Args.notNegative("count", totalBytesToSkip);
      if (in == null)
         throw new IOException("Source InputStream is closed!");

      if (bufCompressed.length < bufUncompressed.length) {
         bufCompressed = new byte[bufUncompressed.length];
      }

      int bytesToSkip = totalBytesToSkip > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalBytesToSkip;
      long totalBytesSkipped = 0;

      while (bytesToSkip > 0) {
         final int bytesToRead = bytesToSkip > bufCompressed.length ? bufCompressed.length : bytesToSkip;
         final int bytesRead = read(bufCompressed, 0, bytesToRead);
         if (bytesRead <= IOUtils.EOF) {
            break;
         }
         totalBytesSkipped += bytesRead;
         bytesToSkip -= bytesRead;
      }
      return totalBytesSkipped;
   }
}
