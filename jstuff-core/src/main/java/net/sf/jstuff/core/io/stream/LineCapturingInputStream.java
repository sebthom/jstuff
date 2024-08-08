/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.sf.jstuff.core.Strings;

/**
 * @author Sebastian Thomschke
 */
public final class LineCapturingInputStream extends FilterInputStream {

   private final Consumer<String> lineListener;
   private final StringBuilder lineBuffer = new StringBuilder();
   private boolean lastCharWasCR = false;

   public LineCapturingInputStream(final InputStream in, final Consumer<String> lineListener) {
      super(in);
      this.lineListener = lineListener;
   }

   @Override
   public int read() throws IOException {
      final int b = super.read();
      if (b == -1) {
         handleEndOfStream();
         return -1;
      }
      processByte(b);
      return b;
   }

   @Override
   public int read(final byte[] b, final int off, final int len) throws IOException {
      final int bytesRead = super.read(b, off, len);
      if (bytesRead == -1) {
         handleEndOfStream();
         return -1;
      }
      for (int i = off; i < off + bytesRead; i++) {
         processByte(b[i]);
      }
      return bytesRead;
   }

   @Override
   public int read(final byte[] b) throws IOException {
      return read(b, 0, b.length);
   }

   private void processByte(final int b) {
      switch (b) {
         case Strings.CR:
            lastCharWasCR = true;
            break;
         case Strings.LF:
            handleEndOfLine();
            break;
         default:
            if (lastCharWasCR) {
               handleEndOfLine();
            }
            lineBuffer.append((char) b);
      }
   }

   private void handleEndOfLine() {
      final String line = lineBuffer.toString();
      lineBuffer.setLength(0);
      lineListener.accept(line);
      lastCharWasCR = false;
   }

   private void handleEndOfStream() {
      if (lineBuffer.length() > 0) {
         lineListener.accept(lineBuffer.toString());
         lineBuffer.setLength(0);
      }
   }
}
