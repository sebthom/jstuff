/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

/**
 * An {@link OutputStream} that transforms each line of text using a provided transformer function before writing it to the underlying
 * stream.
 *
 * <p>
 * Example usage:
 * </p>
 * <pre>{@code
 * OutputStream originalOut = ...;
 * Function<String, String> transformer = String::toUpperCase;
 * try (OutputStream transformingOut = new LineTransformingOutputStream(originalOut, transformer)) {
 *     transformingOut.write("Hello\nWorld".getBytes(StandardCharsets.UTF_8));
 * }
 * }</pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LineTransformingOutputStream extends FilterOutputStream {

   protected final Charset charset;
   protected final FastByteArrayOutputStream lineBuffer = new FastByteArrayOutputStream();
   protected final Function<String, String> lineTransformer;
   protected boolean previousWasCR = false;

   /**
    * Constructs a new {@code LineTransformingOutputStream} with UTF-8 encoding.
    *
    * @param out the underlying output stream
    * @param lineTransformer the function to transform each line
    */
   public LineTransformingOutputStream(final OutputStream out, final Function<String, String> lineTransformer) {
      this(out, lineTransformer, StandardCharsets.UTF_8);
   }

   /**
    * Constructs a new {@code LineTransformingOutputStream} with the specified charset.
    *
    * @param out the underlying output stream
    * @param lineTransformer the function to transform each line
    * @param charset the charset used for encoding and decoding
    */
   public LineTransformingOutputStream(final OutputStream out, final Function<String, String> lineTransformer, final Charset charset) {
      super(out);
      this.lineTransformer = lineTransformer;
      this.charset = charset;
   }

   @Override
   public void flush() throws IOException {
      if (lineBuffer.size() > 0) {
         writeTransformedLine();
      }
      super.flush();
   }

   @Override
   public void write(final byte[] buff, final int off, final int len) throws IOException {
      Objects.checkFromIndexSize(off, len, buff.length);

      int lineStart = off;
      final int end = off + len;

      for (int i = off; i < end; i++) {
         final byte currentByte = buff[i];
         if (currentByte == '\n') {
            final int lineEnd = i + 1; // include the '\n'
            lineBuffer.write(buff, lineStart, lineEnd - lineStart);
            lineStart = lineEnd;
            writeTransformedLine();
         } else {
            if (previousWasCR) {
               final int lineEnd = i;
               lineBuffer.write(buff, lineStart, lineEnd - lineStart);
               lineStart = lineEnd;
               writeTransformedLine();
            }
            if (currentByte == '\r') {
               final int lineEnd = i + 1; // include the '\r'
               lineBuffer.write(buff, lineStart, lineEnd - lineStart);
               lineStart = lineEnd;
               previousWasCR = true;
            }
         }
      }

      // write remaining bytes
      if (lineStart < end) {
         lineBuffer.write(buff, lineStart, end - lineStart);
      }
   }

   @Override
   public void write(final int b) throws IOException {
      if (previousWasCR) {
         if (b == '\n') { // detected \r\n sequence
            lineBuffer.write(b);
            writeTransformedLine();
            return;
         }

         // \r was a standalone line terminator
         writeTransformedLine();
      }

      if (b == '\r') {
         previousWasCR = true;
         return;
      }

      lineBuffer.write(b);
      if (b == '\n') {
         writeTransformedLine();
      }
   }

   protected void writeTransformedLine() throws IOException {
      final String originalLine = lineBuffer.toString(charset);
      final String transformedLine = lineTransformer.apply(originalLine);
      out.write(transformedLine.getBytes(charset));
      lineBuffer.reset();
      previousWasCR = false;
   }
}
