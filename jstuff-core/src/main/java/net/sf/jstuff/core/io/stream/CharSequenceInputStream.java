/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Memory friendly input stream for character sequences.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceInputStream extends AbstractCharsInputStream {

   @FunctionalInterface
   public interface CharsSupplier {
      char charAt(int index) throws Exception;
   }

   private final CharsSupplier chars;
   private final IntSupplier charsLength;

   public CharSequenceInputStream(final String chars) {
      this(chars, Charset.defaultCharset());
   }

   public CharSequenceInputStream(final String chars, final Charset charset) {
      this(chars, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final String chars, final Charset charset, final int bufferSize) {
      this(chars::charAt, chars::length, charset, Math.min(bufferSize, chars.length() + 1 / CHAR_BUFFER_MULTIPLIER));
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final String chars, final int bufferSize) {
      this(chars, Charset.defaultCharset(), bufferSize);
   }

   public CharSequenceInputStream(final CharSequence chars) {
      this(chars, Charset.defaultCharset());
   }

   public CharSequenceInputStream(final CharSequence chars, final Charset charset) {
      this(chars, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final CharSequence chars, final Charset charset, final int bufferSize) {
      this(chars::charAt, chars::length, charset, bufferSize);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final CharSequence chars, final int bufferSize) {
      this(chars, Charset.defaultCharset(), bufferSize);
   }

   /**
    * @param chars function to access indexed chars.
    * @param charsLength function to get the number of indexed chars provided by the <code>chars</code> parameter.
    */
   public CharSequenceInputStream(final CharsSupplier chars, final IntSupplier charsLength) {
      this(chars, charsLength, Charset.defaultCharset());
   }

   /**
    * @param chars function to access indexed chars.
    * @param charsLength function to get the number of indexed chars provided by the <code>chars</code> parameter.
    */
   public CharSequenceInputStream(final CharsSupplier chars, final IntSupplier charsLength, final Charset charset) {
      this(chars, charsLength, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param chars function to access indexed chars.
    * @param charsLength function to get the number of indexed chars provided by the <code>chars</code> parameter.
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final CharsSupplier chars, final IntSupplier charsLength, final Charset charset, final int bufferSize) {
      super(charset, bufferSize);
      this.chars = chars;
      this.charsLength = charsLength;
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final CharsSupplier chars, final IntSupplier charsLength, final int bufferSize) {
      this(chars, charsLength, Charset.defaultCharset(), bufferSize);
   }

   public CharSequenceInputStream(final List<Character> chars) {
      this(chars, Charset.defaultCharset());
   }

   public CharSequenceInputStream(final List<Character> chars, final Charset charset) {
      this(chars, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final List<Character> chars, final Charset charset, final int bufferSize) {
      this(chars::get, chars::size, charset, bufferSize);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final List<Character> chars, final int bufferSize) {
      this(chars, Charset.defaultCharset(), bufferSize);
   }

   @Override
   public int available() {
      final int remaining = byteBuffer.remaining();
      return remaining == 0 ? charsLength.getAsInt() - charIndex : remaining;
   }

   @Override
   protected boolean refillBuffer() throws IOException {
      if (encoderState == EncoderState.DONE)
         return false;

      if (encoderState == EncoderState.FLUSHING)
         return flushEncoder();

      final int charsLen = charsLength.getAsInt();

      // if EOF is reached transition to flushing
      if (charIndex >= charsLen) {
         // finalize encoding before switching to flushing
         byteBuffer.clear();
         final CoderResult result = encoder.encode(CharBuffer.allocate(0), byteBuffer, true /* signal EOF */);
         byteBuffer.flip();
         if (result.isError()) {
            result.throwException();
         }
         return flushEncoder();
      }

      try {
         charBuffer.clear();
         for (int i = 0; i < bufferSize && charIndex < charsLen; i++) {
            final char nextChar = chars.charAt(charIndex++);
            if (Character.isHighSurrogate(nextChar)) { // handle surrogate pairs
               if (charIndex < charsLen) {
                  final char lowSurrogate = chars.charAt(charIndex);
                  if (Character.isLowSurrogate(lowSurrogate)) {
                     charIndex++;
                     charBuffer.put(nextChar);
                     charBuffer.put(lowSurrogate);
                  } else {
                     // missing low surrogate - fallback to replacement character
                     charBuffer.put('\uFFFD');
                  }
               } else {
                  // missing low surrogate - fallback to replacement character
                  charBuffer.put('\uFFFD');
                  break;
               }
            } else {
               charBuffer.put(nextChar);
            }
         }
         charBuffer.flip();

         // encode chars into bytes
         byteBuffer.clear();
         final CoderResult result = encoder.encode(charBuffer, byteBuffer, false);
         byteBuffer.flip();
         if (result.isError()) {
            result.throwException();
         }
      } catch (final Exception ex) {
         throw new IOException(ex);
      }

      return true;
   }
}
