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
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOExceptionWithCause;
import org.apache.commons.io.IOUtils;
import org.meteogroup.jbrotli.BrotliDeCompressor;
import org.meteogroup.jbrotli.BrotliException;
import org.meteogroup.jbrotli.io.BrotliInputStream;
import org.meteogroup.jbrotli.io.BrotliOutputStream;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Or: https://github.com/google/brotli/blob/master/java/org/brotli/wrapper/enc/BrotliOutputStream.java
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BrotliCompression extends AbstractCompression {

   static {
      BrotliLibraryLoader.loadBrotli();
   }

   public static final BrotliCompression INSTANCE = new BrotliCompression();

   private final BrotliDeCompressor decompressor;

   public BrotliCompression() {
      decompressor = new BrotliDeCompressor();
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
         final OutputStream compOS = createCompressingOutputStream(output);
         compOS.write(uncompressed);
         compOS.flush();
      } finally {
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

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

   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      return new BrotliOutputStream(output);
   }

   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      return new BrotliInputStream(compressed);
   }

   @Override
   public int decompress(final byte[] compressed, final byte[] output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try {
         return decompressor.deCompress(compressed, output);
      } catch (final BrotliException ex) {
         if (ex.getMessage().contains("Error code: -14"))
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
         throw new IOExceptionWithCause(ex);
      }
   }

   @Override
   public String toString() {
      return Strings.toString(this);
   }

}