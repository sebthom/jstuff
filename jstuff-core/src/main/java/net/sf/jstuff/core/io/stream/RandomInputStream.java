/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.InputStream;
import java.util.Random;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RandomInputStream extends InputStream {

   private int bytesRead = 0;
   private Random random;
   private byte @Nullable [] buff;
   private final int size;

   public RandomInputStream() {
      size = -1;
      random = new Random();
   }

   public RandomInputStream(final int size) {
      this(size, new Random());
   }

   public RandomInputStream(final int size, final Random random) {
      Args.min("size", size, 0);
      this.size = size;
      this.random = random;
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
      var buff = this.buff;
      if (buff == null || buff.length != 1) {
         buff = this.buff = new byte[1];
      }
      random.nextBytes(buff);
      return buff[0] & 0xFF;
   }

   @Override
   public int read(final byte[] out, final int off, final int len) {
      final int bytesReadable = Math.min(available(), len);
      if (bytesReadable < 1)
         return IOUtils.EOF;

      if (len == 0)
         return 0;

      var buff = this.buff;
      if (buff == null || buff.length != len) {
         buff = this.buff = new byte[len];
      }
      random.nextBytes(buff);
      System.arraycopy(buff, 0, out, off, len);
      bytesRead += bytesReadable;
      return bytesReadable;
   }
}
