/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceInputStream extends InputStream {

   private final CharSequence input;
   protected int inputPos = 0;
   protected final Charset charset;

   protected final byte[] buf;
   protected int bufPos = 0;
   protected int bufSize = 0;

   private int mark = -1;

   public CharSequenceInputStream(final CharSequence input) {
      this(input, Math.min(8196, input.length()), StandardCharsets.UTF_8);
   }

   public CharSequenceInputStream(final CharSequence input, final int bufferSize) {
      this(input, bufferSize, StandardCharsets.UTF_8);
   }

   public CharSequenceInputStream(final CharSequence input, final int bufferSize, final Charset charset) {
      this.input = input;
      buf = new byte[bufferSize];
      this.charset = charset;
   }

   @Override
   public int available() throws IOException {
      return bufSize - bufPos + input.length() - inputPos;
   }

   public Charset getCharset() {
      return charset;
   }

   public CharSequence getInput() {
      return input;
   }

   @Override
   public void mark(final int readAheadLimit) {
      mark = inputPos - (bufSize - bufPos);
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() throws IOException {
      if (bufPos >= bufSize && !refillBuffer())
         return IOUtils.EOF;
      return buf[bufPos++] & 0xff;
   }

   @Override
   public int read(final byte[] b, int off, int len) throws IOException {
      if (off < 0 || len < 0 || len > b.length - off)
         throw new IndexOutOfBoundsException();
      else if (len == 0)
         return 0;

      int bytesRead = 0;
      while (len > 0) {
         if (bufPos >= bufSize && !refillBuffer())
            return bytesRead > 0 ? bytesRead : IOUtils.EOF;
         final int bytesToCopy = Math.min(bufSize - bufPos, len);
         System.arraycopy(buf, bufPos, b, off, bytesToCopy);
         bufPos += bytesToCopy;
         off += bytesToCopy;
         len -= bytesToCopy;
         bytesRead += bytesToCopy;
      }
      return bytesRead;
   }

   protected boolean refillBuffer() {
      final var intputLen = input.length();
      if (inputPos >= intputLen)
         return false;

      final int end = Math.min(inputPos + buf.length, intputLen);
      final byte[] chunk = input.subSequence(inputPos, end).toString().getBytes(charset);
      bufSize = chunk.length;
      System.arraycopy(chunk, 0, buf, 0, bufSize);
      inputPos += chunk.length;
      bufPos = 0;
      return true;
   }

   @Override
   public void reset() throws IOException {
      if (mark < 0)
         throw new IOException("Mark has not been set");
      inputPos = mark;
      bufPos = bufSize; // Force buffer refill on next read
   }

   @Override
   public long skip(long n) throws IOException {
      if (n <= 0)
         return 0;

      long skipped = 0;
      while (n > 0) {
         if (bufPos >= bufSize && !refillBuffer()) {
            break; // End of stream
         }
         final long bytesToSkip = Math.min(bufSize - bufPos, n);
         bufPos += bytesToSkip;
         n -= bytesToSkip;
         skipped += bytesToSkip;
      }
      return skipped;
   }
}
