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

import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
class LZ4FrameCompression implements ByteArrayCompression, InputStreamCompression {

    public static final LZ4FrameCompression INSTANCE = new LZ4FrameCompression();

    // TODO private static final LZ4Compressor COMP = LZ4Factory.fastestInstance().fastCompressor(); // requires https://github.com/lz4/lz4-java/pull/113
    private static final LZ4SafeDecompressor DECOMP = LZ4Factory.fastestInstance().safeDecompressor();
    private static final XXHash32 CHECKSUM = XXHashFactory.fastestInstance().hash32();

    @SuppressWarnings("resource")
    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        final LZ4FrameOutputStream compOS = new LZ4FrameOutputStream(baos, LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB, -1L, /* TODO: COMP, CHECKSUM, */
            LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE);
        IOUtils.copyLarge(new FastByteArrayInputStream(uncompressed), compOS);
        compOS.close(); // writes end-mark

        return baos.toByteArray();
    }

    @SuppressWarnings("resource")
    public void compress(final byte[] uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        if (!closeOutput) {
            output = new DelegatingOutputStream(output, true);
        }

        try {
            final LZ4FrameOutputStream compOS = new LZ4FrameOutputStream(output, LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB, -1L, /* TODO: COMP, CHECKSUM, */
                LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE);
            compOS.write(uncompressed);
            compOS.close(); // writes end-mark
        } finally {
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @SuppressWarnings("resource")
    public void compress(final InputStream input, OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        if (!closeOutput) {
            output = new DelegatingOutputStream(output, true);
        }

        try {
            final LZ4FrameOutputStream compOS = new LZ4FrameOutputStream(output, LZ4FrameOutputStream.BLOCKSIZE.SIZE_64KB, -1L, /* TODO: COMP, CHECKSUM, */
                LZ4FrameOutputStream.FLG.Bits.BLOCK_INDEPENDENCE);
            IOUtils.copyLarge(input, compOS);
            compOS.close(); // writes end-mark
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
        final LZ4FrameInputStream compIS = new LZ4FrameInputStream(new FastByteArrayInputStream(compressed), DECOMP, CHECKSUM);
        IOUtils.copyLarge(compIS, baos);
        return baos.toByteArray();
    }

    @SuppressWarnings("resource")
    public int decompress(final byte[] compressed, final byte[] output) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        final FastByteArrayOutputStream baos = new FastByteArrayOutputStream(compressed.length);
        final LZ4FrameInputStream compIS = new LZ4FrameInputStream(new FastByteArrayInputStream(compressed), DECOMP, CHECKSUM);
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
            final LZ4FrameInputStream compIS = new LZ4FrameInputStream(new FastByteArrayInputStream(compressed), DECOMP, CHECKSUM);
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
            final LZ4FrameInputStream compIS = new LZ4FrameInputStream(input, DECOMP, CHECKSUM);
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
        return Strings.toString(this, new Object[0]);
    }

}
