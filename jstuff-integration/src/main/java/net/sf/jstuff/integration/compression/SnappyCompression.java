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
import org.xerial.snappy.Snappy;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.compression.ByteArrayCompression;
import net.sf.jstuff.core.compression.InputStreamCompression;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SnappyCompression implements ByteArrayCompression, InputStreamCompression {

    public static final SnappyCompression INSTANCE = new SnappyCompression();

    public byte[] compress(final byte[] uncompressed) throws IOException {
        Args.notNull("uncompressed", uncompressed);

        return Snappy.rawCompress(uncompressed, uncompressed.length);
    }

    public void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("uncompressed", uncompressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final SnappyOutputStream compOS = new SnappyOutputStream(output);
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
            final SnappyOutputStream compOS = new SnappyOutputStream(output);
            IOUtils.copy(input, compOS);
            compOS.flush();
        } finally {
            IOUtils.closeQuietly(input);
            if (closeOutput) {
                IOUtils.closeQuietly(output);
            }
        }
    }

    public byte[] decompress(final byte[] compressed) throws IOException {
        Args.notNull("compressed", compressed);

        return Snappy.uncompress(compressed);
    }

    public int decompress(final byte[] compressed, final byte[] output) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        final int uncompressedSize = Snappy.rawUncompress(compressed, 0, compressed.length, output, 0);

        if (uncompressedSize != output.length)
            throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");

        return uncompressedSize;
    }

    public void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        try {
            @SuppressWarnings("resource")
            final SnappyInputStream compIS = new SnappyInputStream(new FastByteArrayInputStream(compressed));
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
            final SnappyInputStream compIS = new SnappyInputStream(input);
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
        return Strings.toString(this, new Object[0]);
    }
}
