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

import static net.sf.jstuff.core.Strings.NEW_LINE;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Extended BufferedOutputStream with write(String) and writeLine(String) methods.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BufferedOutputStreamExt extends BufferedOutputStream {
    /**
     * @param out the underlying output stream
     */
    public BufferedOutputStreamExt(final OutputStream out) {
        super(out);
    }

    /**
     * @param out the underlying output stream
     * @param size the buffer size
     * @throws IllegalArgumentException if size <= 0.
     */
    public BufferedOutputStreamExt(final OutputStream out, final int size) {
        super(out, size);
    }

    /**
     * Writes a string.
     *
     * @param str The <code>CharSequence</code> to be written.
     * @throws IOException - if an I/O error occurs
     */
    public void write(final CharSequence str) throws IOException {
        write(str.toString().getBytes());
    }

    /**
     * Terminate the current line by writing the line separator string. The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     *
     * @throws IOException - if an I/O error occurs
     */
    public void writeLine() throws IOException {
        write(NEW_LINE.getBytes());
    }

    /**
     * Writes a string and then terminates the line. This method behaves as
     * though it invokes <code>{@link #write(CharSequence)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param str The <code>CharSequence</code> to be written.
     * @throws IOException - if an I/O error occurs
     */
    public void writeLine(final CharSequence str) throws IOException {
        write(str);
        writeLine();
    }
}
