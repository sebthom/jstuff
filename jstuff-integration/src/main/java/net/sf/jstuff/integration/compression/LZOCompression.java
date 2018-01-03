/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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

import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoConstraint;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoInputStream;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzoOutputStream;
import org.apache.commons.io.IOUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LZOCompression implements ByteArrayCompression, InputStreamCompression {

    public static final LZOCompression INSTANCE = new LZOCompression();

    private final LzoCompressor compressor;
    private final LzoDecompressor decompressor;

    public LZOCompression() {
        this(LzoAlgorithm.LZO1X, null);
    }

    public LZOCompression(final LzoAlgorithm algorithm) {
        this(algorithm, null);
    }

    public LZOCompression(final LzoAlgorithm algorithm, final LzoConstraint constraint) {
        Args.notNull("algorithm", algorithm);
        compressor = LzoLibrary.getInstance().newCompressor(algorithm, constraint);
        decompressor = LzoLibrary.getInstance().newDecompressor(algorithm, constraint);
    }

    @SuppressWarnings("resource")

    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        final LzoOutputStream compOS = new LzoOutputStream(baos, compressor, uncompressed.length);
        IOUtils.copyLarge(new FastByteArrayInputStream(uncompressed), compOS);
        compOS.flush();

        return baos.toByteArray();
    }

    public void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LzoOutputStream compOS = new LzoOutputStream(output, compressor, uncompressed.length);
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
            final LzoOutputStream compOS = new LzoOutputStream(output, compressor, 32 * 1024);
            IOUtils.copyLarge(input, compOS);
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
        final LzoInputStream compIS = new LzoInputStream(new FastByteArrayInputStream(compressed), decompressor);
        IOUtils.copyLarge(compIS, baos);
        return baos.toByteArray();
    }

    @SuppressWarnings("resource")
    public int decompress(final byte[] compressed, final byte[] output) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        // doesn't work: return decompressor.decompress(compressed, 0, compressed.length, output, 0, new lzo_uintp(output.length));
        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream(compressed.length);
        final LzoInputStream compIS = new LzoInputStream(new FastByteArrayInputStream(compressed), decompressor);
        IOUtils.copyLarge(compIS, baos);
        if (baos.size() > output.length)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
        baos.writeTo(output);
        return baos.size();
    }

    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LzoInputStream compIS = new LzoInputStream(new FastByteArrayInputStream(compressed), decompressor);
            IOUtils.copyLarge(compIS, output);
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
            final LzoInputStream compIS = new LzoInputStream(input, decompressor);
            IOUtils.copyLarge(compIS, output);
        } finally {
            IOUtils.closeQuietly(input);
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @Override
    public String toString() {
        return Strings.toString(this, "algorithm", compressor.getAlgorithm());
    }
}
