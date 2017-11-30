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
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * Use {{@link #toString()} to get the response as string.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ContentCapturingHttpServletResponseWrapper extends StatusCapturingHttpServletResponseWrapper {
    private ServletOutputStream exposedOutputStream;
    private PrintWriter exposedPrintWriter;
    private final FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();

    public ContentCapturingHttpServletResponseWrapper(final HttpServletResponse response) {
        super(response);
    }

    public void clear() {
        outputStream.reset();
    }

    public byte[] toByteArray() {
        return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        if (exposedPrintWriter != null) {
            exposedPrintWriter.flush();
        }

        try {
            final String encoding = getCharacterEncoding();
            return outputStream.toString(encoding);
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (exposedPrintWriter != null)
            throw new IllegalStateException("getWriter() was called already!");

        if (exposedOutputStream == null) {
            exposedOutputStream = new ServletOutputStream() {
                @Override
                public void write(final byte[] b) throws IOException {
                    outputStream.write(b);
                }

                @Override
                public void write(final byte[] b, final int off, final int len) throws IOException {
                    outputStream.write(b, off, len);
                }

                @Override
                public void write(final int b) throws IOException {
                    outputStream.write(b);
                }
            };
        }
        return exposedOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (exposedOutputStream != null)
            throw new IllegalStateException("getOutpuStream() was called already!");

        if (exposedPrintWriter == null) {

            exposedPrintWriter = new PrintWriter(new Writer() {
                @Override
                public void write(final String str) throws IOException {
                    outputStream.write(str.getBytes(getCharacterEncoding()));
                }

                @Override
                public void write(final char[] cbuf, final int off, final int len) throws IOException {
                    outputStream.write(new String(cbuf, off, len).getBytes(getCharacterEncoding()));
                }

                @Override
                public void flush() throws IOException {
                }

                @Override
                public void close() throws IOException {
                }
            }) {
                @Override
                public void write(final String str) {
                    try {
                        outputStream.write(str.getBytes(getCharacterEncoding()));
                    } catch (final UnsupportedEncodingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };

            /* 3x slower:
            try {
                exposedPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, getCharacterEncoding())), false);
            } catch (final UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            } */
        }

        return exposedPrintWriter;
    }

}