package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.util.Map;

import net.sf.jstuff.core.compression.CompressionBenchmark;
import net.sf.jstuff.core.compression.CompressionBenchmark.BenchmarkResult;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionITest extends CompressionTest {

    public void testLZ4Block() throws IOException {
        testByteArrayCompression(LZ4BlockCompression.INSTANCE);
        testIOStreamCompression(LZ4BlockCompression.INSTANCE);
    }

    public void testLZO() throws IOException {
        testByteArrayCompression(LZOCompression.INSTANCE);
        testIOStreamCompression(LZOCompression.INSTANCE);
    }

    public void testPerformance() throws IOException {
        final Map<InputStreamCompression, BenchmarkResult> result = new CompressionBenchmark() //
            .setTestData(TEST_TEXT_BYTES) //
            .setIterations(1000) //
            .addCompression(DeflateCompression.INSTANCE) //
            .addCompression(GZipCompression.INSTANCE) //
            .addCompression(LZ4BlockCompression.INSTANCE) //
            .addCompression(SnappyCompression.INSTANCE) //
            //.addCompression(LZOCompression.INSTANCE) // buggy - throws random exceptions
            .addCompression(ZStdCompression.INSTANCE) //
            .execute();

        for (final BenchmarkResult r : result.values()) {
            System.out.println(r);
        }
    }

    public void testZStd() throws IOException {
        testByteArrayCompression(ZStdCompression.INSTANCE);
        testIOStreamCompression(ZStdCompression.INSTANCE);
    }
}
