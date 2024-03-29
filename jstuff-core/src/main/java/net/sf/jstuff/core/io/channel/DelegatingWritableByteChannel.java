/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.functional.BiLongConsumer;
import net.sf.jstuff.core.validation.Args;

/**
 * Delegating WritableByteChannel with write callback.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingWritableByteChannel implements WritableByteChannel {

   private final WritableByteChannel delegate;
   private final BiLongConsumer onBytesWritten;
   private long totalBytesWritten;

   public DelegatingWritableByteChannel(final WritableByteChannel delegate) {
      this(delegate, null);
   }

   /**
    * @param onBytesWritten LongBiConsumer#accept(long bytesWritten, long totalBytesWritten)
    */
   @SuppressWarnings("resource")
   public DelegatingWritableByteChannel(final WritableByteChannel delegate, final @Nullable BiLongConsumer onBytesWritten) {
      Args.notNull("delegate", delegate);

      this.delegate = delegate;
      this.onBytesWritten = onBytesWritten == null ? (a, b) -> { /* ignore */ } : onBytesWritten;
   }

   @Override
   public void close() throws IOException {
      delegate.close();
   }

   public WritableByteChannel getDelegate() {
      return delegate;
   }

   public long getTotalBytesWritten() {
      return totalBytesWritten;
   }

   @Override
   public boolean isOpen() {
      return delegate.isOpen();
   }

   protected void onBytesWritten(final int bytesWritten) {
      totalBytesWritten += bytesWritten;
      onBytesWritten.accept(bytesWritten, totalBytesWritten);
   }

   @Override
   public int write(final ByteBuffer bb) throws IOException {
      final int bytesWritten = delegate.write(bb);
      if (bytesWritten > 0) {
         onBytesWritten(bytesWritten);
      }
      return bytesWritten;
   }
}
