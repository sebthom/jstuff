/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharArrayInputStream extends AbstractCharsInputStream {

   private final char[] chars;

   public CharArrayInputStream(final char[] chars) {
      this(chars, Charset.defaultCharset());
   }

   public CharArrayInputStream(final char[] chars, final Charset charset) {
      this(chars, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharArrayInputStream(final char[] chars, final Charset charset, final int bufferSize) {
      super(charset, Math.min(bufferSize, chars.length + 1 / CHAR_BUFFER_MULTIPLIER));
      this.chars = chars;
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharArrayInputStream(final char[] chars, final int bufferSize) {
      this(chars, Charset.defaultCharset(), bufferSize);
   }

   @Override
   public int available() {
      final int remaining = byteBuffer.remaining();
      return remaining == 0 ? chars.length - charIndex : remaining;
   }

   @Override
   protected boolean refillByteBuffer() throws IOException {
      if (encoderState == EncoderState.DONE)
         return false;

      if (encoderState == EncoderState.FLUSHING)
         return flushEncoder();

      final int charsLen = chars.length;

      // if EOF is reached transition to flushing
      if (charIndex >= charsLen) {
         // finalize encoding before switching to flushing
         encodeChars(CharBuffer.allocate(0), true /* signal EOF */);
         return flushEncoder();
      }

      try {
         charBuffer.clear();
         for (int i = 0; i < bufferSize && charIndex < charsLen; i++) {
            final char nextChar = chars[charIndex++];
            if (Character.isHighSurrogate(nextChar)) { // handle surrogate pairs
               if (charIndex < charsLen) {
                  final char lowSurrogate = chars[charIndex];
                  if (Character.isLowSurrogate(lowSurrogate)) {
                     charIndex++;
                     charBuffer.put(nextChar);
                     charBuffer.put(lowSurrogate);
                  } else {
                     // missing low surrogate - fallback to replacement character
                     charBuffer.put(UNICODE_REPLACEMENT_CHAR);
                  }
               } else {
                  // missing low surrogate - fallback to replacement character
                  charBuffer.put(UNICODE_REPLACEMENT_CHAR);
                  break;
               }
            } else {
               charBuffer.put(nextChar);
            }
         }
         charBuffer.flip();

         // encode chars into bytes
         encodeChars(charBuffer, false);
      } catch (final RuntimeException ex) {
         throw new IOException(ex);
      }

      return true;
   }
}
