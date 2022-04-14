/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.InputStream;
import java.util.Arrays;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DummyInputStream extends InputStream {

   private int bytesRead = 0;
   private byte dummyByte;
   private final int size;

   public DummyInputStream() {
      size = -1;
      dummyByte = 1;
   }

   public DummyInputStream(final int size) {
      this(size, (byte) 1);
   }

   public DummyInputStream(final int size, final byte dummyByte) {
      Args.min("size", size, 0);

      this.size = size;
      this.dummyByte = dummyByte;
   }

   @Override
   public int available() {
      if (size == -1)
         return Integer.MAX_VALUE;
      return size - bytesRead;
   }

   @Override
   public int read() {
      if (size > -1 && bytesRead >= size)
         return IOUtils.EOF;
      bytesRead++;
      return dummyByte & 0xFF;
   }

   @Override
   public int read(final byte[] out, final int off, final int len) {
      final int bytesReadable = Math.min(available(), len);
      if (bytesReadable < 1)
         return IOUtils.EOF;

      Arrays.fill(out, off, off + bytesReadable, dummyByte);
      bytesRead += bytesReadable;
      return bytesReadable;
   }
}
