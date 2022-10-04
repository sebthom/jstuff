/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static net.sf.jstuff.core.Strings.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import net.sf.jstuff.core.validation.Args;

/**
 * Extended BufferedOutputStream with write(String) and writeLine(String) methods.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BufferedOutputStreamExt extends BufferedOutputStream {

   private Charset charset = Charset.defaultCharset();

   /**
    * @param out the underlying output stream
    */
   public BufferedOutputStreamExt(final OutputStream out) {
      super(out);
   }

   /**
    * @param out the underlying output stream
    */
   public BufferedOutputStreamExt(final OutputStream out, final Charset charset) {
      super(out);
      Args.notNull("charset", charset);
      this.charset = charset;
   }

   /**
    * @param out the underlying output stream
    * @param size the buffer size
    * @throws IllegalArgumentException if size <= 0.
    */
   public BufferedOutputStreamExt(final OutputStream out, final int size) {
      super(out, size);
   }

   /**
    * @param out the underlying output stream
    * @param size the buffer size
    * @throws IllegalArgumentException if size <= 0.
    */
   public BufferedOutputStreamExt(final OutputStream out, final int size, final Charset charset) {
      super(out, size);
      Args.notNull("charset", charset);
      this.charset = charset;
   }

   /**
    * Writes a string.
    *
    * @param str The <code>CharSequence</code> to be written.
    * @throws IOException - if an I/O error occurs
    */
   public void write(final CharSequence str) throws IOException {
      write(str.toString().getBytes(charset));
   }

   /**
    * Terminate the current line by writing the line separator string. The
    * line separator string is defined by the system property
    * <code>line.separator</code>, and is not necessarily a single newline
    * character (<code>'\n'</code>).
    *
    * @throws IOException - if an I/O error occurs
    */
   public void writeLine() throws IOException {
      write(NEW_LINE.getBytes(charset));
   }

   /**
    * Writes a string and then terminates the line. This method behaves as
    * though it invokes <code>{@link #write(CharSequence)}</code> and then
    * <code>{@link #writeLine()}</code>.
    *
    * @param str The <code>CharSequence</code> to be written.
    * @throws IOException - if an I/O error occurs
    */
   public void writeLine(final CharSequence str) throws IOException {
      write(str);
      writeLine();
   }
}
