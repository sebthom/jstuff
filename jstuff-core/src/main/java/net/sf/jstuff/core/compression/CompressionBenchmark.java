/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.compression;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Benchmark for compression types.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionBenchmark {

   public static class BenchmarkResult {

      public @Nullable Compression compression;
      public int iterations;
      public long compressTimeMS;
      public long decompressTimeMS;
      public int uncompressedSize;
      public int compressedSize;

      @Override
      public String toString() {
         final NumberFormat nf = NumberFormat.getIntegerInstance(Locale.ENGLISH);
         return String.format("%d x %s bytes -> %s bytes in  comp: %s  decomp: %s  total: %s  %s", //
            iterations, //
            nf.format(uncompressedSize), //
            nf.format(compressedSize), //
            DurationFormatUtils.formatDurationHMS(compressTimeMS), //
            DurationFormatUtils.formatDurationHMS(decompressTimeMS), //
            DurationFormatUtils.formatDurationHMS(compressTimeMS + decompressTimeMS), //
            compression);
      }
   }

   public static final Comparator<BenchmarkResult> COMPARATOR_COMPRESS_SPEED = //
         (o1, o2) -> o1.compressTimeMS < o2.compressTimeMS ? -1 : o1.compressTimeMS == o2.compressTimeMS ? 0 : 1;
   public static final Comparator<BenchmarkResult> COMPARATOR_DECOMPRESS_SPEED = //
         (o1, o2) -> o1.decompressTimeMS < o2.decompressTimeMS ? -1 : o1.decompressTimeMS == o2.decompressTimeMS ? 0 : 1;
   public static final Comparator<BenchmarkResult> COMPARATOR_RATIO = //
         (o1, o2) -> o1.compressedSize < o2.compressedSize ? -1 : o1.compressedSize == o2.compressedSize ? 0 : 1;
   public static final Comparator<BenchmarkResult> COMPARATOR_ROUNDTRIP_SPEED = //
         (o1, o2) -> o1.compressTimeMS + o1.decompressTimeMS < o2.compressTimeMS + o2.decompressTimeMS ? -1
               : o1.compressTimeMS + o1.decompressTimeMS == o2.compressTimeMS + o2.decompressTimeMS ? 0 : 1;

   private static final Logger LOG = Logger.create();

   private final Set<Compression> compressions = new HashSet<>();
   private byte[] uncompressed = ArrayUtils.EMPTY_BYTE_ARRAY;
   private int iterations;

   public CompressionBenchmark addCompression(final Compression compression) {
      Args.notNull("compression", compression);
      compressions.add(compression);
      return this;
   }

   @SuppressWarnings("resource")
   public Map<Compression, BenchmarkResult> execute() throws IOException {
      final var result = new HashMap<Compression, BenchmarkResult>();
      for (final Compression cmp : compressions) {
         final var uncompressedIS = new FastByteArrayInputStream(uncompressed);
         final var compressedOS = new FastByteArrayOutputStream();
         cmp.compress(uncompressedIS, compressedOS);

         final BenchmarkResult res = new BenchmarkResult();
         res.compression = cmp;
         res.uncompressedSize = uncompressed.length;
         res.compressedSize = compressedOS.size();
         res.iterations = iterations;

         result.put(cmp, res);
      }

      Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

      /*
       * warmup
       */
      for (final Compression cmp : compressions) {
         LOG.info("Warmup [%s]...", cmp);
         for (int i = 0; i < 50; i++) {
            final var uncompressedIS = new FastByteArrayInputStream(uncompressed);
            final var compressedOS = new FastByteArrayOutputStream();
            cmp.compress(uncompressedIS, compressedOS);

            final var compressedIS = new FastByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY);
            compressedIS.setData(compressedOS.toByteArray());

            final var uncompressedOS = new FastByteArrayOutputStream();
            cmp.decompress(compressedIS, uncompressedOS);
            if (!Objects.deepEquals(uncompressed, uncompressedOS.toByteArray()))
               throw new IOException("Compression [" + cmp + "] is buggy!");
         }
      }

      /*
       * micro benchmark
       */
      final var sw = new StopWatch();
      for (final Compression cmp : compressions) {
         LOG.info("Benchmarking compression [%s]...", cmp);

         System.gc();
         Threads.sleep(200);
         System.gc();
         Threads.sleep(100);

         final var uncompressedIS = new FastByteArrayInputStream(uncompressed);
         final var compressedOS = new FastByteArrayOutputStream();

         sw.reset();
         sw.start();
         for (int i = 0; i < iterations; i++) {
            uncompressedIS.reset();
            compressedOS.reset();
            cmp.compress(uncompressedIS, compressedOS);
         }
         sw.stop();
         asNonNull(result.get(cmp)).compressTimeMS = sw.getDuration().toMillis();
      }
      for (final Compression cmp : compressions) {
         LOG.info("Benchmarking decompression [%s]...", cmp);

         System.gc();
         Threads.sleep(200);
         System.gc();
         Threads.sleep(100);

         final var uncompressedIS = new FastByteArrayInputStream(uncompressed);
         final var compressedOS = new FastByteArrayOutputStream();
         final var uncompressedOS = new FastByteArrayOutputStream();
         cmp.compress(uncompressedIS, compressedOS);

         final var compressedIS = new FastByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY);
         compressedIS.setData(compressedOS.toByteArray());

         sw.reset();
         sw.start();
         for (int i = 0; i < iterations; i++) {
            compressedIS.reset();
            uncompressedOS.reset();
            cmp.decompress(compressedIS, uncompressedOS);
         }
         sw.stop();
         asNonNull(result.get(cmp)).decompressTimeMS = sw.getDuration().toMillis();
      }

      Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

      LOG.info("Done.");

      return Maps.sortByValue(result, COMPARATOR_ROUNDTRIP_SPEED);
   }

   public CompressionBenchmark setIterations(final int iterations) {
      this.iterations = iterations;
      return this;
   }

   public CompressionBenchmark setTestData(final byte[] uncompressed) {
      Args.notNull("uncompressed", uncompressed);
      this.uncompressed = uncompressed;
      return this;
   }
}
