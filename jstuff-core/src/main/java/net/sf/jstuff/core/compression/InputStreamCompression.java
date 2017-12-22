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
package net.sf.jstuff.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface InputStreamCompression {

    /**
     * @param input will be closed
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void compress(InputStream input, OutputStream output, boolean closeOutput) throws IOException;

    /**
     * @param input will be closed
     * @param closeOutput if output shall be closed, if false output may also not be flushed automatically
     */
    void decompress(InputStream input, OutputStream output, boolean closeOutput) throws IOException;
}
