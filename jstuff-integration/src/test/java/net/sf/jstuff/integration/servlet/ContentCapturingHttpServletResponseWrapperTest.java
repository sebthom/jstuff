/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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

      final String[] encoding = new String[] {"ISO-8859-1"};

      final HttpServletResponse mock = Types.createMixin(HttpServletResponse.class, new Object() {
         public String getCharacterEncoding() {
            return encoding[0];
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
         encoding[0] = "UTF-8";
         wrapper.getWriter().write(strUTF);
         LOG.info(encoding[0] + ": " + strUTF + " = " + wrapper.toString());
         assertEquals(strUTF, wrapper.toString());
         assertFalse(strISO_8859_1.equals(wrapper.toString())); // ISO-8859-1 does not have the Euro symbol
         assertEquals(strISO_8859_15, wrapper.toString());
      }

      {
         final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
         encoding[0] = "ISO-8859-1";
         wrapper.getWriter().write(strISO_8859_1);
         LOG.info(encoding[0] + ": " + strISO_8859_1 + " = " + wrapper.toString());
         assertFalse(strUTF.equals(wrapper.toString()));
         assertEquals(strISO_8859_1, wrapper.toString());
         assertFalse(strISO_8859_15.equals(wrapper.toString()));
      }

      {
         final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
         encoding[0] = "ISO-8859-15";
         wrapper.getWriter().write(strISO_8859_15);
         LOG.info(encoding[0] + ": " + strISO_8859_15 + " = " + wrapper.toString());
         assertEquals(strUTF, wrapper.toString());
         assertFalse(strISO_8859_1.equals(wrapper.toString()));
         assertEquals(strISO_8859_15, wrapper.toString());
      }

   }

}
