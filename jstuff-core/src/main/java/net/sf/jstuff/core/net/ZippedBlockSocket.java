/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.Deflater;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.stream.ZippedBlockInputStream;
import net.sf.jstuff.core.io.stream.ZippedBlockOutputStream;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockSocket extends Socket {
   private @Nullable ZippedBlockInputStream in;
   private @Nullable ZippedBlockOutputStream out;

   public ZippedBlockSocket() {
   }

   public ZippedBlockSocket(final String host, final int port) throws IOException {
      super(host, port);
   }

   @SuppressWarnings("resource")
   @Override
   public synchronized void close() throws IOException {
      getOutputStream().flush();
      super.close();
   }

   @SuppressWarnings("resource")
   @Override
   public InputStream getInputStream() throws IOException {
      var in = this.in;
      if (in == null) {
         in = this.in = new ZippedBlockInputStream(super.getInputStream());
      }
      return in;
   }

   @SuppressWarnings("resource")
   @Override
   public OutputStream getOutputStream() throws IOException {
      var out = this.out;
      if (out == null) {
         out = this.out = new ZippedBlockOutputStream(super.getOutputStream(), 1024);
         out.getCompressor().setStrategy(Deflater.DEFAULT_STRATEGY);
         out.getCompressor().setLevel(Deflater.BEST_COMPRESSION);
      }
      return out;
   }
}
