/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Compress/decompress using "gzip" compression format.
 *
 * GZip is deflate plus CRC32 checksum
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GZipCompression extends AbstractCompression {

   public static final GZipCompression INSTANCE = new GZipCompression();

   /**
    * https://www.rootusers.com/gzip-vs-bzip2-vs-xz-performance-comparison/
    */
   private int compressionLevel = 4;

   public GZipCompression() {
   }

   public GZipCompression(final int compressionLevel) {
      this.compressionLevel = compressionLevel;
   }

   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      if (!closeOutput) {
         // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
         output = new DelegatingOutputStream(output, true);
      }
      try {
         final GZIPOutputStream compOS = (GZIPOutputStream) createCompressingOutputStream(output);
         compOS.write(uncompressed);
         compOS.finish();
      } finally {
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @SuppressWarnings("resource")
   public void compress(final InputStream uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      if (!closeOutput) {
         // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
         output = new DelegatingOutputStream(output, true);
      }

      try {
         final GZIPOutputStream compOS = (GZIPOutputStream) createCompressingOutputStream(output);
         IOUtils.copyLarge(uncompressed, compOS);
         compOS.finish();
      } finally {
         IOUtils.closeQuietly(uncompressed);
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @Override
   public InputStream createCompressingInputStream(final InputStream uncompressed) throws IOException {
      return new GZIPCompressingInputStream(uncompressed, compressionLevel);
   }

   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      return new GZIPOutputStream(output) {
         {
            def.setLevel(compressionLevel);
         }
      };
   }

   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      return new GZIPInputStream(compressed);
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }
}
