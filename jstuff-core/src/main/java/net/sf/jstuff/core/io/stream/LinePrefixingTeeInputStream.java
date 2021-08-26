/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.apache.commons.io.IOUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class LinePrefixingTeeInputStream extends InputStream {

   private final InputStream input;
   private final OutputStream branch;
   private byte[] prefix;
   private boolean isNewLine = true;

   public LinePrefixingTeeInputStream(final InputStream input, final OutputStream branch, final String prefix) {
      this.input = input;
      this.branch = branch;
      this.prefix = prefix == null ? ArrayUtils.EMPTY_BYTE_ARRAY : prefix.getBytes();
   }

   @Override
   public void close() throws IOException {
      try {
         input.close();
      } finally {
         branch.close();
      }
   }

   private void onByteRead(final int byteRead) throws IOException {
      if (isNewLine) {
         branch.write(prefix);
         isNewLine = false;
      }
      switch (byteRead) {
         case EOF:
            return;
         case '\n':
            isNewLine = true;
            branch.write('\n');
            break;
         default:
            branch.write(byteRead);
      }
   }

   @Override
   public int read() throws IOException {
      final int byteRead = input.read();
      onByteRead(byteRead);
      return byteRead;
   }

   @Override
   public int read(final byte[] buffer, final int off, final int len) throws IOException {
      final int bytesRead = input.read(buffer, off, len);
      if (bytesRead != EOF) {
         for (int i = 0; i < bytesRead; i++) {
            final byte byteRead = buffer[off + i];
            onByteRead(byteRead);
         }
      }
      return bytesRead;
   }

   @Override
   public int read(final byte[] buffer) throws IOException {
      return read(buffer, 0, buffer.length);
   }
}
