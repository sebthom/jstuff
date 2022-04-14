/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * Compress/decompress using "gzip" compression format.
 *
 * GZip is deflate plus CRC32 checksum
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class GZipCompression extends AbstractCompression {

   /**
    * Shared instance with compression level 4.
    *
    * See https://www.rootusers.com/gzip-vs-bzip2-vs-xz-performance-comparison/#attachment_1478
    */
   public static final GZipCompression INSTANCE = new GZipCompression(4);

   private final int compressionLevel;

   public GZipCompression(final int compressionLevel) {
      this.compressionLevel = compressionLevel;
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createCompressingInputStream(final InputStream uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      return new GZIPCompressingInputStream(uncompressed, compressionLevel);
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new GZIPOutputStream(output) {
         {
            def.setLevel(compressionLevel);
         }
      };
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new GZIPInputStream(compressed);
   }

   public int getCompressionLevel() {
      return compressionLevel;
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }
}
