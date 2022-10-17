/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.portlet;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import javax.portlet.RenderResponse;

import org.eclipse.jdt.annotation.NonNull;
import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("resource")
public class ContentCapturingRenderResponseWrapperTest {

   private static final Logger LOG = Logger.create();

   @Test
   public void testContentCapturingRenderResponseWrapper() throws UnsupportedEncodingException {

      final @NonNull String[] encoding = {"ISO-8859-1"};

      final RenderResponse mock = Types.createMixin(RenderResponse.class, new Object() {
         @SuppressWarnings("unused")
         public String getCharacterEncoding() {
            return encoding[0];
         }
      });

      {
         final var wrapper = new ContentCapturingRenderResponseWrapper(mock);
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

      final var strUTF = "20.00€";
      final var strISO_8859_1 = new String(strUTF.getBytes("ISO-8859-1"), "ISO-8859-1");
      final var strISO_8859_15 = new String(strUTF.getBytes("ISO-8859-15"), "ISO-8859-15");

      {
         final var wrapper = new ContentCapturingRenderResponseWrapper(mock);
         encoding[0] = "UTF-8";
         wrapper.getWriter().write(strUTF);
         LOG.info(encoding[0] + ": " + strUTF + " = " + wrapper.toString());
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1); // ISO-8859-1 does not have the Euro symbol
         assertThat(wrapper).hasToString(strISO_8859_15);
      }

      {
         final var wrapper = new ContentCapturingRenderResponseWrapper(mock);
         encoding[0] = "ISO-8859-1";
         wrapper.getWriter().write(strISO_8859_1);
         LOG.info(encoding[0] + ": " + strISO_8859_1 + " = " + wrapper.toString());
         assertThat(wrapper.toString()).isNotEqualTo(strUTF);
         assertThat(wrapper).hasToString(strISO_8859_1);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_15);
      }

      {
         final var wrapper = new ContentCapturingRenderResponseWrapper(mock);
         encoding[0] = "ISO-8859-15";
         wrapper.getWriter().write(strISO_8859_15);
         LOG.info(encoding[0] + ": " + strISO_8859_15 + " = " + wrapper.toString());
         assertThat(wrapper).hasToString(strUTF);
         assertThat(wrapper.toString()).isNotEqualTo(strISO_8859_1);
         assertThat(wrapper).hasToString(strISO_8859_15);
      }

   }

}
