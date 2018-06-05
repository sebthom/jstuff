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
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Compression {

    byte[] compress(byte[] uncompressed) throws IOException;

    /**
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void compress(final byte[] uncompressed, final OutputStream output, final boolean closeOutput) throws IOException;

    /**
     * @param uncompressed will be closed
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void compress(InputStream uncompressed, OutputStream output, boolean closeOutput) throws IOException;

    InputStream createCompressingInputStream(byte[] uncompressed) throws IOException;

    InputStream createCompressingInputStream(InputStream uncompressed) throws IOException;

    OutputStream createCompressingOutputStream(OutputStream output) throws IOException;

    InputStream createDecompressingInputStream(byte[] compressed) throws IOException;

    InputStream createDecompressingInputStream(InputStream compressed) throws IOException;

    byte[] decompress(byte[] compressed) throws IOException;

    int decompress(final byte[] compressed, final byte[] output) throws IOException;

    /**
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void decompress(final byte[] compressed, final OutputStream output, final boolean closeOutput) throws IOException;

    /**
     * @param compressed will be closed
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void decompress(InputStream compressed, OutputStream output, boolean closeOutput) throws IOException;
}
