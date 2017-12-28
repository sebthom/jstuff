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
package net.sf.jstuff.integration.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZStdCompression implements ByteArrayCompression, InputStreamCompression {

    public static final ZStdCompression INSTANCE = new ZStdCompression();

    private int compressionLevel = 3;

    public ZStdCompression() {
    }

    public ZStdCompression(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final long maxSize = Zstd.compressBound(uncompressed.length);
        if (maxSize > Integer.MAX_VALUE)
            throw new IOException("Max output size is greater than Integer.MAX_VALUE!");
        final byte[] dst = new byte[(int) maxSize];

        final long rc = Zstd.compress(dst, uncompressed, compressionLevel);
        if (Zstd.isError(rc))
            throw new IOException(Zstd.getErrorName(rc));

        final int size = (int) rc;
        final byte[] out = new byte[size];
        System.arraycopy(dst, 0, out, 0, size);
        return out;
    }

    public void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final ZstdOutputStream compOS = new ZstdOutputStream(output, compressionLevel, true);
            compOS.write(uncompressed);
            compOS.flush();
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
            final ZstdOutputStream compOS = new ZstdOutputStream(output, compressionLevel, true);
            IOUtils.copy(input, compOS);
            compOS.flush();
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

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream(compressed.length);
        final ZstdInputStream compIS = new ZstdInputStream(new FastByteArrayInputStream(compressed));
        IOUtils.copy(compIS, baos);
        return baos.toByteArray();
    }

    public int decompress(final byte[] compressed, final byte[] output) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        final long rc = Zstd.decompress(output, compressed);
        if (Zstd.isError(rc)) {
            if (rc == -70)
                throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
            throw new IOException(Zstd.getErrorName(rc));
        }
        return (int) rc;
    }

    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final ZstdInputStream compIS = new ZstdInputStream(new FastByteArrayInputStream(compressed));
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
            final ZstdInputStream compIS = new ZstdInputStream(input);
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
