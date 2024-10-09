/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */

package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class Base64Test {

   @Test
   void testDecode() {

      /*
       * Base64.decode( //
       * "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM9UjXquRajbqlgDPp+lb7WegOSR/Tmm\nl56S7aTanVvR1sBVWUQZMPpspRuEHZw+FF4Zb2utZnMLdQk3ZL9nCVMCAwEAAQ=="
       * );
       */
      assertThat(new String(Base64.decode("SGVsbG8gV29ybGQh"), StandardCharsets.UTF_8)).isEqualTo("Hello World!");
      assertThat(new String(Base64.decode("SGVsbG8g\nV29ybGQh"), StandardCharsets.UTF_8)).isEqualTo("Hello World!");
      assertThat(new String(Base64.decode("SGVsbA=="), StandardCharsets.UTF_8)).isEqualTo("Hell");
      assertThat(new String(Base64.decode("SGVsbA="), StandardCharsets.UTF_8)).isEqualTo("Hell");
      assertThat(new String(Base64.decode("SGVsbA"), StandardCharsets.UTF_8)).isEqualTo("Hell");
      assertThat(Base64.decode("")).isEmpty();
      assertThat(Base64.decode(new byte[0])).isEmpty();
      assertThat(Base64.urldecode("A_")).isEqualTo(Base64.decode("A/"));
      assertThat(Base64.urldecode("A-")).isEqualTo(Base64.decode("A+"));
      assertThat(Base64.urldecode("A-")).isEqualTo(Base64.decode("A+="));
      assertThat(Base64.urldecode("A-=")).isEqualTo(Base64.decode("A+=="));
      try {
         System.out.println(new String(Base64.decode("ÖÄÜ")));
         failBecauseExceptionWasNotThrown(Exception.class);
      } catch (final Exception ex) {
         // expected
      }
   }
}
