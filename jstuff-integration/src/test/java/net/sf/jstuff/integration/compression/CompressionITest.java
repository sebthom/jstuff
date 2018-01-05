package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.util.Map;

import net.sf.jstuff.core.compression.Compression;
import net.sf.jstuff.core.compression.CompressionBenchmark;
import net.sf.jstuff.core.compression.CompressionBenchmark.BenchmarkResult;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionITest extends CompressionTest {

    /* TODO https://github.com/MeteoGroup/jbrotli/issues/15
    public void testBrotli() throws IOException {
        testByteArrayCompression(BrotliCompression.INSTANCE);
        testInputStreamCompression(BrotliCompression.INSTANCE);
    }
    */

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
        final Map<Compression, BenchmarkResult> result = new CompressionBenchmark() //
            .setTestData(TEST_TEXT_BYTES) //
            .setIterations(500) //
            // .addCompression(BrotliCompression.INSTANCE) // TODO https://github.com/MeteoGroup/jbrotli/issues/15
            .addCompression(DeflateCompression.INSTANCE) //
            .addCompression(GZipCompression.INSTANCE) //
            .addCompression(LZ4BlockCompression.INSTANCE) //
            .addCompression(LZ4FrameCompression.INSTANCE) //
            .addCompression(LZOCompression.INSTANCE) //
            .addCompression(SnappyCompression.INSTANCE) //
            .addCompression(ZStdCompression.INSTANCE) //
            .addCompression(new ZStdCompression(ZStdCompression.LEVEL_SMALL_AS_DEFLATE_4)) //
            .execute();

        System.out.println("Benchmark results:");
        for (final BenchmarkResult r : result.values()) {
            System.out.print(" ");
            System.out.println(r);
        }
    }

    public void testZStd() throws IOException {
        testByteArrayCompression(ZStdCompression.INSTANCE);
        testInputStreamCompression(ZStdCompression.INSTANCE);
    }
}
