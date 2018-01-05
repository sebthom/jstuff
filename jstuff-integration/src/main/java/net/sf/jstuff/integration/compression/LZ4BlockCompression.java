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
import java.util.zip.Checksum;

import org.apache.commons.io.IOUtils;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.xxhash.XXHashFactory;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.AbstractCompression;
import net.sf.jstuff.core.io.stream.DelegatingOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LZ4BlockCompression extends AbstractCompression {

    public static final LZ4BlockCompression INSTANCE = new LZ4BlockCompression();

    private static final int DEFAULT_BLOCK_SIZE = 64 * 1024;

    private static final LZ4Compressor COMP = LZ4Factory.fastestInstance().fastCompressor();
    private static final LZ4FastDecompressor DECOMP = LZ4Factory.fastestInstance().fastDecompressor();
    private static final ThreadLocal<Checksum> CHECKSUM = new ThreadLocal<Checksum>() {

        @Override
        protected Checksum initialValue() {
            return XXHashFactory.fastestInstance().newStreamingHash32(0x9747b28c).asChecksum();
        }
    };

    @SuppressWarnings("resource")
    public void compress(final byte[] uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        final int blockSize = uncompressed.length >= DEFAULT_BLOCK_SIZE ? DEFAULT_BLOCK_SIZE : uncompressed.length < 65 ? 64 : uncompressed.length;

        if (!closeOutput) {
            // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
            output = new DelegatingOutputStream(output, true);
        }
        try {
            final LZ4BlockOutputStream compOS = new LZ4BlockOutputStream(output, blockSize, COMP, CHECKSUM.get(), false);
            compOS.write(uncompressed);
            compOS.finish();
        } finally {
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    @SuppressWarnings("resource")
    public void compress(final InputStream uncompressed, OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        if (!closeOutput) {
            // prevent unwanted closing of output in case compOS has a finalize method that closes underlying resource on GC
            output = new DelegatingOutputStream(output, true);
        }

        try {
            final LZ4BlockOutputStream compOS = new LZ4BlockOutputStream(output, DEFAULT_BLOCK_SIZE, COMP, CHECKSUM.get(), false);
            IOUtils.copyLarge(uncompressed, compOS);
            compOS.finish();
        } finally {
            IOUtils.closeQuietly(uncompressed);
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    public OutputStream createCompressingOutputStream(final OutputStream output) throws IOException {
        return new LZ4BlockOutputStream(output, DEFAULT_BLOCK_SIZE, COMP);
    }

    public InputStream createDecompressingInputStream(final InputStream compressed) throws IOException {
        return new LZ4BlockInputStream(compressed, DECOMP);
    }

    @Override
    public void decompress(final InputStream input, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("input", input);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final LZ4BlockInputStream compIS = new LZ4BlockInputStream(input, DECOMP, CHECKSUM.get());
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
