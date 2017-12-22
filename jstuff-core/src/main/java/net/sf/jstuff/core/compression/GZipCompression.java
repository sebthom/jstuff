/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Compress/decompress using "gzip" compression format.
 *
 * GZip is deflate plus CRC32 checksum
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GZipCompression implements ByteArrayCompression, InputStreamCompression {

    public static final GZipCompression INSTANCE = new GZipCompression();

    /**
     * https://www.rootusers.com/gzip-vs-bzip2-vs-xz-performance-comparison/
     */
    private int compressionLevel = 4;

    public GZipCompression() {
    }

    public GZipCompression(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @SuppressWarnings("resource")
    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream();
        final GZIPOutputStream compOS = new GZIPOutputStream(bytesOS) {
            {
                def.setLevel(compressionLevel);
            }
        };
        compOS.write(uncompressed);
        compOS.close();
        return bytesOS.toByteArray();
    }

    public void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        try {
            final GZIPOutputStream compOS = new GZIPOutputStream(output) {
                {
                    def.setLevel(compressionLevel);
                }
            };
            compOS.write(uncompressed);
            compOS.finish();
        } finally {
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    public void compress(final InputStream input, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final GZIPOutputStream compOS = new GZIPOutputStream(output) {
                {
                    def.setLevel(compressionLevel);
                }
            };
            IOUtils.copy(input, compOS);
            compOS.finish();
        } finally {
            IOUtils.closeQuietly(input);
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @SuppressWarnings("resource")
    public byte[] decompress(final byte[] compressed) throws IOException {
        Args.notNull("compressed", compressed);

        final GZIPInputStream compIS = new GZIPInputStream(new FastByteArrayInputStream(compressed));
        final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream();
        IOUtils.copy(compIS, bytesOS);
        return bytesOS.toByteArray();
    }

    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final GZIPInputStream compIS = new GZIPInputStream(new FastByteArrayInputStream(compressed));
            IOUtils.copy(compIS, output);
        } finally {
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    public void decompress(final InputStream input, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final GZIPInputStream compIS = new GZIPInputStream(input);
            IOUtils.copy(compIS, output);
        } finally {
            IOUtils.closeQuietly(input);
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @Override
    public String toString() {
        return Strings.toString(this, "compressionLevel", compressionLevel);
    }
}
