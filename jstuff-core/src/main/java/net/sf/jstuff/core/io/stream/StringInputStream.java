/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.nio.charset.Charset;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringInputStream extends CharSequenceInputStream {

   @SuppressWarnings("hiding")
   private final String input;
   private final int intputLen;

   public StringInputStream(final String input) {
      super(input);
      this.input = input;
      intputLen = input.length();
   }

   public StringInputStream(final String input, final int bufferSize) {
      super(input, bufferSize);
      this.input = input;
      intputLen = input.length();
   }

   public StringInputStream(final String input, final int bufferSize, final Charset charset) {
      super(input, bufferSize, charset);
      this.input = input;
      intputLen = input.length();
   }

   @Override
   protected boolean refillBuffer() {
      if (inputPos >= intputLen)
         return false;

      final int end = Math.min(inputPos + buf.length, intputLen);
      final byte[] chunk = input.substring(inputPos, end).getBytes(charset);
      bufSize = chunk.length;
      System.arraycopy(chunk, 0, buf, 0, bufSize);
      inputPos += chunk.length;
      bufPos = 0;
      return true;
   }

   @Override
   public String getInput() {
      return input;
   }
}
