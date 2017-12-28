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
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.IOExceptionWithCause;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
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
    private byte[] dictionary = ArrayUtils.EMPTY_BYTE_ARRAY;

    private final ThreadLocal<Deflater> deflater = new ThreadLocal<Deflater>() {
        @Override
        public Deflater get() {
            final Deflater result = super.get();
            result.reset();
            return result;
        }

        @Override
        protected Deflater initialValue() {
            final Deflater def = new Deflater(compressionLevel);
            if (dictionary.length > 0) {
                def.setDictionary(dictionary);
            }
            return def;
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
            final Inflater inf = new Inflater(false);
            if (dictionary.length > 0) {
                inf.setDictionary(dictionary);
            }
            return inf;
        };
    };

    public DeflateCompression() {
    }

    public DeflateCompression(final int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public DeflateCompression(final int compressionLevel, final byte[] dictionary) {
        this.compressionLevel = compressionLevel;
        if (dictionary != null && dictionary.length > 0) {
            this.dictionary = dictionary.clone();
        }
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

    public int decompress(final byte[] compressed, final byte[] output) throws IOException {
        Args.notNull("compressed", compressed);
        Args.notNull("output", output);

        final Inflater inf = inflater.get();
        inf.setInput(compressed);
        try {
            final int bytesRead = inf.inflate(output);
            if (inf.getRemaining() > 0)
                throw new IndexOutOfBoundsException("[output] byte array of size " + output.length + " is too small for given input.");
            return bytesRead;
        } catch (final DataFormatException ex) {
            throw new IOExceptionWithCause(ex);
        }
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
