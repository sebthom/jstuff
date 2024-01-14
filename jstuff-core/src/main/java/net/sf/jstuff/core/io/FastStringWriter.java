/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized alternative to {@link StringWriter} backed by a {@link StringBuilder}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastStringWriter extends Writer {

   private final StringBuilder sb;

   public FastStringWriter() {
      sb = new StringBuilder();
   }

   public FastStringWriter(final int initialSize) {
      Args.notNegative("initialSize", initialSize);
      sb = new StringBuilder(initialSize);
   }

   @Override
   public FastStringWriter append(final char c) {
      write(c);
      return this;
   }

   @Override
   public FastStringWriter append(final @Nullable CharSequence csq) {
      write(String.valueOf(csq));
      return this;
   }

   @Override
   public FastStringWriter append(@Nullable CharSequence csq, final int start, final int end) {
      if (csq == null) {
         csq = "null";
      }
      return append(csq.subSequence(start, end));
   }

   @Override
   public void close() {
   }

   @Override
   public void flush() {
   }

   public StringBuilder getBuilder() {
      return sb;
   }

   /**
    * Return the builder's current value as a string.
    */
   @Override
   public String toString() {
      return sb.toString();
   }

   @Override
   public void write(final char[] cbuf, final int off, final int len) {
      if (off < 0 || len < 0 || off > cbuf.length || off + len > cbuf.length || off + len < 0)
         throw new IndexOutOfBoundsException();
      if (len == 0)
         return;
      sb.append(cbuf, off, len);
   }

   @Override
   public void write(final int c) {
      sb.append((char) c);
   }

   @Override
   public void write(final String str) {
      sb.append(str);
   }

   @Override
   public void write(final String str, final int off, final int len) {
      sb.append(str, off, off + len);
   }
}
