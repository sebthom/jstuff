/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingInputStream;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractCompression implements Compression {

   private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

   @Override
   public byte[] compress(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      try (var output = new FastByteArrayOutputStream()) {
         compress(uncompressed, output);
         return output.toByteArray();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, final OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      try (OutputStream compOS = createCompressingOutputStream(toCloseIgnoring(output))) {
         compOS.write(uncompressed);
         compOS.flush();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final InputStream uncompressed, final OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      try (OutputStream compOS = createCompressingOutputStream(toCloseIgnoring(output))) {
         IOUtils.copyLarge(uncompressed, compOS);
         compOS.flush();
      }
   }

   @Override
   public InputStream createCompressingInputStream(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      try (var output = new FastByteArrayOutputStream()) {
         compress(uncompressed, output);
         return output.toInputStream();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createCompressingInputStream(final InputStream uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      final var compressingInputStream = new PipedInputStream();
      final var transfomer = new PipedOutputStream(compressingInputStream);
      final OutputStream compressingOutputStream = createCompressingOutputStream(transfomer);

      EXECUTOR.submit(() -> {
         try {
            IOUtils.copyLarge(uncompressed, compressingOutputStream);
            compressingOutputStream.flush();
         } catch (final IOException ex) {
            IOUtils.closeQuietly(compressingInputStream);
            throw ex;
         } finally {
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

      final var bytesOS = new FastByteArrayOutputStream(compressed.length);
      decompress(compressed, bytesOS);
      return bytesOS.toByteArray();
   }

   @Override
   public int decompress(final byte[] compressed, final byte[] output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (var baos = new FastByteArrayOutputStream(compressed.length);
           InputStream compIS = createDecompressingInputStream(new FastByteArrayInputStream(compressed)) //
      ) {
         IOUtils.copyLarge(compIS, baos);
         if (baos.size() > output.length)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
         baos.writeTo(output);
         return baos.size();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void decompress(final byte[] compressed, final OutputStream output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (InputStream compIS = createDecompressingInputStream(compressed)) {
         IOUtils.copyLarge(compIS, output);
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void decompress(final InputStream compressed, final OutputStream output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (InputStream compIS = createDecompressingInputStream(toCloseIgnoring(compressed))) {
         IOUtils.copyLarge(compIS, output);
         output.flush();
      }
   }

   @SuppressWarnings("resource")
   protected DelegatingInputStream toCloseIgnoring(final InputStream stream) {
      Args.notNull("stream", stream);

      return new DelegatingInputStream(stream, true);
   }

   @SuppressWarnings("resource")
   protected DelegatingOutputStream toCloseIgnoring(final OutputStream stream) {
      Args.notNull("stream", stream);

      return new DelegatingOutputStream(stream, true);
   }
}
