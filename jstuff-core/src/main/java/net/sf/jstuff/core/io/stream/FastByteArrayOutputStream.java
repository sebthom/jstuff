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
package net.sf.jstuff.core.io.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import net.sf.jstuff.core.validation.Args;

/**
 * An unsynchronized implementation of {@link java.io.ByteArrayOutputStream}.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayOutputStream extends OutputStream {

    protected byte[] data;
    protected int count;

    public FastByteArrayOutputStream() {
        data = new byte[32];
    }

    public FastByteArrayOutputStream(final int initialSize) {
        Args.notNegative("initialSize", initialSize);
        data = new byte[initialSize];
    }

    /**
     * Closing a {@link FastByteArrayOutputStream} has no effect.
     */
    @Override
    public void close() {
        // nothing to do
    }

    private void ensureCapacity(final int minCapacity) {
        // resizing needed?
        if (minCapacity <= data.length)
            return;

        // integer overflow?
        if (minCapacity < 0)
            throw new OutOfMemoryError("Cannot allocate array larger than " + Integer.MAX_VALUE);

        final int newCapacity = Math.max(data.length + (data.length >> 2) /* == data.length x 1.5 */, minCapacity);
        final byte copy[] = new byte[newCapacity];
        System.arraycopy(data, 0, copy, 0, count);
        data = copy;
    }

    /**
     * Flushing a {@link FastByteArrayOutputStream} has no effect.
     */
    @Override
    public void flush() {
        // nothing to do
    }

    public void reset() {
        count = 0;
    }

    public int size() {
        return count;
    }

    public byte[] toByteArray() {
        final byte copy[] = new byte[count];
        System.arraycopy(data, 0, copy, 0, count);
        return copy;
    }

    @Override
    public String toString() {
        return new String(data, 0, count);
    }

    public String toString(final String charsetName) throws UnsupportedEncodingException {
        return new String(data, 0, count, charsetName);
    }

    @Override
    public void write(final byte b[]) {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] buf, final int offset, final int length) {
        if (offset < 0 || length < 0 || offset + length > buf.length)
            throw new IndexOutOfBoundsException();

        if (length == 0)
            return;

        final int newcount = count + length;
        ensureCapacity(newcount);
        System.arraycopy(buf, offset, data, count, length);
        count = newcount;
    }

    @Override
    public void write(final int b) {
        final int newcount = count + 1;
        ensureCapacity(newcount);
        data[count] = (byte) b;
        count = newcount;
    }

    public void writeTo(final OutputStream out) throws IOException {
        out.write(data, 0, count);
    }
}
