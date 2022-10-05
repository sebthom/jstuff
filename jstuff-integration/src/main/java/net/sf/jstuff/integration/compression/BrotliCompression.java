/*********************************************************************
 * Copyright 2010-2022 by Sebastian Thomschke and others.
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

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.decoder.Decoder;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import com.aayushatharva.brotli4j.decoder.DirectDecompress;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * Or: https://github.com/google/brotli/blob/master/java/org/brotli/wrapper/enc/BrotliOutputStream.java
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BrotliCompression extends AbstractCompression {

   public static final int LEVEL_SMALL_AS_DEFLATE_4 = 2;
   public static final int LEVEL_SMALL_AS_DEFLATE_6 = 4;
   public static final int LEVEL_SMALL_AS_DEFLATE_9 = 4;

   public static final BrotliCompression INSTANCE = new BrotliCompression(LEVEL_SMALL_AS_DEFLATE_4);

   static {
      Assert.isTrue(Brotli4jLoader.isAvailable(), "Failed to load Brotli native library!");
   }

   private final int compressionLevel;
   private final Encoder.Parameters encoderParams;

   /**
    * @param compressionLevel 0-11 or -1
    */
   public BrotliCompression(final int compressionLevel) {
      this.compressionLevel = compressionLevel;
      encoderParams = new Encoder.Parameters();
      encoderParams.setQuality(compressionLevel);
   }

   @Override
   @SuppressWarnings("null")
   public byte[] compress(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      return Encoder.compress(uncompressed, encoderParams);
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new BrotliOutputStream(output, encoderParams);
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new BrotliInputStream(compressed);
   }

   @Override
   @SuppressWarnings("null")
   public byte[] decompress(final byte[] compressed) throws IOException {
      Args.notNull("compressed", compressed);

      final DirectDecompress result = Decoder.decompress(compressed);
      if (result.getResultStatus() != DecoderJNI.Status.DONE)
         throw new IOException("Decompression via Brotli native library failed with: " + result.getResultStatus());
      return result.getDecompressedData();
   }

   public int getCompressionLevel() {
      return compressionLevel;
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }
}
