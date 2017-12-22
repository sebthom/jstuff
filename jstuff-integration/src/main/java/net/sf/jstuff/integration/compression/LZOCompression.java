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
    @Override
    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        final LzoOutputStream compOS = new LzoOutputStream(baos, compressor, uncompressed.length);
        IOUtils.copy(new FastByteArrayInputStream(uncompressed), compOS);
        compOS.flush();

        return baos.toByteArray();
    }

    @Override
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

    @Override
    public void compress(final InputStream input, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LzoOutputStream compOS = new LzoOutputStream(output, compressor, 32 * 1024);
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
    @Override
    public byte[] decompress(final byte[] compressed) throws IOException {
        Args.notNull("compressed", compressed);

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream(compressed.length);
        final LzoInputStream compIS = new LzoInputStream(new FastByteArrayInputStream(compressed), decompressor);
        IOUtils.copy(compIS, baos);
        return baos.toByteArray();
    }

    @Override
    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LzoInputStream compIS = new LzoInputStream(new FastByteArrayInputStream(compressed), decompressor);
            IOUtils.copy(compIS, output);
        } finally {
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @Override
    public void decompress(final InputStream input, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LzoInputStream compIS = new LzoInputStream(input, decompressor);
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
        return Strings.toString(this, "algorithm", compressor.getAlgorithm());
    }
}
