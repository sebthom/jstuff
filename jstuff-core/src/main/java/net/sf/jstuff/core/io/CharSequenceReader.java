/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.IOException;
import java.io.Reader;

import net.sf.jstuff.core.validation.Args;

/**
 * Not thread-safe. (in contrast to {@link java.io.StringReader})
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReader extends Reader {
   private CharSequence text;
   private int next = 0;
   private int mark = 0;

   public CharSequenceReader(final CharSequence input) {
      text = input;
   }

   @Override
   public void close() {
      text = null;
   }

   private void ensureOpen() throws IOException {
      if (text == null)
         throw new IOException("Stream closed");
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
      ensureOpen();
      if (next >= text.length())
         return IOUtils.EOF;
      return text.charAt(next++);
   }

   @Override
   public int read(final char[] cbuf, final int off, final int len) throws IOException {
      ensureOpen();

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
      ensureOpen();
      if (next >= text.length())
         return 0;
      // Bound skip by beginning and end of the source
      long n = Math.min(text.length() - next, ns);
      n = Math.max(-next, n);
      next += n;
      return n;
   }

}
