/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.portlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import javax.portlet.ResourceResponse;

import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("resource")
public class ContentCapturingResourceResponseWrapperTest {

   private static final Logger LOG = Logger.create();

   @Test
   @SuppressWarnings("unused")
   public void testContentCapturingResourceResponseWrapper() throws UnsupportedEncodingException {

      final String[] encoding = new String[] {"ISO-8859-1"};

      final ResourceResponse mock = Types.createMixin(ResourceResponse.class, new Object() {
         public String getCharacterEncoding() {
            return encoding[0];
         }
      });

      {
         final ContentCapturingResourceResponseWrapper wrapper = new ContentCapturingResourceResponseWrapper(mock);
         wrapper.getWriter().print("Hello");
         assertThat(wrapper).hasToString("Hello");
         wrapper.clear();
         assertEquals("", wrapper.toString());

         try {
            wrapper.getPortletOutputStream();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
         } catch (final IllegalStateException ex) {
            // expected
         }
      }

      final String strUTF = "20.00€";
      final String strISO_8859_1 = new String(strUTF.getBytes("ISO-8859-1"), "ISO-8859-1");
      final String strISO_8859_15 = new String(strUTF.getBytes("ISO-8859-15"), "ISO-8859-15");

      {
         final ContentCapturingResourceResponseWrapper wrapper = new ContentCapturingResourceResponseWrapper(mock);
         encoding[0] = "UTF-8";
         wrapper.getWriter().write(strUTF);
         LOG.info(encoding[0] + ": " + strUTF + " = " + wrapper.toString());
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1); // ISO-8859-1 does not have the Euro symbol
         assertThat(wrapper).hasToString(strISO_8859_15);
      }

      {
         final ContentCapturingResourceResponseWrapper wrapper = new ContentCapturingResourceResponseWrapper(mock);
         encoding[0] = "ISO-8859-1";
         wrapper.getWriter().write(strISO_8859_1);
         LOG.info(encoding[0] + ": " + strISO_8859_1 + " = " + wrapper.toString());
         assertThat(wrapper.toString()).isNotEqualTo(strUTF);
         assertThat(wrapper).hasToString(strISO_8859_1);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_15);
      }

      {
         final ContentCapturingResourceResponseWrapper wrapper = new ContentCapturingResourceResponseWrapper(mock);
         encoding[0] = "ISO-8859-15";
         wrapper.getWriter().write(strISO_8859_15);
         LOG.info(encoding[0] + ": " + strISO_8859_15 + " = " + wrapper.toString());
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1);
         assertThat(wrapper).hasToString(strISO_8859_15);
      }

   }

}
