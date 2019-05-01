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

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
class LZ4FrameCompression extends AbstractCompression {

   public static final LZ4FrameCompression INSTANCE = new LZ4FrameCompression();

   private static final LZ4FrameOutputStream.BLOCKSIZE DEFAULT_BLOCK_SIZE = LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB;

   // TODO private static final LZ4Compressor COMP = LZ4Factory.fastestInstance().fastCompressor(); // requires https://github.com/lz4/lz4-java/pull/113
   private static final LZ4SafeDecompressor DECOMP = LZ4Factory.fastestInstance().safeDecompressor();
   private static final XXHash32 CHECKSUM = XXHashFactory.fastestInstance().hash32();

   @Override
   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      if (!closeOutput) {
         output = new DelegatingOutputStream(output, true);
      }

      try {
         final OutputStream compOS = createCompressingOutputStream(output);
         compOS.write(uncompressed);
         compOS.close(); // writes end-mark
      } finally {
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final InputStream uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      if (!closeOutput) {
         output = new DelegatingOutputStream(output, true);
      }

      try {
         final OutputStream compOS = createCompressingOutputStream(output);
         IOUtils.copyLarge(uncompressed, compOS);
         compOS.close(); // writes end-mark
      } finally {
         IOUtils.closeQuietly(uncompressed);
         if (closeOutput) {
            IOUtils.closeQuietly(output);
         }
      }
   }

   @Override
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      return new LZ4FrameOutputStream(output, DEFAULT_BLOCK_SIZE, -1L, /* TODO: COMP, CHECKSUM, */
         LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE);
   }

   @Override
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      return new LZ4FrameInputStream(compressed, DECOMP, CHECKSUM);
   }

   @Override
   public String toString() {
      return Strings.toString(this, new Object[0]);
   }

}
