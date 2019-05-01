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

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoConstraint;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoInputStream;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzoOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LZOCompression extends AbstractCompression {

   public static final LZOCompression INSTANCE = new LZOCompression();

   private final LzoCompressor compressor;
   private final LzoDecompressor decompressor;

   public LZOCompression() {
      this(LzoAlgorithm.LZO1X, null);
   }

   public LZOCompression(final LzoAlgorithm algorithm) {
      this(algorithm, null);
   }

   public LZOCompression(final LzoAlgorithm algorithm, final LzoConstraint constraint) {
      Args.notNull("algorithm", algorithm);
      compressor = LzoLibrary.getInstance().newCompressor(algorithm, constraint);
      decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, constraint);
   }

   @Override
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

   @Override
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

   @Override
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      return new LzoOutputStream(output, compressor, 32 * 1024);
   }

   @Override
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      return new LzoInputStream(compressed, decompressor);
   }

   @Override
   public String toString() {
      return Strings.toString(this, "algorithm", compressor.getAlgorithm());
   }
}
