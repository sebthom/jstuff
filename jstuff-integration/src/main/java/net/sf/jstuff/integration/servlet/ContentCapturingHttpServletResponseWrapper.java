/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * Use {{@link #toString()} to get the response as string.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ContentCapturingHttpServletResponseWrapper extends StatusCapturingHttpServletResponseWrapper {
   private ServletOutputStream exposedOutputStream;
   private PrintWriter exposedPrintWriter;
   private final FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();

   public ContentCapturingHttpServletResponseWrapper(final HttpServletResponse response) {
      super(response);
   }

   public void clear() {
      outputStream.reset();
   }

   public byte[] toByteArray() {
      if (exposedPrintWriter != null) {
         exposedPrintWriter.flush();
      }

      return outputStream.toByteArray();
   }

   @SuppressWarnings("resource")
   public void copyTo(final OutputStream target) throws IOException {
      Args.notNull("target", target);

      IOUtils.copy(outputStream.toInputStream(), target);
   }

   @Override
   public String toString() {
      try {
         final String encoding = getCharacterEncoding();
         return outputStream.toString(encoding);
      } catch (final UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   @Override
   @SuppressWarnings("resource")
   public ServletOutputStream getOutputStream() {
      Assert.isNull(exposedPrintWriter, "getWriter() was called already!");

      if (exposedOutputStream == null) {
         exposedOutputStream = new ServletOutputStream() {
            @Override
            public void write(final byte[] b) throws IOException {
               outputStream.write(b);
            }

            @Override
            public void write(final byte[] b, final int off, final int len) throws IOException {
               outputStream.write(b, off, len);
            }

            @Override
            public void write(final int b) throws IOException {
               outputStream.write(b);
            }

            @Override
            public boolean isReady() {
               throw new UnsupportedOperationException();
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
               throw new UnsupportedOperationException();
            }
         };
      }
      return exposedOutputStream;
   }

   @Override
   @SuppressWarnings("resource")
   public PrintWriter getWriter() {
      Assert.isNull(exposedOutputStream, "getOutpuStream() was called already!");

      if (exposedPrintWriter == null) {

         exposedPrintWriter = new PrintWriter(new Writer() {

            @Override
            public void write(final String str) throws IOException {
               outputStream.write(str.getBytes(getCharacterEncoding()));
            }

            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
               // outputStream.write(new String(cbuf, off, len).getBytes(getCharacterEncoding()));
               final Charset charset = Charset.forName(getCharacterEncoding());
               outputStream.write(ArrayUtils.toByteArray(cbuf, off, len, charset));
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
         }) {
            @Override
            public void write(final String str) {
               try {
                  outputStream.write(str.getBytes(getCharacterEncoding()));
               } catch (final UnsupportedEncodingException ex) {
                  throw new RuntimeException(ex);
               }
            }
         };

         /* 3x slower:
         try {
            exposedPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, getCharacterEncoding())), false);
            // or
            exposedPrintWriter = new PrintWriter(outputStream);
         } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
         } */
      }

      return exposedPrintWriter;
   }
}
