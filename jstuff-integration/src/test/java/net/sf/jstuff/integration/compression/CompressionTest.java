package net.sf.jstuff.integration.compression;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.fluttercode.datafactory.impl.DataFactory;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.CompressionBenchmark;
import net.sf.jstuff.core.compression.CompressionBenchmark.BenchmarkResult;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionTest extends TestCase {

    private static final byte[] TEST_TEXT_BYTES;
    static {
        final DataFactory df = new DataFactory();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append(//
                df.getBusinessName() + ", " + //
                        df.getAddress() + " " + df.getAddressLine2() + ", " + //
                        df.getNumberText(5) + " " + df.getCity() + ", " + //
                        df.getBirthDate() + ", " + //
                        df.getEmailAddress() //
            );
        }
        TEST_TEXT_BYTES = sb.toString().getBytes();
    }

    @SuppressWarnings("resource")
    public static void testByteArrayCompression(final ByteArrayCompression cmp) throws IOException {
        for (int i = 0; i < 4; i++) { // testing instance re-use
            final byte[] compressed = cmp.compress(TEST_TEXT_BYTES);
            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, cmp.decompress(compressed)));

            final FastByteArrayOutputStream uncompressedOS = new FastByteArrayOutputStream();
            final FastByteArrayOutputStream compressedOS = new FastByteArrayOutputStream();
            cmp.compress(TEST_TEXT_BYTES, compressedOS, true);
            cmp.decompress(compressedOS.toByteArray(), uncompressedOS, true);
            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, uncompressedOS.toByteArray()));
        }
    }

    @SuppressWarnings("resource")
    public static void testIOStreamCompression(final InputStreamCompression cmp) throws IOException {
        for (int i = 0; i < 4; i++) { // testing instance re-use
            final FastByteArrayOutputStream compressedOS = new FastByteArrayOutputStream();
            final FastByteArrayOutputStream uncompressedOS = new FastByteArrayOutputStream();
            cmp.compress(new ByteArrayInputStream(TEST_TEXT_BYTES), compressedOS, true);
            cmp.decompress(new ByteArrayInputStream(compressedOS.toByteArray()), uncompressedOS, true);

            assertTrue(ArrayUtils.isEquals(TEST_TEXT_BYTES, uncompressedOS.toByteArray()));
        }
    }

    public void testGZip() throws IOException {
        testByteArrayCompression(GZipCompression.INSTANCE);
        testIOStreamCompression(GZipCompression.INSTANCE);
    }

    public void testDeflate() throws IOException {
        testByteArrayCompression(DeflateCompression.INSTANCE);
        testIOStreamCompression(DeflateCompression.INSTANCE);
    }

    public void testSnappy() throws IOException {
        testByteArrayCompression(SnappyCompression.INSTANCE);
        testIOStreamCompression(SnappyCompression.INSTANCE);
    }

    public void testPerformance() throws IOException {
        final Map<InputStreamCompression, BenchmarkResult> result = new CompressionBenchmark() //
            .setTestData(TEST_TEXT_BYTES) //
            .setIterations(1000) //
            .addCompression(DeflateCompression.INSTANCE) //
            .addCompression(GZipCompression.INSTANCE) //
            .addCompression(LZ4BlockCompression.INSTANCE) //
            .addCompression(LZOCompression.INSTANCE) //
            .addCompression(SnappyCompression.INSTANCE) //
            .execute();

        for (final BenchmarkResult r : result.values()) {
            System.out.println(r);
        }
    }

    public void testLZ4Block() throws IOException {
        testByteArrayCompression(LZ4BlockCompression.INSTANCE);
        testIOStreamCompression(LZ4BlockCompression.INSTANCE);
    }
}
