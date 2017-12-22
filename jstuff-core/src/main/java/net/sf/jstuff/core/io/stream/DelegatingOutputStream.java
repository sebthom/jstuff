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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingOutputStream extends OutputStream {

    protected OutputStream delegate;
    protected boolean ignoreClose = false;

    public DelegatingOutputStream(final OutputStream delegate) {
        this.delegate = delegate;
    }

    public DelegatingOutputStream(final OutputStream delegate, final boolean ignoreClose) {
        this.delegate = delegate;
        this.ignoreClose = ignoreClose;
    }

    @Override
    public void close() throws IOException {
        if (!ignoreClose) {
            delegate.close();
        }
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    public OutputStream getDelegate() {
        return delegate;
    }

    public boolean isIgnoreClose() {
        return ignoreClose;
    }

    public void setDelegate(final OutputStream delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreClose(final boolean ignoreClose) {
        this.ignoreClose = ignoreClose;
    }

    @Override
    public void write(final byte[] bytes) throws IOException {
        delegate.write(bytes);
    }

    @Override
    public void write(final byte[] bytes, final int i, final int i1) throws IOException {
        delegate.write(bytes, i, i1);
    }

    @Override
    public void write(final int i) throws IOException {
        delegate.write(i);
    }

    public void writeByte(final byte b) throws IOException {
        delegate.write(new byte[] { b });
    }
}