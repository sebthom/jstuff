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

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ContentCapturingHttpServletResponseWrapperTest extends TestCase {

    private static final Logger LOG = Logger.create();

    @SuppressWarnings("unused")
    public void testContentCapturingHttpServletResponseWrapper() throws UnsupportedEncodingException {

        final HttpServletResponse mock = Types.createMixin(HttpServletResponse.class, new Object() {

            private String encoding = "ISO-8859-1";

            public String getCharacterEncoding() {
                return encoding;
            }

            public void setCharacterEncoding(final String encoding) {
                this.encoding = encoding;
            }
        });

        {
            final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
            wrapper.getWriter().print("Hello");
            assertEquals("Hello", wrapper.toString());
            wrapper.clear();
            assertEquals("", wrapper.toString());

            try {
                wrapper.getOutputStream();
                fail();
            } catch (final IllegalStateException ex) {
                // expected
            }
        }

        final String strUTF = "20.00€";
        final String strISO_8859_1 = new String(strUTF.getBytes("ISO-8859-1"), "ISO-8859-1");
        final String strISO_8859_15 = new String(strUTF.getBytes("ISO-8859-15"), "ISO-8859-15");

        {
            final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
            mock.setCharacterEncoding("UTF-8");
            wrapper.getWriter().write(strUTF);
            LOG.info("UTF-8: " + strUTF + " = " + wrapper.toString());
            assertEquals(strUTF, wrapper.toString());
            assertFalse(strISO_8859_1.equals(wrapper.toString())); // ISO-8859-1 does not have the Euro symbol
            assertEquals(strISO_8859_15, wrapper.toString());
        }

        {
            final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
            mock.setCharacterEncoding("ISO-8859-1");
            wrapper.getWriter().write(strISO_8859_1);
            LOG.info("ISO-8859-1: " + strISO_8859_1 + " = " + wrapper.toString());
            assertFalse(strUTF.equals(wrapper.toString()));
            assertEquals(strISO_8859_1, wrapper.toString());
            assertFalse(strISO_8859_15.equals(wrapper.toString()));
        }

        {
            final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
            mock.setCharacterEncoding("ISO-8859-15");
            wrapper.getWriter().write(strISO_8859_15);
            LOG.info("ISO-8859-15: " + strISO_8859_15 + " = " + wrapper.toString());
            assertEquals(strUTF, wrapper.toString());
            assertFalse(strISO_8859_1.equals(wrapper.toString()));
            assertEquals(strISO_8859_15, wrapper.toString());
        }

    }

}
