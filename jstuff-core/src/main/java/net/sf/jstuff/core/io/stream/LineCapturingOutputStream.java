/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public final class LineCapturingOutputStream extends FilterOutputStream {

   private final Consumer<String> lineListener;
   private final StringBuilder lineBuffer = new StringBuilder();
   private boolean lastCharWasCR = false;

   public LineCapturingOutputStream(final OutputStream out, final Consumer<String> lineListener) {
      super(out);
      this.lineListener = lineListener;
   }

   private void handleEndOfLine() {
      final String line = lineBuffer.toString();
      lineBuffer.setLength(0);
      lineListener.accept(line);
      lastCharWasCR = false;
   }

   @Override
   public void write(final int b) throws IOException {
      out.write(b);

      switch (b) {
         case Strings.CR:
            lastCharWasCR = true;
            break;
         case Strings.LF:
            handleEndOfLine();
            break;
         default:
            if (lastCharWasCR) { // if CR was not followed by LF treat it as line ending
               handleEndOfLine();
            }
            lineBuffer.append((char) b);
      }
   }

   @Override
   public void write(final byte[] bytes, final int off, final int len) throws IOException {
      if ((off | len | bytes.length - (len + off) | off + len) < 0)
         throw new IndexOutOfBoundsException();

      out.write(bytes, off, len);

      for (int i = 0; i < len; i++) {
         final byte b = bytes[off + i];
         switch (b) {
            case Strings.CR:
               lastCharWasCR = true;
               break;
            case Strings.LF:
               handleEndOfLine();
               break;
            default:
               if (lastCharWasCR) { // if CR was not followed by LF treat it as line ending
                  handleEndOfLine();
               }
               lineBuffer.append((char) b);
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (lineBuffer.length() > 0) {
         handleEndOfLine();
      }
      super.close();
   }
}
