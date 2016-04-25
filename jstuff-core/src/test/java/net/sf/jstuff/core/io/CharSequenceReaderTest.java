/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReaderTest extends TestCase {

    @SuppressWarnings("resource")
    public void testCharSequenceReader() throws IOException {
        {
            final char[] buf = new char[5];
            final CharSequenceReader r = new CharSequenceReader("Hello World!");
            r.read(buf, 0, 5);
            assertEquals("Hello", new String(buf).intern());
        }

        {
            final char[] buf = new char[5];
            final CharSequenceReader r = new CharSequenceReader(new StringBuffer("Hello World!"));
            r.read(buf, 0, 5);
            assertEquals("Hello", new String(buf).intern());
        }

        {
            final char[] buf = new char[5];
            final CharSequenceReader r = new CharSequenceReader(new StringBuilder("Hello World!"));
            r.read(buf, 0, 5);
            assertEquals("Hello", new String(buf).intern());
        }
    }
}
