/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteBufferInputStream extends InputStream {

   private final ByteBuffer buf;

   public ByteBufferInputStream(final ByteBuffer buf) {
      this.buf = buf;
   }

   @Override
   public int read() throws IOException {
      if (!buf.hasRemaining())
         return -1;
      return buf.get() & 0xFF;
   }

   @Override
   public int read(final byte[] bytes, final int off, int len) throws IOException {
      if (!buf.hasRemaining())
         return -1;

      len = Math.min(len, buf.remaining());
      buf.get(bytes, off, len);
      return len;
   }
}
