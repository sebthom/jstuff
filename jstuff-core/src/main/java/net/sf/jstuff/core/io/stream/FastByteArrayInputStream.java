/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.stream;

import java.io.InputStream;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link java.io.ByteArrayInputStream}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayInputStream extends InputStream {
   protected byte[] data;
   protected int pos;
   protected int count;
   protected int mark;

   public FastByteArrayInputStream(final byte[] data) {
      this(data, 0, data.length);
   }

   public FastByteArrayInputStream(final byte[] data, final int offset, final int length) {
      Args.notNull("data", data);
      Args.notNegative("offset", offset);
      Args.notNegative("length", length);

      this.data = data;
      pos = offset;
      count = Math.min(offset + length, data.length);
      mark = offset;
   }

   @Override
   public int available() {
      return count - pos;
   }

   /**
    * Closing a {@link FastByteArrayInputStream} has no effect.
    */
   @Override
   public void close() {
      // nothing to do
   }

   @Override
   public void mark(final int readLimit) {
      mark = pos;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() {
      return pos < count ? data[pos++] & 0xff : IOUtils.EOF;
   }

   @Override
   public int read(final byte[] buf, final int offset, int length) {
      if (offset < 0 || length < 0 || offset + length > buf.length)
         throw new IndexOutOfBoundsException();

      if (pos >= count)
         return IOUtils.EOF;

      final int avail = count - pos;
      if (length > avail) {
         length = avail;
      }

      if (length <= 0)
         return 0;

      System.arraycopy(data, pos, buf, offset, length);
      pos += length;
      return length;
   }

   @Override
   public void reset() {
      pos = mark;
   }

   public void setData(final byte[] data) {
      Args.notNull("data", data);

      this.data = data;
      pos = 0;
      count = data.length;
      mark = 0;
   }

   public void setData(final byte[] data, final int offset, final int length) {
      Args.notNull("data", data);
      Args.notNegative("offset", offset);
      Args.notNegative("length", length);

      this.data = data;
      pos = offset;
      count = Math.min(offset + length, data.length);
      mark = offset;
   }

   @Override
   public long skip(final long n) {
      if (n < 0)
         return 0;

      final long avail = count - pos;
      final long skipped = n < avail ? n : avail;

      pos += skipped;
      return skipped;
   }
}
