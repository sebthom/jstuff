/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

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
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionBenchmark {

    public static class BenchmarkResult {
        public InputStreamCompression compression;
        public int iterations;
        public long compressTimeMS;
        public long decompressTimeMS;
        public int uncompressedSize;
        public int compressedSize;

        @Override
        public String toString() {
            final NumberFormat nf = NumberFormat.getIntegerInstance(Locale.ENGLISH);
            return String.format("%7s x %7s bytes -> %7s bytes in  compr: %s  decompr: %s  total: %s  %s", //
                nf.format(iterations), //
                nf.format(uncompressedSize), //
                nf.format(compressedSize), //
                DurationFormatUtils.formatDurationHMS(compressTimeMS), //
                DurationFormatUtils.formatDurationHMS(decompressTimeMS), //
                DurationFormatUtils.formatDurationHMS(compressTimeMS + decompressTimeMS), //
                compression);
        }
    }

    public static class CompressSpeedComparator implements Comparator<BenchmarkResult> {
        public int compare(final BenchmarkResult o1, final BenchmarkResult o2) {
            return o1.compressTimeMS < o2.compressTimeMS ? -1 : o1.compressTimeMS == o2.compressTimeMS ? 0 : 1;
        }
    }

    public static class DecompressSpeedComparator implements Comparator<BenchmarkResult> {
        public int compare(final BenchmarkResult o1, final BenchmarkResult o2) {
            return o1.decompressTimeMS < o2.decompressTimeMS ? -1 : o1.decompressTimeMS == o2.decompressTimeMS ? 0 : 1;
        }
    }

    public static class SizeComparator implements Comparator<BenchmarkResult> {
        public int compare(final BenchmarkResult o1, final BenchmarkResult o2) {
            return o1.compressedSize < o2.compressedSize ? -1 : o1.compressedSize == o2.compressedSize ? 0 : 1;
        }
    }

    public static class SpeedComparator implements Comparator<BenchmarkResult> {
        public int compare(final BenchmarkResult o1, final BenchmarkResult o2) {
            return o1.compressTimeMS + o1.decompressTimeMS < o2.compressTimeMS + o2.decompressTimeMS ? -1
                    : o1.compressTimeMS + o1.decompressTimeMS == o2.compressTimeMS + o2.decompressTimeMS ? 0 : 1;
        }
    }

    private static final Logger LOG = Logger.create();

    private final Set<InputStreamCompression> compressions = new HashSet<InputStreamCompression>();
    private byte[] uncompressed = ArrayUtils.EMPTY_BYTE_ARRAY;
    private int iterations = 0;

    public CompressionBenchmark addCompression(final InputStreamCompression compression) {
        Args.notNull("compression", compression);
        compressions.add(compression);
        return this;
    }

    @SuppressWarnings("resource")
    public Map<InputStreamCompression, BenchmarkResult> execute() throws IOException {
        final FastByteArrayInputStream uncompressedIS = new FastByteArrayInputStream(uncompressed);
        final FastByteArrayOutputStream compressedOS = new FastByteArrayOutputStream();

        final Map<InputStreamCompression, BenchmarkResult> result = new HashMap<InputStreamCompression, BenchmarkResult>();
        for (final InputStreamCompression cmp : compressions) {
            uncompressedIS.reset();
            compressedOS.reset();
            cmp.compress(uncompressedIS, compressedOS, true);

            final BenchmarkResult res = new BenchmarkResult();
            res.compression = cmp;
            res.uncompressedSize = uncompressed.length;
            res.compressedSize = compressedOS.size();
            res.iterations = iterations;

            result.put(cmp, res);
        }

        final FastByteArrayOutputStream uncompressedOS = new FastByteArrayOutputStream();
        final FastByteArrayInputStream compressedIS = new FastByteArrayInputStream(ArrayUtils.EMPTY_BYTE_ARRAY);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        /*
         * warmup
         */
        for (final InputStreamCompression cmp : compressions) {
            LOG.info("Warmup [%s]...", cmp.getClass().getSimpleName());
            for (int i = 0; i < 50; i++) {
                uncompressedIS.reset();
                compressedOS.reset();
                uncompressedOS.reset();
                cmp.compress(uncompressedIS, compressedOS, true);
                compressedIS.setData(compressedOS.toByteArray());
                cmp.decompress(compressedIS, uncompressedOS, true);
                if (!ArrayUtils.isEquals(uncompressed, uncompressedOS.toByteArray()))
                    throw new IOException("Compression [" + cmp + "] is buggy!");
            }
        }

        /*
         * micro benchmark
         */
        final StopWatch sw = new StopWatch();
        for (final InputStreamCompression cmp : compressions) {
            LOG.info("Benchmarking compression [%s]...", cmp.getClass().getSimpleName());

            System.gc();
            Threads.sleep(200);
            System.gc();
            Threads.sleep(100);

            sw.reset();
            sw.start();
            for (int i = 0; i < iterations; i++) {
                uncompressedIS.reset();
                compressedOS.reset();
                cmp.compress(uncompressedIS, compressedOS, true);
            }
            sw.stop();
            result.get(cmp).compressTimeMS = sw.getTime();
        }
        for (final InputStreamCompression cmp : compressions) {
            LOG.info("Benchmarking decompression [%s]...", cmp.getClass().getSimpleName());

            System.gc();
            Threads.sleep(200);
            System.gc();
            Threads.sleep(100);

            uncompressedIS.reset();
            compressedOS.reset();
            cmp.compress(uncompressedIS, compressedOS, true);
            compressedIS.setData(compressedOS.toByteArray());

            sw.reset();
            sw.start();
            for (int i = 0; i < iterations; i++) {
                compressedIS.reset();
                uncompressedOS.reset();
                cmp.decompress(compressedIS, uncompressedOS, true);
            }
            sw.stop();
            result.get(cmp).decompressTimeMS = sw.getTime();
        }

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        LOG.info("Done.");

        return Maps.sortByValue(result, new CompressSpeedComparator());
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
