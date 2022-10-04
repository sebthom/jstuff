/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * Not thread-safe. (in contrast to {@link java.io.StringReader})
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReader extends Reader {
   @Nullable
   private CharSequence text;
   private int next;
   private int mark;

   public CharSequenceReader(final CharSequence input) {
      text = input;
   }

   @Override
   public void close() {
      text = null;
   }

   private CharSequence ensureOpen() throws IOException {
      final var text = this.text;
      if (text == null)
         throw new IOException("Stream closed");
      return text;
   }

   @Override
   public void mark(final int readAheadLimit) throws IOException {
      Args.notNegative("readAheadLimit", readAheadLimit);
      ensureOpen();
      mark = next;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() throws IOException {
      final var text = ensureOpen();
      if (next >= text.length())
         return IOUtils.EOF;
      return text.charAt(next++);
   }

   @Override
   public int read(final char[] cbuf, final int off, final int len) throws IOException {
      final var text = ensureOpen();

      if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0)
         throw new IndexOutOfBoundsException();
      else if (len == 0)
         return 0;

      if (next >= text.length())
         return IOUtils.EOF;

      final int n = Math.min(text.length() - next, len);

      if (text instanceof String) {
         ((String) text).getChars(next, next + n, cbuf, off);
      } else if (text instanceof StringBuilder) {
         ((StringBuilder) text).getChars(next, next + n, cbuf, off);
      } else if (text instanceof StringBuffer) {
         ((StringBuffer) text).getChars(next, next + n, cbuf, off);
      } else {
         for (int i = next, l = next + n; i < l; i++) {
            cbuf[off + i] = text.charAt(i);
         }
      }
      next += n;
      return n;
   }

   @Override
   public boolean ready() throws IOException {
      ensureOpen();
      return true;
   }

   @Override
   public void reset() throws IOException {
      ensureOpen();
      next = mark;
   }

   @Override
   public long skip(final long ns) throws IOException {
      final var text = ensureOpen();

      if (next >= text.length())
         return 0;
      // Bound skip by beginning and end of the source
      long n = Math.min(text.length() - next, ns);
      n = Math.max(-next, n);
      next += n;
      return n;
   }

}
