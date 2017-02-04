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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.FastByteArrayOutputStream;

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
            if (Strings.isEmpty(encoding))
                return outputStream.toString("ISO-8859-1");

            return outputStream.toString(encoding);
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() {
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
        if (exposedPrintWriter == null) {
            exposedPrintWriter = new PrintWriter(outputStream, true);
        }
        return exposedPrintWriter;
    }

}