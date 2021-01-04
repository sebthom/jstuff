/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceReaderTest {

   @Test
   @SuppressWarnings("resource")
   public void testCharSequenceReader() throws IOException {
      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader("Hello World!");
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }

      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader(new StringBuffer("Hello World!"));
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }

      {
         final char[] buf = new char[5];
         final CharSequenceReader r = new CharSequenceReader(new StringBuilder("Hello World!"));
         r.read(buf, 0, 5);
         assertThat(new String(buf).intern()).isEqualTo("Hello");
      }
   }
}
