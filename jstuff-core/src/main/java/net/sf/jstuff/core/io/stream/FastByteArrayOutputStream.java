/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link java.io.ByteArrayOutputStream}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayOutputStream extends OutputStream {

   protected byte[] data = ArrayUtils.EMPTY_BYTE_ARRAY;
   protected int count;
   protected int initialSize;

   public FastByteArrayOutputStream() {
      this(32);
   }

   public FastByteArrayOutputStream(final int initialSize) {
      Args.notNegative("initialSize", initialSize);
      this.initialSize = initialSize;
   }

   /**
    * Closing a {@link FastByteArrayOutputStream} has no effect.
    */
   @Override
   public void close() {
      // nothing to do
   }

   private void ensureCapacity(final int minCapacity) {
      final int dataSize = data.length;

      // resizing needed?
      if (minCapacity <= dataSize)
         return;

      // integer overflow?
      if (minCapacity < 0)
         throw new OutOfMemoryError("Cannot allocate array larger than " + Integer.MAX_VALUE);

      final int newCapacity = Math.max( //
         dataSize + (dataSize >> 2) /* == dataSize x 1.5 */, //
         minCapacity < initialSize ? initialSize : minCapacity //
      );
      final var copy = new byte[newCapacity];
      System.arraycopy(data, 0, copy, 0, count);
      data = copy;
   }

   /**
    * Flushing a {@link FastByteArrayOutputStream} has no effect.
    */
   @Override
   public void flush() {
      // nothing to do
   }

   public void reset() {
      count = 0;
   }

   public int size() {
      return count;
   }

   /**
    * @return a copy of the internal buffer
    */
   public byte[] toByteArray() {
      if (count == 0)
         return ArrayUtils.EMPTY_BYTE_ARRAY;
      final var copy = new byte[count];
      System.arraycopy(data, 0, copy, 0, count);
      return copy;
   }

   /**
    * This will reset the internal buffer.
    *
    * @return an {@link FastByteArrayInputStream} with this stream's internal buffer.
    */
   public InputStream toInputStream() {
      final var in = new FastByteArrayInputStream(data, 0, count);
      data = ArrayUtils.EMPTY_BYTE_ARRAY;
      count = 0;
      return in;
   }

   @Override
   public String toString() {
      return new String(data, 0, count, Charset.defaultCharset());
   }

   public String toString(final Charset charset) {
      return new String(data, 0, count, charset);
   }

   public String toString(final String charsetName) throws UnsupportedEncodingException {
      return new String(data, 0, count, charsetName);
   }

   @Override
   public void write(final byte[] b) {
      write(b, 0, b.length);
   }

   @Override
   public void write(final byte[] buf, final int offset, final int length) {
      if (offset < 0 || length < 0 || offset + length > buf.length)
         throw new IndexOutOfBoundsException();
      if (length == 0)
         return;

      final int newcount = count + length;

      ensureCapacity(newcount);
      System.arraycopy(buf, offset, data, count, length);
      count = newcount;
   }

   @Override
   public void write(final int b) {
      final int newcount = count + 1;
      ensureCapacity(newcount);
      data[count] = (byte) b;
      count = newcount;
   }

   /**
    * IndexOutOfBoundsException if copying would cause access of data outside array bounds.
    */
   public void writeTo(final byte[] out) {
      Args.notNull("out", out);

      System.arraycopy(data, 0, out, 0, count);
   }

   /**
    * IndexOutOfBoundsException if copying would cause access of data outside array bounds.
    */
   public void writeTo(final byte[] out, final int offset) {
      Args.notNull("out", out);

      System.arraycopy(data, 0, out, offset, count);
   }

   @SuppressWarnings("resource")
   public void writeTo(final OutputStream out) throws IOException {
      Args.notNull("out", out);

      out.write(data, 0, count);
   }
}
