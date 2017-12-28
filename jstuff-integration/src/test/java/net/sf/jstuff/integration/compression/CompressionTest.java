package net.sf.jstuff.integration.compression;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

import com.github.javafaker.Faker;

//import org.fluttercode.datafactory.impl.DataFactory;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.DeflateCompression;
import net.sf.jstuff.core.compression.GZipCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompressionTest extends TestCase {

    protected static final byte[] TEST_TEXT_BYTES;
    static {
        final StringBuilder sb = new StringBuilder();
        final Faker faker = new Faker(Locale.ENGLISH);
        for (int i = 0; i < 300; i++) {
            sb.append(//
                faker.firstName() + ", " + //
                        faker.lastName() + ", " + //
                        faker.streetAddress(true) + ", " + //
                        faker.cityPrefix() + faker.citySuffix() + ", " + //
                        faker.country() + ", " + //
                        faker.paragraph() + "\n" //
            );
        }
        TEST_TEXT_BYTES = sb.toString().getBytes();
    }

    @SuppressWarnings("resource")
    public static void testByteArrayCompression(final ByteArrayCompression cmp) throws IOException {
        for (int i = 0; i < 4; i++) { // testing instance re-use
            final byte[] compressed = cmp.compress(TEST_TEXT_BYTES);

            {
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

    public void testDeflate() throws IOException {
        testByteArrayCompression(DeflateCompression.INSTANCE);
        testIOStreamCompression(DeflateCompression.INSTANCE);
    }

    public void testGZip() throws IOException {
        testByteArrayCompression(GZipCompression.INSTANCE);
        testIOStreamCompression(GZipCompression.INSTANCE);
    }

    public void testSnappy() throws IOException {
        testByteArrayCompression(SnappyCompression.INSTANCE);
        testIOStreamCompression(SnappyCompression.INSTANCE);
    }
}
