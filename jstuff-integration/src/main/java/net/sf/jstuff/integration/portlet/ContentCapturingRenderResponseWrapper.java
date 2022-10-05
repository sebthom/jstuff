/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.portlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.portlet.RenderResponse;
import javax.portlet.filter.RenderResponseWrapper;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * Use {{@link #toString()} to get the response as string.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ContentCapturingRenderResponseWrapper extends RenderResponseWrapper {
   private @Nullable PrintWriter exposedPrintWriter;
   private final FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();

   public ContentCapturingRenderResponseWrapper(final RenderResponse response) {
      super(response);
   }

   public void clear() {
      outputStream.reset();
   }

   public byte[] toByteArray() {
      return outputStream.toByteArray();
   }

   @Override
   public String toString() {
      if (exposedPrintWriter != null) {
         exposedPrintWriter.flush();
      }

      try {
         final String encoding = getCharacterEncoding();
         return outputStream.toString(encoding);
      } catch (final UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   @Override
   public OutputStream getPortletOutputStream() {
      if (exposedPrintWriter != null)
         throw new IllegalStateException("getWriter() was called already!");

      return outputStream;
   }

   @Override
   public PrintWriter getWriter() {
      var exposedPrintWriter = this.exposedPrintWriter;
      if (exposedPrintWriter == null) {
         exposedPrintWriter = this.exposedPrintWriter = new PrintWriter(new Writer() {
            @Override
            public void write(final String str) throws IOException {
               outputStream.write(str.getBytes(getCharacterEncoding()));
            }

            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
               outputStream.write(new String(cbuf, off, len).getBytes(getCharacterEncoding()));
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
         } catch (final UnsupportedEncodingException ex) {
             throw new RuntimeException(ex);
         } */
      }
      return exposedPrintWriter;
   }

}
