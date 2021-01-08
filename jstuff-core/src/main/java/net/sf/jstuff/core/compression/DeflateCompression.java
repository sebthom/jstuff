/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Compress/decompress using "deflate" compression format.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DeflateCompression extends AbstractCompression {

   /**
    * Shared instance with compression level 4.
    *
    * See https://www.rootusers.com/gzip-vs-bzip2-vs-xz-performance-comparison/#attachment_1478
    */
   public static final DeflateCompression INSTANCE = new DeflateCompression(4);

   private final int compressionLevel;

   private final ThreadLocal<Deflater> compressor = new ThreadLocal<Deflater>() {
      @Override
      public Deflater get() {
         final Deflater result = super.get();
         result.reset();
         return result;
      }

      @Override
      protected Deflater initialValue() {
         final Deflater def = new Deflater(compressionLevel);
         if (dictionary.length > 0) {
            def.setDictionary(dictionary);
         }
         return def;
      }
   };

   private final ThreadLocal<Inflater> decompressor = new ThreadLocal<Inflater>() {
      @Override
      public Inflater get() {
         final Inflater result = super.get();
         result.reset();
         return result;
      }

      @Override
      protected Inflater initialValue() {
         final Inflater inf = new Inflater(false);
         if (dictionary.length > 0) {
            inf.setDictionary(dictionary);
         }
         return inf;
      }
   };

   private byte[] dictionary = ArrayUtils.EMPTY_BYTE_ARRAY;

   public DeflateCompression(final int compressionLevel) {
      this.compressionLevel = compressionLevel;
   }

   public DeflateCompression(final int compressionLevel, final byte[] dictionary) {
      this.compressionLevel = compressionLevel;

      if (dictionary != null && dictionary.length > 0) {
         this.dictionary = dictionary.clone();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final byte[] uncompressed, final OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      try (DeflaterOutputStream compOS = new DeflaterOutputStream(toCloseIgnoring(output), compressor.get())) {
         compOS.write(uncompressed);
         compOS.finish();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void compress(final InputStream uncompressed, final OutputStream output) throws IOException {
      Args.notNull("uncompressed", uncompressed);
      Args.notNull("output", output);

      try (DeflaterOutputStream compOS = new DeflaterOutputStream(toCloseIgnoring(output), compressor.get())) {
         IOUtils.copyLarge(uncompressed, compOS);
         compOS.finish();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createCompressingInputStream(final InputStream uncompressed) throws IOException {
      Args.notNull("uncompressed", uncompressed);

      final Deflater compressor = new Deflater(compressionLevel);
      if (dictionary.length > 0) {
         compressor.setDictionary(dictionary);
      }
      return new DeflaterInputStream(uncompressed, compressor);
   }

   @Override
   @SuppressWarnings("resource")
   public OutputStream createCompressingOutputStream(final OutputStream output) {
      Args.notNull("output", output);

      final Deflater compressor = new Deflater(compressionLevel);
      if (dictionary.length > 0) {
         compressor.setDictionary(dictionary);
      }
      return new DeflaterOutputStream(output, compressor);
   }

   @Override
   @SuppressWarnings("resource")
   public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
      Args.notNull("compressed", compressed);

      final Inflater decompressor = new Inflater(false);
      if (dictionary.length > 0) {
         decompressor.setDictionary(dictionary);
      }
      return new InflaterInputStream(compressed, decompressor);
   }

   @Override
   public int decompress(final byte[] compressed, final byte[] output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      final Inflater inf = decompressor.get();
      inf.setInput(compressed);
      try {
         final int bytesRead = inf.inflate(output);
         if (inf.getRemaining() > 0)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
         return bytesRead;
      } catch (final DataFormatException ex) {
         throw new IOException(ex);
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void decompress(final byte[] compressed, final OutputStream output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (InflaterInputStream compIS = new InflaterInputStream(new FastByteArrayInputStream(compressed), decompressor.get())) {
         IOUtils.copyLarge(compIS, output);
         output.flush();
      }
   }

   @Override
   @SuppressWarnings("resource")
   public void decompress(final InputStream compressed, final OutputStream output) throws IOException {
      Args.notNull("compressed", compressed);
      Args.notNull("output", output);

      try (InflaterInputStream compIS = new InflaterInputStream(toCloseIgnoring(compressed), decompressor.get())) {
         IOUtils.copyLarge(compIS, output);
         output.flush();
      }
   }

   public int getCompressionLevel() {
      return compressionLevel;
   }

   public byte[] getDictionary() {
      return dictionary.clone();
   }

   @Override
   public String toString() {
      return Strings.toString(this, "compressionLevel", compressionLevel);
   }
}
