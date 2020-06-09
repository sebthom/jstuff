/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.Deflater;

import net.sf.jstuff.core.io.stream.ZippedBlockInputStream;
import net.sf.jstuff.core.io.stream.ZippedBlockOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockSocket extends Socket {
   private ZippedBlockInputStream in;
   private ZippedBlockOutputStream out;

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
      if (in == null) {
         in = new ZippedBlockInputStream(super.getInputStream());
      }
      return in;
   }

   @SuppressWarnings("resource")
   @Override
   public OutputStream getOutputStream() throws IOException {
      if (out == null) {
         out = new ZippedBlockOutputStream(super.getOutputStream(), 1024);
         out.getCompressor().setStrategy(Deflater.DEFAULT_STRATEGY);
         out.getCompressor().setLevel(Deflater.BEST_COMPRESSION);
      }
      return out;
   }
}
