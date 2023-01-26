/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("null")
public class LZ4FrameCompression extends AbstractCompression {

   public static final LZ4FrameCompression INSTANCE = new LZ4FrameCompression();

   private static final LZ4FrameOutputStream.BLOCKSIZE DEFAULT_BLOCK_SIZE = LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB;
   private static final LZ4Compressor COMP = LZ4Factory.fastestInstance().fastCompressor();
   private static final LZ4SafeDecompressor DECOMP = LZ4Factory.fastestInstance().safeDecompressor();
   private static final XXHash32 CHECKSUM = XXHashFactory.fastestInstance().hash32();

   protected LZ4FrameCompression() {
      // prevent instantiation
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new LZ4FrameOutputStream(output, DEFAULT_BLOCK_SIZE, -1L, COMP, CHECKSUM, LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE);
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new LZ4FrameInputStream(compressed, DECOMP, CHECKSUM);
   }

   @Override
   public String toString() {
      return Strings.toString(this, ArrayUtils.EMPTY_OBJECT_ARRAY);
   }
}
