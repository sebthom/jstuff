/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ning.compress.lzf.LZFDecoder;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.LZFOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LZFCompression extends AbstractCompression {

   public static final LZFCompression INSTANCE = new LZFCompression();

   protected LZFCompression() {
      // prevent instantiation
   }

   @SuppressWarnings("null")
   @Override
   public byte[] compress(final byte[] uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      return LZFEncoder.encode(uncompressed);
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new LZFOutputStream(output);
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new LZFInputStream(compressed);
   }

   @SuppressWarnings("null")
   @Override
   public byte[] decompress(final byte[] compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return LZFDecoder.decode(compressed);
   }

   @Override
   public String toString() {
      return Strings.toString(this, ArrayUtils.EMPTY_OBJECT_ARRAY);
   }
}
