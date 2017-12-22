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
package net.sf.jstuff.integration.portlet;

import java.io.UnsupportedEncodingException;

import javax.portlet.RenderResponse;

import junit.framework.TestCase;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ContentCapturingRenderResponseWrapperTest extends TestCase {

    private static final Logger LOG = Logger.create();

    @SuppressWarnings("unused")
    public void testContentCapturingRenderResponseWrapper() throws UnsupportedEncodingException {

        final String[] encoding = new String[] { "ISO-8859-1" };

        final RenderResponse mock = Types.createMixin(RenderResponse.class, new Object() {

            public String getCharacterEncoding() {
                return encoding[0];
            }
        });

        {
            final ContentCapturingRenderResponseWrapper wrapper = new ContentCapturingRenderResponseWrapper(mock);
            wrapper.getWriter().print("Hello");
            assertEquals("Hello", wrapper.toString());
            wrapper.clear();
            assertEquals("", wrapper.toString());

            try {
                wrapper.getPortletOutputStream();
                fail();
            } catch (final IllegalStateException ex) {
                // expected
            }
        }

        final String strUTF = "20.00€";
        final String strISO_8859_1 = new String(strUTF.getBytes("ISO-8859-1"), "ISO-8859-1");
        final String strISO_8859_15 = new String(strUTF.getBytes("ISO-8859-15"), "ISO-8859-15");

        {
            final ContentCapturingRenderResponseWrapper wrapper = new ContentCapturingRenderResponseWrapper(mock);
            encoding[0] = "UTF-8";
            wrapper.getWriter().write(strUTF);
            LOG.info(encoding[0] + ": " + strUTF + " = " + wrapper.toString());
            assertEquals(strUTF, wrapper.toString());
            assertFalse(strISO_8859_1.equals(wrapper.toString())); // ISO-8859-1 does not have the Euro symbol
            assertEquals(strISO_8859_15, wrapper.toString());
        }

        {
            final ContentCapturingRenderResponseWrapper wrapper = new ContentCapturingRenderResponseWrapper(mock);
            encoding[0] = "ISO-8859-1";
            wrapper.getWriter().write(strISO_8859_1);
            LOG.info(encoding[0] + ": " + strISO_8859_1 + " = " + wrapper.toString());
            assertFalse(strUTF.equals(wrapper.toString()));
            assertEquals(strISO_8859_1, wrapper.toString());
            assertFalse(strISO_8859_15.equals(wrapper.toString()));
        }

        {
            final ContentCapturingRenderResponseWrapper wrapper = new ContentCapturingRenderResponseWrapper(mock);
            encoding[0] = "ISO-8859-15";
            wrapper.getWriter().write(strISO_8859_15);
            LOG.info(encoding[0] + ": " + strISO_8859_15 + " = " + wrapper.toString());
            assertEquals(strUTF, wrapper.toString());
            assertFalse(strISO_8859_1.equals(wrapper.toString()));
            assertEquals(strISO_8859_15, wrapper.toString());
        }

    }

}
