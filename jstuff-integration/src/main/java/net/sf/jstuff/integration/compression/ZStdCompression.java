/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * https://gregoryszorc.com/blog/2017/03/07/better-compression-with-zstandard/
 * https://code.facebook.com/posts/1658392934479273/smaller-and-faster-data-compression-with-zstandard/
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZStdCompression extends AbstractCompression {

   public static final ZStdCompression INSTANCE = new ZStdCompression();

   public static final int LEVEL_SMALL_AS_DEFLATE_4 = 2;
   public static final int LEVEL_SMALL_AS_DEFLATE_6 = 5;
   public static final int LEVEL_SMALL_AS_DEFLATE_9 = 5;

   private boolean useChecksum = false;
   private int compressionLevel = 3;

   public ZStdCompression() {
   }

   public ZStdCompression(final int compressionLevel) {
      this.compressionLevel = compressionLevel;
   }

   public ZStdCompression(final int compressionLevel, final boolean useChecksum) {
      this.compressionLevel = compressionLevel;
      this.useChecksum = useChecksum;
   }

   @Override
   public byte[] compress(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      final long maxSize = Zstd.compressBound(uncompressed.length);
      if (maxSize > Integer.MAX_VALUE)
         throw new IOException("Max output size is greater than Integer.MAX_VALUE!");
      final byte[] dst = new byte[(int) maxSize];

      final long rc = Zstd.compress(dst, uncompressed, compressionLevel);
      if (Zstd.isError(rc))
         throw new IOException(Zstd.getErrorName(rc));

      final int size = (int) rc;
      final byte[] out = new byte[size];
      System.arraycopy(dst, 0, out, 0, size);
      return out;
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      if (!closeOutput) {
         // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
         output = new DelegatingOutputStream(output, true);
      }

      try {
         final OutputStream compOS = createCompressingOutputStream(output);
         compOS.write(uncompressed);
         compOS.flush();
      } finally {
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final InputStream input, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("input", input);
      Args.notNull("output", output);

      if (!closeOutput) {
         // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
         output = new DelegatingOutputStream(output, true);
      }

      try {
         final OutputStream compOS = createCompressingOutputStream(output);
         IOUtils.copyLarge(input, compOS);
         compOS.flush();
      } finally {
         IOUtils.closeQuietly(input);
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @Override
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      final ZstdOutputStream out = new ZstdOutputStream(output, compressionLevel);
      out.setCloseFrameOnFlush(true);
      out.setChecksum(useChecksum);
      return out;
   }

   @Override
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      return new ZstdInputStream(compressed);
   }

   @Override
   public int decompress(final byte[] compressed, final byte[] output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      final long rc = Zstd.decompress(output, compressed);
      if (Zstd.isError(rc)) {
         if (rc == -70)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
         throw new IOException(Zstd.getErrorName(rc));
      }
      return (int) rc;
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }

}
