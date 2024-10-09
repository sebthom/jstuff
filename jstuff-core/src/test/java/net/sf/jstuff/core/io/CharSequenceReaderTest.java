/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CharSequenceReaderTest {

   @Test
   @SuppressWarnings("resource")
   void testCharSequenceReader() throws IOException {
      {
         final var buf = new char[5];
         final var r = new CharSequenceReader("Hello World!");
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }

      {
         final var buf = new char[5];
         final var r = new CharSequenceReader(new StringBuffer("Hello World!"));
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }

      {
         final var buf = new char[5];
         final var r = new CharSequenceReader(new StringBuilder("Hello World!"));
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }
   }
}
