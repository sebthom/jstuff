/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.compression;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.javafaker.Faker;
import com.github.luben.zstd.ZstdException;

import net.sf.jstuff.core.compression.Compression;
import net.sf.jstuff.core.compression.CompressionBenchmark;
import net.sf.jstuff.core.compression.CompressionBenchmark.BenchmarkResult;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionITest {

   protected static final byte[] TEST_TEXT_BYTES;

   static {
      final var sb = new StringBuilder();
      for (int i = 0; i < 1000; i++) {
         final Faker faker = new Faker(Locale.ENGLISH);
         sb.append(//
            faker.address().firstName() + ", " //
               + faker.address().lastName() + ", " //
               + faker.address().streetAddress(true) + ", " //
               + faker.address().cityPrefix() + faker.address().citySuffix() + ", " //
               + faker.address().country() + ", " //
               + faker.chuckNorris().fact() + "\n" //
         );
      }
      TEST_TEXT_BYTES = sb.toString().getBytes();
   }

   @SuppressWarnings("resource")
   public static void testByteArrayCompression(final Compression cmp) throws IOException {

      for (int i = 0; i < 4; i++) { // testing instance re-use

         final byte[] compressed = cmp.compress(TEST_TEXT_BYTES);

         {
            assertThat(cmp.compress(TEST_TEXT_BYTES)).isEqualTo(compressed); // test for reproducible results
            assertThat(cmp.decompress(compressed)).isEqualTo(TEST_TEXT_BYTES);
         }

         {
            final var uncompressed = new byte[TEST_TEXT_BYTES.length];
            assertThat(cmp.decompress(compressed, uncompressed)).isEqualTo(uncompressed.length);
            assertThat(uncompressed).isEqualTo(TEST_TEXT_BYTES);
         }

         {
            final var uncompressed = new byte[TEST_TEXT_BYTES.length - 1];
            try {
               cmp.decompress(compressed, uncompressed);
               failBecauseExceptionWasNotThrown(ZstdException.class);
            } catch (final ZstdException | IndexOutOfBoundsException ex) {
               // expected
            }
         }

         {
            final var compressedOS = new FastByteArrayOutputStream();
            cmp.compress(TEST_TEXT_BYTES, compressedOS);

            final var uncompressedOS = new FastByteArrayOutputStream();
            cmp.decompress(compressedOS.toByteArray(), uncompressedOS);
            assertThat(uncompressedOS.toByteArray()).isEqualTo(TEST_TEXT_BYTES);
         }

         {
            assertThat(IOUtils.readBytes(cmp.createDecompressingInputStream(compressed))).isEqualTo(TEST_TEXT_BYTES);

            final byte[] compressed2 = IOUtils.readBytes(cmp.createCompressingInputStream(TEST_TEXT_BYTES));
            assertThat(IOUtils.readBytes(cmp.createDecompressingInputStream(compressed2))).isEqualTo(TEST_TEXT_BYTES);
         }
      }
   }

   @SuppressWarnings("resource")
   public static void testInputStreamCompression(final Compression cmp) throws IOException {
      for (int i = 0; i < 4; i++) { // testing instance re-use
         final var compressedOS = new FastByteArrayOutputStream();
         final var uncompressedOS = new FastByteArrayOutputStream();
         cmp.compress(new ByteArrayInputStream(TEST_TEXT_BYTES), compressedOS);
         cmp.decompress(new ByteArrayInputStream(compressedOS.toByteArray()), uncompressedOS);

         assertThat(uncompressedOS.toByteArray()).isEqualTo(TEST_TEXT_BYTES);
         assertThat(IOUtils.readBytes(cmp.createDecompressingInputStream(new ByteArrayInputStream(compressedOS.toByteArray())))).isEqualTo(
            TEST_TEXT_BYTES);
         assertThat(cmp.decompress(IOUtils.readBytes(cmp.createCompressingInputStream(new ByteArrayInputStream(TEST_TEXT_BYTES)))))
            .isEqualTo(TEST_TEXT_BYTES);
      }
   }

   @Test
   public void testBrotli() throws IOException {
      testByteArrayCompression(BrotliCompression.INSTANCE);
      testInputStreamCompression(BrotliCompression.INSTANCE);
   }

   @Test
   public void testDeflate() throws IOException {
      testByteArrayCompression(DeflateCompression.INSTANCE);
      testInputStreamCompression(DeflateCompression.INSTANCE);
   }

   @Test
   public void testGZip() throws IOException {
      testByteArrayCompression(GZipCompression.INSTANCE);
      testInputStreamCompression(GZipCompression.INSTANCE);
   }

   @Test
   public void testLZ4Block() throws IOException {
      testByteArrayCompression(LZ4BlockCompression.INSTANCE);
      testInputStreamCompression(LZ4BlockCompression.INSTANCE);
   }

   @Test
   public void testLZ4Frame() throws IOException {
      testByteArrayCompression(LZ4FrameCompression.INSTANCE);
      testInputStreamCompression(LZ4FrameCompression.INSTANCE);
   }

   @Test
   public void testLZFFrame() throws IOException {
      testByteArrayCompression(LZFCompression.INSTANCE);
      testInputStreamCompression(LZFCompression.INSTANCE);
   }

   @Test
   public void testPerformance() throws IOException {
      final var benchmark = new CompressionBenchmark() //
         .setTestData(TEST_TEXT_BYTES) //
         .setIterations(500) //

         .addCompression(BrotliCompression.INSTANCE) //
         .addCompression(DeflateCompression.INSTANCE) //
         .addCompression(GZipCompression.INSTANCE) //
         .addCompression(LZ4BlockCompression.INSTANCE) //
         .addCompression(LZ4FrameCompression.INSTANCE) //
         .addCompression(LZFCompression.INSTANCE) //
         .addCompression(SnappyCompression.INSTANCE) //
         .addCompression(ZStdCompression.INSTANCE);

      final Map<Compression, BenchmarkResult> result = benchmark.execute();
      assertThat(result).isNotEmpty();

      System.out.println("Benchmark results by roundtrip speed:");
      int i = 1;
      for (final BenchmarkResult r : result.values().stream().sorted(CompressionBenchmark.COMPARATOR_ROUNDTRIP_SPEED).collect(Collectors
         .toList())) {
         System.out.println(" " + i++ + ". " + r);
      }
      System.out.println("Benchmark results by ratio:");
      i = 1;
      for (final BenchmarkResult r : result.values().stream().sorted(CompressionBenchmark.COMPARATOR_RATIO).collect(Collectors.toList())) {
         System.out.println(" " + i++ + ". " + r);
      }
   }

   @Test
   public void testSnappy() throws IOException {
      testByteArrayCompression(SnappyCompression.INSTANCE);
      testInputStreamCompression(SnappyCompression.INSTANCE);
   }

   @Test
   public void testZStd() throws IOException {
      testByteArrayCompression(ZStdCompression.INSTANCE);
      testInputStreamCompression(ZStdCompression.INSTANCE);
   }
}
