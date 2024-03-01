/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.apache.commons.io.IOUtils.EOF;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LimitedInputStream extends FilterInputStream {
   private int bytesRemaining;
   private final boolean closeWrapped;
   private boolean isClosed;
   private int mark = EOF;

   /**
    * @param closeWrapped controls if the underlying {@link InputStream} should also be closed via {@link #close()}
    */
   public LimitedInputStream(final InputStream wrapped, final int maxBytesToRead, final boolean closeWrapped) {
      super(wrapped);
      Args.min("maxBytesToRead", maxBytesToRead, 0);
      bytesRemaining = maxBytesToRead;
      this.closeWrapped = closeWrapped;
   }

   @Override
   public int available() throws IOException {
      if (isClosed)
         return 0;
      final int availableBytes = in.available();
      return Math.min(availableBytes, bytesRemaining);
   }

   @Override
   public void close() throws IOException {
      if (closeWrapped) {
         in.close();
      }
      isClosed = true;
   }

   @Override
   public int read() throws IOException {
      if (isClosed || bytesRemaining < 1)
         return EOF;

      final int data = in.read();
      if (data != EOF) {
         bytesRemaining--;
      }
      return data;
   }

   @Override
   public int read(final byte[] b, final int off, final int len) throws IOException {
      if (isClosed || bytesRemaining < 1)
         return EOF;

      final int bytesRead = in.read(b, off, Math.min(len, bytesRemaining));
      if (bytesRead != EOF) {
         bytesRemaining -= bytesRead;
      }
      return bytesRead;
   }

   @Override
   public synchronized void mark(final int readlimit) {
      in.mark(readlimit);
      mark = bytesRemaining;
   }

   @Override
   public synchronized void reset() throws IOException {
      if (!in.markSupported())
         throw new IOException("mark/reset not supported");

      if (mark == EOF)
         throw new IOException("mark not set");

      in.reset();
      bytesRemaining = mark;
   }

   @Override
   public long skip(final long n) throws IOException {
      final long skipped = in.skip(Math.min(n, bytesRemaining));
      bytesRemaining -= skipped;
      return skipped;
   }
}
