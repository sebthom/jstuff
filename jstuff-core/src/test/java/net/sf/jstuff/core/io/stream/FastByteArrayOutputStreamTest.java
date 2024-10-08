/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class FastByteArrayOutputStreamTest {

   @Test
   @SuppressWarnings({"resource", "unused"})
   void testFastByteArrayOutputStream() throws IOException {

      try {
         new FastByteArrayOutputStream(-1);
         failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
      } catch (final IllegalArgumentException expected) {
         //expected
      }

      final var os = new FastByteArrayOutputStream(0);
      assertThat(os.size()).isZero();
      os.write(new byte[] {1, 2, 3});
      os.write(new byte[] {4, 5});
      os.write(6);
      assertThat(os.size()).isEqualTo(6);
      assertThat(os.toByteArray()).isEqualTo(new byte[] {1, 2, 3, 4, 5, 6});

      final var os2 = new FastByteArrayOutputStream();
      os.writeTo(os2);
      assertThat(os2.size()).isEqualTo(6);
      assertThat(os2.toByteArray()).isEqualTo(os2.toByteArray());

      os.reset();
      assertThat(os.size()).isZero();

      final String utf8text = "äÄüÜöÖß!€";
      os.write(utf8text.getBytes(UTF_8));
      assertThat(os.toString(UTF_8)).isEqualTo(utf8text);
      assertThat(os.toString(ISO_8859_1)).isNotEqualTo(utf8text);

      os.reset();
      os.write(utf8text.getBytes("ISO-8859-1"));
      assertThat(os.toString(UTF_8)).isNotEqualTo(utf8text);
      assertThat(os.toString(ISO_8859_1)).isNotEqualTo(utf8text); // € not part of ISO-8859-1

      os.reset();
      os.write(utf8text.getBytes("ISO-8859-15"));
      assertThat(os.toString(UTF_8)).isNotEqualTo(utf8text);
      assertThat(os.toString("ISO-8859-15")).isEqualTo(utf8text);
   }
}
