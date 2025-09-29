/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("javadoc")
abstract class AbstractCharsInputStream extends InputStream {

   protected enum EncoderState {
      /**
       * The {@link #encoder} is actively encoding characters into bytes. This is the
       * initial state of the encoder.
       */
      ENCODING, //

      /**
       * The {@link #encoder} has finished processing all characters and is now
       * flushing any remaining bytes in its internal buffer.
       */
      FLUSHING, //

      /**
       * The {@link #encoder} has completed both the encoding and flushing processes.
       * No more data is left to be read from the encoder.
       */
      DONE
   }

   protected static final char UNICODE_REPLACEMENT_CHAR = '\uFFFD';

   /** 1024 surrogate character pairs */
   protected static final int DEFAULT_BUFFER_SIZE = 512;
   protected static final int CHAR_BUFFER_MULTIPLIER = 2; // 2 chars for one high/low surrogate character pair
   protected static final int BYTE_BUFFER_MULTIPLIER = 4; // 4 bytes for one UTF character (up to 4 bytes)

   protected final int bufferSize;
   protected final CharBuffer charBuffer;
   protected final ByteBuffer byteBuffer;
   protected final CharsetEncoder encoder;
   protected EncoderState encoderState = EncoderState.ENCODING;

   private ByteBuffer markedByteBuffer = lateNonNull();
   private int markedCharIndex = -1;

   protected int charIndex = 0;

   protected AbstractCharsInputStream(final Charset charset) {
      this(charset, DEFAULT_BUFFER_SIZE);
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   protected AbstractCharsInputStream(final Charset charset, final int bufferSize) {
      Args.min("bufferSize", bufferSize, 1);
      encoder = charset.newEncoder();

      this.bufferSize = bufferSize;
      charBuffer = CharBuffer.allocate(bufferSize * CHAR_BUFFER_MULTIPLIER);
      byteBuffer = ByteBuffer.allocate(bufferSize * BYTE_BUFFER_MULTIPLIER);
      byteBuffer.flip();
      charBuffer.flip();
   }

   /**
    * @param bufferSize number of surrogate character pairs to encode at once.
    */
   protected AbstractCharsInputStream(final int bufferSize) {
      this(StandardCharsets.UTF_8, bufferSize);
   }

   @Override
   public abstract int available();

   /**
    * This method is called by {@link #refillByteBuffer()} to encode characters
    * from the given {@link CharBuffer} into bytes and stores them in the
    * {@link #byteBuffer}.
    *
    * <p>
    * The method can be used either to encode characters in the middle of input
    * (with {@code isEndOfInput=false}) or to finalize the encoding process at the
    * end of input (with {@code isEndOfInput=true}).
    * </p>
    *
    * @param in
    *           the {@link CharBuffer} containing characters to encode.
    * @param isEndOfInput
    *           if {@code true}, signals that no more input will be provided,
    *           allowing the encoder to complete its final encoding steps.
    */
   protected void encodeChars(final CharBuffer in, final boolean isEndOfInput) throws CharacterCodingException {
      byteBuffer.clear();
      final CoderResult result = encoder.encode(in, byteBuffer, isEndOfInput);
      byteBuffer.flip();
      if (result.isError()) {
         result.throwException();
      }
   }

   protected boolean flushEncoder() throws IOException {
      if (encoderState == EncoderState.DONE)
         return false;

      if (encoderState == EncoderState.ENCODING) {
         encoderState = EncoderState.FLUSHING;
      }

      // flush
      byteBuffer.clear();
      final CoderResult result = encoder.flush(byteBuffer);
      byteBuffer.flip();

      if (result.isOverflow())
         // the byteBuffer has been filled, but there are more bytes to be flushed.
         // after reading all available bytes from byteBuffer, flushEncoder() needs to
         // be called again to process the remaining data.
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
      if (!byteBuffer.hasRemaining() && !refillByteBuffer())
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
            if (refillByteBuffer()) {
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

   /**
    * Refills the {@link #byteBuffer} by reading characters from the character
    * supplier, encoding them, and storing the resulting bytes into the
    * {@link #byteBuffer}.
    *
    * @return {@code true} if the buffer was successfully refilled and has bytes
    *         available for reading, {@code false} if the end of the stream is
    *         reached and there are no more bytes to read.
    */
   protected abstract boolean refillByteBuffer() throws IOException;

   @Override
   public synchronized void reset() throws IOException {
      if (!markSupported())
         throw new IOException("mark/reset not supported");

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
