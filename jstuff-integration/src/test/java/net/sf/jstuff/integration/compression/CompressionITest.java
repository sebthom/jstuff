/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.compression;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;

import com.github.javafaker.Faker;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.Compression;
import net.sf.jstuff.core.compression.CompressionBenchmark;
import net.sf.jstuff.core.compression.CompressionBenchmark.BenchmarkResult;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionITest extends TestCase {

   protected static final byte[] TEST_TEXT_BYTES;

   static {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 500; i++) {
         final Faker faker = new Faker(Locale.ENGLISH);
         sb.append(//
            faker.address().firstName() + ", " + //
               faker.address().lastName() + ", " + //
               faker.address().streetAddress(true) + ", " + //
               faker.address().cityPrefix() + faker.address().citySuffix() + ", " + //
               faker.address().country() + ", " + //
               faker.chuckNorris().fact() + "\n" //
         );
      }
      TEST_TEXT_BYTES = sb.toString().getBytes();
   }

   @SuppressWarnings("resource")
   public static void testByteArrayCompression(final Compression cmp) throws IOException {
      for (int i = 0; i < 4; i++) { // testing instance re-use

         final byte[] compressed = cmp.compress(TEST_TEXT_BYTES);

         {
            assertTrue(ArrayUtils.isEquals(compressed, cmp.compress(TEST_TEXT_BYTES))); // test for reproducable results

            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, cmp.decompress(compressed)));
         }

         {
            final byte[] uncompressed = new byte[TEST_TEXT_BYTES.length];
            assertEquals(uncompressed.length, cmp.decompress(compressed, uncompressed));
            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, uncompressed));
         }

         {
            final byte[] uncompressed = new byte[TEST_TEXT_BYTES.length - 1];
            try {
               cmp.decompress(compressed, uncompressed);
               fail();
            } catch (final IndexOutOfBoundsException ex) {
               // expected
            }
         }

         {
            final FastByteArrayOutputStream compressedOS = new FastByteArrayOutputStream();
            cmp.compress(TEST_TEXT_BYTES, compressedOS, true);

            final FastByteArrayOutputStream uncompressedOS = new FastByteArrayOutputStream();
            cmp.decompress(compressedOS.toByteArray(), uncompressedOS, true);

            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, uncompressedOS.toByteArray()));
         }

         {
            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, IOUtils.readBytes(cmp.createDecompressingInputStream(compressed))));

            final byte[] compressed2 = IOUtils.readBytes(cmp.createCompressingInputStream(TEST_TEXT_BYTES));
            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, IOUtils.readBytes(cmp.createDecompressingInputStream(compressed2))));
         }

      }
   }

   @SuppressWarnings("resource")
   public static void testInputStreamCompression(final Compression cmp) throws IOException {
      for (int i = 0; i < 4; i++) { // testing instance re-use
         final FastByteArrayOutputStream compressedOS = new FastByteArrayOutputStream();
         final FastByteArrayOutputStream uncompressedOS = new FastByteArrayOutputStream();
         cmp.compress(new ByteArrayInputStream(TEST_TEXT_BYTES), compressedOS, true);
         cmp.decompress(new ByteArrayInputStream(compressedOS.toByteArray()), uncompressedOS, true);

         assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, uncompressedOS.toByteArray()));

         assertTrue(ArrayUtils.isEquals( //
            TEST_TEXT_BYTES, //
            IOUtils.readBytes(cmp.createDecompressingInputStream(new ByteArrayInputStream(compressedOS.toByteArray()))) //
         ));

         assertTrue(ArrayUtils.isEquals( //
            TEST_TEXT_BYTES, //
            cmp.decompress(IOUtils.readBytes(cmp.createCompressingInputStream(new ByteArrayInputStream(TEST_TEXT_BYTES)))) ///
         ));
      }
   }

   public void testBrotli() throws IOException {
      if (!SystemUtils.IS_OS_WINDOWS) { // https://github.com/MeteoGroup/jbrotli/issues/15
         testByteArrayCompression(BrotliCompression.INSTANCE);
         testInputStreamCompression(BrotliCompression.INSTANCE);
      }
   }

   public void testDeflate() throws IOException {
      testByteArrayCompression(DeflateCompression.INSTANCE);
      testInputStreamCompression(DeflateCompression.INSTANCE);
   }

   public void testGZip() throws IOException {
      testByteArrayCompression(GZipCompression.INSTANCE);
      testInputStreamCompression(GZipCompression.INSTANCE);
   }

   public void testLZ4Block() throws IOException {
      testByteArrayCompression(LZ4BlockCompression.INSTANCE);
      testInputStreamCompression(LZ4BlockCompression.INSTANCE);
   }

   public void testLZ4Frame() throws IOException {
      testByteArrayCompression(LZ4FrameCompression.INSTANCE);
      testInputStreamCompression(LZ4FrameCompression.INSTANCE);
   }

   public void testLZO() throws IOException {
      testByteArrayCompression(LZOCompression.INSTANCE);
      testInputStreamCompression(LZOCompression.INSTANCE);
   }

   public void testPerformance() throws IOException {
      final CompressionBenchmark benchmark = new CompressionBenchmark() //
         .setTestData(TEST_TEXT_BYTES) //
         .setIterations(500) //

         .addCompression(DeflateCompression.INSTANCE) //
         .addCompression(GZipCompression.INSTANCE) //
         .addCompression(LZ4BlockCompression.INSTANCE) //
         .addCompression(LZ4FrameCompression.INSTANCE) //
         .addCompression(LZOCompression.INSTANCE) //
         .addCompression(SnappyCompression.INSTANCE) //
         .addCompression(ZStdCompression.INSTANCE) //
         .addCompression(new ZStdCompression(ZStdCompression.LEVEL_SMALL_AS_DEFLATE_4));

      if (!SystemUtils.IS_OS_WINDOWS) {
         // TODO https://github.com/MeteoGroup/jbrotli/issues/15
         benchmark.addCompression(BrotliCompression.INSTANCE);
      }

      final Map<Compression, BenchmarkResult> result = benchmark.execute();

      System.out.println("Benchmark results:");
      for (final BenchmarkResult r : result.values()) {
         System.out.print(" ");
         System.out.println(r);
      }
   }

   public void testSnappy() throws IOException {
      testByteArrayCompression(SnappyCompression.INSTANCE);
      testInputStreamCompression(SnappyCompression.INSTANCE);
   }

   public void testZStd() throws IOException {
      testByteArrayCompression(ZStdCompression.INSTANCE);
      testInputStreamCompression(ZStdCompression.INSTANCE);
   }
}
