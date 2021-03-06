/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.InputStream;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.types.Composite;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeInputStream extends InputStream implements Composite<InputStream> {

   private static final Logger LOG = Logger.create();

   private final InputStream[] streams;
   private InputStream currentStream;
   private int currentStreamIdx;

   public CompositeInputStream(final InputStream... streams) {
      Args.notNull("streams", streams);
      Args.notEmpty("streams", streams);
      Args.noNulls("streams", streams);

      this.streams = streams;
      currentStreamIdx = 0;
      currentStream = streams[0];
   }

   @Override
   public void addComponent(final InputStream stream) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int available() throws IOException {
      return currentStream.available();
   }

   @Override
   public void close() throws IOException {
      IOException ex = null;
      for (final InputStream stream : streams) {
         try {
            stream.close();
         } catch (final IOException iox) {
            if (ex == null) {
               ex = new IOException("Failed to close InputStream with 0-based-index: " + currentStreamIdx, iox);
            } else {
               LOG.error(iox, "Failed to close InputStream with 0-based-index: %s", currentStreamIdx);
            }
         }
      }
      if (ex != null)
         throw ex;
   }

   @Override
   public boolean hasComponent(final InputStream stream) {
      return ArrayUtils.contains(streams, stream);
   }

   @Override
   public boolean isModifiable() {
      return false;
   }

   @Override
   public void mark(final int readlimit) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int read() throws IOException {
      do {
         final int b = currentStream.read();
         if (b > -1)
            return b;
      }
      while (useNextStream());
      return -1;
   }

   @Override
   public int read(final byte[] buffer) throws IOException {
      return read(buffer, 0, buffer.length);
   }

   @Override
   public int read(final byte[] buffer, final int byteOffset, final int byteCount) throws IOException {
      do {
         final int read = currentStream.read(buffer, byteOffset, byteCount);
         if (read > -1)
            return read;
      }
      while (useNextStream());
      return -1;
   }

   @Override
   public boolean removeComponent(final InputStream stream) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void reset() throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   public long skip(long bytesToSkip) throws IOException {
      long bytesSkipped = 0;
      final byte[] buff = new byte[bytesToSkip > 4096 ? 4096 : (int) bytesToSkip];
      do {
         final int bytes2Read = bytesToSkip > buff.length ? buff.length : (int) bytesToSkip;
         final int read = read(buff, 0, bytes2Read);
         if (read == -1) {
            break;
         }
         bytesToSkip -= read;
         bytesSkipped += read;
      }
      while (bytesToSkip > 0);
      return bytesSkipped;
   }

   private boolean useNextStream() {
      if (currentStreamIdx + 1 < streams.length) {
         currentStreamIdx++;
         currentStream = streams[currentStreamIdx];
         return true;
      }
      return false;

   }
}
