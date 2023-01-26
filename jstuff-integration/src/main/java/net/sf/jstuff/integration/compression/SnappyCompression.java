/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SnappyCompression extends AbstractCompression {

   private static final int DEFAULT_BLOCK_SIZE = 64 * 1024;

   public static final SnappyCompression INSTANCE = new SnappyCompression();

   protected SnappyCompression() {
      // prevent instantiation
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      return new SnappyInputStream(compressed);
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
      Args.notNull("output", output);

      return new SnappyOutputStream(output, DEFAULT_BLOCK_SIZE);
   }

   @Override
   public String toString() {
      return Strings.toString(this, ArrayUtils.EMPTY_OBJECT_ARRAY);
   }
}
