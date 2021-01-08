/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Checksum;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.xxhash.XXHashFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LZ4BlockCompression extends AbstractCompression {

   public static final LZ4BlockCompression INSTANCE = new LZ4BlockCompression();

   private static final int DEFAULT_BLOCK_SIZE = 64 * 1024;

   private static final LZ4Compressor COMP = LZ4Factory.fastestInstance().fastCompressor();
   private static final LZ4FastDecompressor DECOMP = LZ4Factory.fastestInstance().fastDecompressor();
   private static final ThreadLocal<Checksum> CHECKSUM = ThreadLocal.withInitial( //
      () -> XXHashFactory.fastestInstance().newStreamingHash32(0x97_47B_28C /* LZ4BlockOutputStream.DEFAULT_SEED*/).asChecksum() //
   );

   protected LZ4BlockCompression() {
      // prevent instantiation
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, final OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      final int blockSize = uncompressed.length >= DEFAULT_BLOCK_SIZE //
         ? DEFAULT_BLOCK_SIZE //
         : uncompressed.length < 65 //
            ? 64 //
            : uncompressed.length;

      try (LZ4BlockOutputStream compOS = new LZ4BlockOutputStream(toCloseIgnoring(output), blockSize, COMP, CHECKSUM.get(), false)) {
         compOS.write(uncompressed);
         compOS.finish();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final InputStream uncompressed, OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      output = new DelegatingOutputStream(output, true);

      try (LZ4BlockOutputStream compOS = new LZ4BlockOutputStream(toCloseIgnoring(output), DEFAULT_BLOCK_SIZE, COMP, CHECKSUM.get(), false)) {
         IOUtils.copyLarge(uncompressed, compOS);
         compOS.finish();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new LZ4BlockOutputStream(output, DEFAULT_BLOCK_SIZE, COMP);
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new LZ4BlockInputStream(compressed, DECOMP);
   }

   @Override
   @SuppressWarnings("resource")
   public void decompress(final InputStream compressed, final OutputStream output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (LZ4BlockInputStream compIS = new LZ4BlockInputStream(toCloseIgnoring(compressed), DECOMP, CHECKSUM.get())) {
         IOUtils.copyLarge(compIS, output);
         output.flush();
      }
   }

   @Override
   public String toString() {
      return Strings.toString(this, ArrayUtils.EMPTY_OBJECT_ARRAY);
   }
}
