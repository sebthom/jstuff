/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("resource")
public class ContentCapturingHttpServletResponseWrapperTest {

   private static final Logger LOG = Logger.create();

   @Test
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
         assertThat(wrapper).hasToString("Hello");
         wrapper.clear();
         assertEquals("", wrapper.toString());

         try {
            wrapper.getOutputStream();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
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
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1); // ISO-8859-1 does not have the Euro symbol
         assertThat(wrapper).hasToString(strISO_8859_15);
      }

      {
         final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
         encoding[0] = "ISO-8859-1";
         wrapper.getWriter().write(strISO_8859_1);
         LOG.info(encoding[0] + ": " + strISO_8859_1 + " = " + wrapper.toString());
         assertThat(wrapper.toString()).isNotEqualTo(strUTF);
         assertThat(wrapper).hasToString(strISO_8859_1);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_15);
      }

      {
         final ContentCapturingHttpServletResponseWrapper wrapper = new ContentCapturingHttpServletResponseWrapper(mock);
         encoding[0] = "ISO-8859-15";
         wrapper.getWriter().write(strISO_8859_15);
         LOG.info(encoding[0] + ": " + strISO_8859_15 + " = " + wrapper.toString());
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1);
         assertThat(wrapper).hasToString(strISO_8859_15);
      }
   }
}
