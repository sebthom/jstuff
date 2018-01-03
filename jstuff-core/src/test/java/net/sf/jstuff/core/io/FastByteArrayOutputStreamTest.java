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
package net.sf.jstuff.core.io;

import java.io.IOException;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastByteArrayOutputStreamTest extends TestCase {

    @SuppressWarnings({ "resource", "unused" })
    public void testFastByteArrayOutputStream() throws IOException {

        try {
            new FastByteArrayOutputStream(-1);
            fail();
        } catch (final IllegalArgumentException expected) {
            //expected
        }

        final FastByteArrayOutputStream os = new FastByteArrayOutputStream(0);
        assertEquals(0, os.size());
        os.write(new byte[] { 1, 2, 3 });
        os.write(new byte[] { 4, 5 });
        os.write(6);
        assertEquals(6, os.size());
        assertTrue(ArrayUtils.isEquals(new byte[] { 1, 2, 3, 4, 5, 6 }, os.toByteArray()));

        final FastByteArrayOutputStream os2 = new FastByteArrayOutputStream();
        os.writeTo(os2);
        assertEquals(6, os2.size());
        assertTrue(ArrayUtils.isEquals(os.toByteArray(), os2.toByteArray()));

        os.reset();
        assertEquals(0, os.size());

        os.write("äÄüÜöÖß!€".getBytes("UTF-8"));
        assertEquals("äÄüÜöÖß!€", os.toString("UTF-8"));
        assertFalse("äÄüÜöÖß!€".equals(os.toString("ISO-8859-1")));

        os.reset();
        os.write("äÄüÜöÖß!€".getBytes("ISO-8859-1"));
        assertFalse("äÄüÜöÖß!€".equals(os.toString("UTF-8")));
        assertFalse("äÄüÜöÖß!€".equals(os.toString("ISO-8859-1"))); // € not part of ISO-8859-1

        os.reset();
        os.write("äÄüÜöÖß!€".getBytes("ISO-8859-15"));
        assertFalse("äÄüÜöÖß!€".equals(os.toString("UTF-8")));
        assertEquals("äÄüÜöÖß!€", os.toString("ISO-8859-15"));
    }
}
