/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import net.sf.jstuff.core.functional.LongBiConsumer;
import net.sf.jstuff.core.validation.Args;

/**
 * Delegating ReadableByteChannel with read callback.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingReadableByteChannel implements ReadableByteChannel {

   private final ReadableByteChannel delegate;
   private final LongBiConsumer onBytesRead;
   private long totalBytesRead;

   public DelegatingReadableByteChannel(final ReadableByteChannel delegate) {
      this(delegate, null);
   }

   /**
    * @param onBytesRead LongBiConsumer#accept(long bytesRead, long totalBytesRead)
    */
   @SuppressWarnings("resource")
   public DelegatingReadableByteChannel(final ReadableByteChannel delegate, final LongBiConsumer onBytesRead) {
      Args.notNull("delegate", delegate);

      this.delegate = delegate;
      this.onBytesRead = onBytesRead == null ? (a, b) -> { /* ignore */ } : onBytesRead;
   }

   @Override
   public void close() throws IOException {
      delegate.close();
   }

   public ReadableByteChannel getDelegate() {
      return delegate;
   }

   public long getTotalBytesRead() {
      return totalBytesRead;
   }

   @Override
   public boolean isOpen() {
      return delegate.isOpen();
   }

   protected void onBytesRead(final int bytesRead) {
      totalBytesRead += bytesRead;
      onBytesRead.accept(bytesRead, totalBytesRead);
   }

   @Override
   public int read(final ByteBuffer bb) throws IOException {
      final int bytesRead = delegate.read(bb);
      if (bytesRead > 0) {
         onBytesRead(bytesRead);
      }
      return bytesRead;
   }
}
