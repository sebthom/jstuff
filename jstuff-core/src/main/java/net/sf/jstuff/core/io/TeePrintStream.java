/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.core.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TeePrintStream extends PrintStream {
    private final PrintStream original;

    /**
     * Constructs a TeePrintStream.
     * 
     * @param orig the main PrintStream
     * @param branch a file to tee the data to
     */
    public TeePrintStream(final PrintStream orig, final File branch) throws IOException {
        this(orig, branch, true);
    }

    /**
     * Constructs a TeePrintStream.
     * 
     * @param orig the main PrintStream
     * @param branch a file to tee the data to
     */
    @SuppressWarnings("resource")
    public TeePrintStream(final PrintStream orig, final File branch, final boolean autoFlush) throws IOException {
        this(orig, new BufferedOutputStream(new FileOutputStream(branch)), autoFlush);
    }

    /**
     * Constructs a TeePrintStream.
     * 
     * @param out the main PrintStream
     * @param branch the second OutputStream
     */
    public TeePrintStream(final PrintStream out, final OutputStream branch) {
        this(out, branch, true);
    }

    /**
     * Constructs a TeePrintStream.
     * 
     * @param out the main PrintStream
     * @param branch the second OutputStream
     */
    public TeePrintStream(final PrintStream out, final OutputStream branch, final boolean autoFlush) {
        super(branch, autoFlush);
        original = out;
    }

    @Override
    public boolean checkError() {
        return super.checkError() | original.checkError();
    }

    @Override
    public void close() {
        original.close();
        super.close();
    }

    @Override
    public void flush() {
        original.flush();
        super.flush();
    }

    @Override
    public void write(final byte buf[], final int off, final int len) {
        original.write(buf, off, len);
        super.write(buf, off, len);
    }

    @Override
    public void write(final int b) {
        original.write(b);
        super.write(b);
    }
}