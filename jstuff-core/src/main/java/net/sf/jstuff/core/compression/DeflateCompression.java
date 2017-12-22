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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Compress/decompress using "deflate" compression format.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DeflateCompression implements ByteArrayCompression, InputStreamCompression {

    public static final DeflateCompression INSTANCE = new DeflateCompression();

    /**
     * https://www.rootusers.com/gzip-vs-bzip2-vs-xz-performance-comparison/
     */
    private int compressionLevel = 4;

    private final ThreadLocal<Deflater> deflater = new ThreadLocal<Deflater>() {
        @Override
        public Deflater get() {
            final Deflater result = super.get();
            result.reset();
            return result;
        }

        @Override
        protected Deflater initialValue() {
            return new Deflater(compressionLevel);
        };
    };
    private final ThreadLocal<Inflater> inflater = new ThreadLocal<Inflater>() {
        @Override
        public Inflater get() {
            final Inflater result = super.get();
            result.reset();
            return result;
        }

        @Override
        protected Inflater initialValue() {
            return new Inflater(false);
        };
    };

    public DeflateCompression() {
    }

    public DeflateCompression(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream();
        final DeflaterOutputStream compOS = new DeflaterOutputStream(bytesOS, deflater.get());
        compOS.write(uncompressed);
        compOS.close();
        return bytesOS.toByteArray();

    }

    public void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        try {
            final DeflaterOutputStream compOS = new DeflaterOutputStream(output, deflater.get());
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
            final DeflaterOutputStream compOS = new DeflaterOutputStream(output, deflater.get());
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

        final InflaterInputStream compIS = new InflaterInputStream(new FastByteArrayInputStream(compressed), inflater.get());
        final FastByteArrayOutputStream bytesOS = new FastByteArrayOutputStream();
        IOUtils.copy(compIS, bytesOS);
        return bytesOS.toByteArray();
    }

    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final InflaterInputStream compIS = new InflaterInputStream(new FastByteArrayInputStream(compressed), inflater.get());
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
            final InflaterInputStream compIS = new InflaterInputStream(input, inflater.get());
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
