/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.validation.Args;

/**
 * https://gregoryszorc.com/blog/2017/03/07/better-compression-with-zstandard/
 * https://code.facebook.com/posts/1658392934479273/smaller-and-faster-data-compression-with-zstandard/
 * https://quixdb.github.io/squash-benchmark/
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZStdCompression extends AbstractCompression {

   public static final int LEVEL_SMALL_AS_DEFLATE_4 = 2;
   public static final int LEVEL_SMALL_AS_DEFLATE_6 = 3;
   public static final int LEVEL_SMALL_AS_DEFLATE_9 = 3;

   public static final ZStdCompression INSTANCE = new ZStdCompression(LEVEL_SMALL_AS_DEFLATE_4, false);

   private final boolean useChecksum;
   private final int compressionLevel;

   public ZStdCompression(final int compressionLevel) {
      this(compressionLevel, false);
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
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      final ZstdOutputStream out = new ZstdOutputStream(output, compressionLevel);
      out.setCloseFrameOnFlush(true);
      out.setChecksum(useChecksum);
      return out;
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

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

   public int getCompressionLevel() {
      return compressionLevel;
   }

   public boolean isUseChecksum() {
      return useChecksum;
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }
}
