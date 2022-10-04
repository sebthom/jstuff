/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.apache.commons.io.IOUtils.*;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LinePrefixingTeeOutputStream extends OutputStream {

   private final OutputStream main;
   private final OutputStream branch;
   private byte[] prefix;
   private boolean isNewLine = true;

   public LinePrefixingTeeOutputStream(final OutputStream main, final OutputStream branch, final @Nullable String prefix) {
      this.main = main;
      this.branch = branch;
      this.prefix = prefix == null ? ArrayUtils.EMPTY_BYTE_ARRAY : prefix.getBytes();
   }

   @Override
   public void close() throws IOException {
      try {
         main.close();
      } finally {
         branch.close();
      }
   }

   @Override
   public void flush() throws IOException {
      main.flush();
      branch.flush();
   }

   private void onByteWritten(final byte byteWritten) throws IOException {
      if (isNewLine) {
         branch.write(prefix);
         isNewLine = false;
      }
      switch (byteWritten) {
         case EOF:
            return;
         case '\n':
            isNewLine = true;
            branch.write('\n');
            break;
         default:
            branch.write(byteWritten);
      }
   }

   @Override
   public void write(final byte[] b) throws IOException {
      write(b, 0, b.length);
   }

   @Override
   public void write(final byte[] b, final int off, final int len) throws IOException {
      main.write(b, off, len);
      for (int i = off, l = off + len; i < l; i++) {
         onByteWritten(b[i]);
      }
   }

   @Override
   public void write(final int b) throws IOException {
      main.write(b);
      onByteWritten((byte) b);
   }
}
