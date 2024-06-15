/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CharSequenceInputStreamTest {

   private static final int BUFFER_SIZE = 4;
   private static final StringBuilder TEST_CHARSEQ = new StringBuilder("Hello, World!");

   private final CharSequenceInputStream charseqIS = new CharSequenceInputStream(TEST_CHARSEQ, BUFFER_SIZE);

   @Test
   public void testAvailable() throws IOException {
      assertThat(charseqIS.available()).isEqualTo(TEST_CHARSEQ.length());

      final byte[] buffer = new byte[BUFFER_SIZE];
      charseqIS.read(buffer);
      assertThat(charseqIS.available()).isEqualTo(TEST_CHARSEQ.length() - BUFFER_SIZE);
   }

   @Test
   public void testMarkAndReset() throws IOException {
      final byte[] buffer = new byte[BUFFER_SIZE];

      // Read initial data
      int bytesRead = charseqIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Hell");

      // Mark the current position
      charseqIS.mark(0);

      // Read more data
      bytesRead = charseqIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("o, W");

      // Reset to the marked position
      charseqIS.reset();

      // Read again from the marked position
      bytesRead = charseqIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("o, W");
   }

   @Test
   public void testRead() throws IOException {
      final byte[] buffer = new byte[BUFFER_SIZE];
      final int bytesRead = charseqIS.read(buffer);

      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Hell");
   }

   @Test
   public void testReadToEnd() throws IOException {
      final byte[] buffer = new byte[TEST_CHARSEQ.length()];
      charseqIS.read(buffer);

      assertThat(new String(buffer, StandardCharsets.UTF_8)).isEqualTo(TEST_CHARSEQ.toString());
   }

   @Test
   @SuppressWarnings("null")
   public void testResetWithoutMark() {
      assertThatThrownBy(() -> charseqIS.reset()) //
         .isInstanceOf(IOException.class) //
         .hasMessage("Mark has not been set");
   }

   @Test
   public void testSkip() throws IOException {
      final long skipped = charseqIS.skip(7);
      assertThat(skipped).isEqualTo(7);

      final byte[] buffer = new byte[BUFFER_SIZE];
      final int bytesRead = charseqIS.read(buffer);

      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Worl");
   }
}
