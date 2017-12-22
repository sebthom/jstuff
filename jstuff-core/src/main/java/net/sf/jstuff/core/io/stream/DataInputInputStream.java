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
 * Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.io.stream;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DataInputInputStream extends InputStream {

    private final DataInput input;

    public DataInputInputStream(final DataInput input) {
        this.input = input;
    }

    @Override
    public int read() throws IOException {
        try {
            return input.readInt();
        } catch (final IndexOutOfBoundsException ex) {
            // e.g. in io.netty.buffer.AbstractByteBuf.readInt()
            return -1;
        } catch (final EOFException ex) {
            return -1;
        }
    }
}
