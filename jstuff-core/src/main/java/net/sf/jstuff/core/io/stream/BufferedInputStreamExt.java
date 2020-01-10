/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.stream;

import static net.sf.jstuff.core.Strings.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jstuff.core.io.IOUtils;

/**
 * Extended BufferInputStream with readString and readLine methods.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BufferedInputStreamExt extends BufferedInputStream {
   /**
    * @param in the underlying input stream
    */
   public BufferedInputStreamExt(final InputStream in) {
      super(in);
   }

   /**
    * @param in the underlying input stream
    * @param size the buffer size
    * @throws IllegalArgumentException if size <= 0
    */
   public BufferedInputStreamExt(final InputStream in, final int size) {
      super(in, size);
   }

   /**
    * Reads a text line into a String object.
    * The line must be terminated with a CR/LF pair, or with a LF alone.
    * The terminator isn't included in the returned string.
    *
    * @throws IOException - if an I/O error occurs
    */
   public CharSequence readLine() throws IOException {
      final StringBuilder sb = new StringBuilder();

      while (true) {
         final int ch = read();
         switch (ch) {
            case LF:
               final int len = sb.length();
               if (len > 0 && sb.charAt(len - 1) == CR) {
                  sb.setLength(len - 1);
               }
               return sb;

            case IOUtils.EOF:
               if (sb.length() > 0)
                  return sb;
               return null;

            default:
               sb.append((char) ch);
         }
      }
   }

   /**
    * Reads text from the input stream of the given length into a String object.
    *
    * @param length number of characters to read
    * @throws IOException - if an I/O error occurs
    */
   public String readString(final int length) throws IOException {
      final byte[] chars = new byte[length];
      final int countBytes = read(chars);
      if (countBytes == IOUtils.EOF)
         return null;
      return new String(chars, 0, countBytes);
   }
}
