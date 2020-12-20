/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractCompression implements Compression {

   private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

   @Override
   @SuppressWarnings("resource")
   public byte[] compress(final byte[] uncompressed) throws IOException {
      final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream();
      compress(uncompressed, bytesOS, true);
      return bytesOS.toByteArray();
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createCompressingInputStream(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      final FastByteArrayOutputStream output = new FastByteArrayOutputStream();
      compress(uncompressed, output, true);
      return output.toInputStream();
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createCompressingInputStream(final InputStream uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      final PipedInputStream compressingInputStream = new PipedInputStream();
      final PipedOutputStream transfomer = new PipedOutputStream(compressingInputStream);
      final OutputStream compressingOutputStream = createCompressingOutputStream(transfomer);

      EXECUTOR.submit(() -> {
         try {
            IOUtils.copyLarge(uncompressed, compressingOutputStream);
            compressingOutputStream.flush();
         } catch (final IOException ex) {
            IOUtils.closeQuietly(compressingInputStream);
            throw ex;
         } finally {
            IOUtils.closeQuietly(uncompressed);
            IOUtils.closeQuietly(compressingOutputStream);
            IOUtils.closeQuietly(transfomer);
         }
         return null;
      });

      return compressingInputStream;
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final byte[] compressed) throws IOException {
      Args.notNull("compressed", compressed);
      return createDecompressingInputStream(new FastByteArrayInputStream(compressed));
   }

   @Override
   @SuppressWarnings("resource")
   public byte[] decompress(final byte[] compressed) throws IOException {
      Args.notNull("compressed", compressed);

      final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream(compressed.length);
      decompress(compressed, bytesOS, true);
      return bytesOS.toByteArray();
   }

   @Override
   public int decompress(final byte[] compressed, final byte[] output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (FastByteArrayOutputStream baos = new FastByteArrayOutputStream(compressed.length);
           InputStream compIS = createDecompressingInputStream(new FastByteArrayInputStream(compressed)) //
      ) {
         IOUtils.copyLarge(compIS, baos);
         if (baos.size() > output.length)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
         baos.writeTo(output);
         return baos.size();
      }
   }

   @SuppressWarnings("resource")
   @Override
   public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try {
         IOUtils.copyLarge(createDecompressingInputStream(compressed), output);
      } finally {
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @SuppressWarnings("resource")
   @Override
   public void decompress(final InputStream compressed, final OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try {
         final InputStream compIS = createDecompressingInputStream(compressed);
         IOUtils.copyLarge(compIS, output);
      } finally {
         IOUtils.closeQuietly(compressed);
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

}
