/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;

import org.apache.commons.io.IOUtils;

import net.sf.jstuff.core.validation.Args;

/**
 * Memory friendly input stream for character sequences.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceInputStream extends InputStream {

   @FunctionalInterface
   public interface CharsSupplier {
      char charAt(int index);
   }

   private enum EncoderState {
      ENCODING,
      FLUSHING,
      DONE
   }

   /** 1024 surrogate character pairs */
   private static final int DEFAULT_BUFFER_SIZE = 1024;

   private final int bufferSize;
   private final CharBuffer charBuffer;
   private final ByteBuffer byteBuffer;
   private final CharsetEncoder encoder;
   private EncoderState encoderState = EncoderState.ENCODING;

   private ByteBuffer markedByteBuffer = lateNonNull();
   private int markedCharIndex = -1;

   private final CharsSupplier chars;
   private final IntSupplier charsLength;
   private int charIndex = 0;

   public CharSequenceInputStream(final char[] chars) {
      this(chars, Charset.defaultCharset());
   }

   public CharSequenceInputStream(final char[] chars, final Charset charset) {
      this(chars, charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final char[] chars, final Charset charset, final int bufferSize) {
      this(i -> chars[i], () -> chars.length, charset, bufferSize);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   public CharSequenceInputStream(final char[] chars, final int bufferSize) {
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
      Args.min("bufferSize", bufferSize, 1);
      encoder = charset.newEncoder();

      this.bufferSize = bufferSize;
      charBuffer = CharBuffer.allocate(bufferSize * 2); // buffer for 2 chars (high/low surrogate)
      byteBuffer = ByteBuffer.allocate(bufferSize * 4); // buffer for one UTF character (up to 4 bytes)
      this.chars = chars;
      this.charsLength = charsLength;
      byteBuffer.flip();
      charBuffer.flip();
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

   private boolean flushEncoder() throws IOException {
      if (encoderState == EncoderState.DONE)
         return false;

      if (encoderState == EncoderState.ENCODING) {
         encoderState = EncoderState.FLUSHING;
      }

      // flush
      byteBuffer.clear();
      final CoderResult result = encoder.flush(byteBuffer);
      byteBuffer.flip();

      if (result.isOverflow()) // byteBuffer too small
         return true;

      if (result.isError()) {
         result.throwException();
      }

      encoderState = EncoderState.DONE;
      return byteBuffer.hasRemaining();
   }

   public Charset getCharset() {
      return encoder.charset();
   }

   @Override
   public synchronized void mark(final int readlimit) {
      markedCharIndex = charIndex;
      markedByteBuffer = byteBuffer.duplicate();
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() throws IOException {
      if (!byteBuffer.hasRemaining() && !refillBuffer())
         return IOUtils.EOF;
      return byteBuffer.get() & 0xFF; // next byte as an unsigned integer (0 to 255)
   }

   @Override
   public int read(final byte[] buf, final int off, final int bytesToRead) throws IOException {
      Objects.checkFromIndexSize(off, bytesToRead, buf.length);
      if (bytesToRead == 0)
         return 0;

      int bytesRead = 0;
      int bytesReadable = byteBuffer.remaining();

      while (bytesRead < bytesToRead) {
         if (bytesReadable == 0) {
            if (refillBuffer()) {
               bytesReadable = byteBuffer.remaining();
            } else
               return bytesRead == 0 ? IOUtils.EOF : bytesRead;
         }

         final int bytesToReadNow = Math.min(bytesToRead - bytesRead, bytesReadable);
         byteBuffer.get(buf, off + bytesRead, bytesToReadNow);
         bytesRead += bytesToReadNow;
         bytesReadable -= bytesToReadNow;
      }

      return bytesRead;
   }

   private boolean refillBuffer() throws IOException {
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
      } catch (final RuntimeException ex) {
         throw new IOException(ex);
      }

      return true;
   }

   @Override
   public synchronized void reset() throws IOException {
      charBuffer.clear();
      byteBuffer.clear();
      if (markedCharIndex == -1) {
         charIndex = 0;
      } else {
         charIndex = markedCharIndex;
         byteBuffer.put(markedByteBuffer);
      }
      charBuffer.flip();
      byteBuffer.flip();

      encoderState = EncoderState.ENCODING;
      encoder.reset();
   }
}
