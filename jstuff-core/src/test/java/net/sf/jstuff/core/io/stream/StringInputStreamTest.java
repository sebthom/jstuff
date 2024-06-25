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
public class StringInputStreamTest {

   private static final int BUFFER_SIZE = 4;
   private static final String TEST_STRING = "Hello, World!";

   private final StringInputStream stringIS = new StringInputStream(TEST_STRING, BUFFER_SIZE);

   @Test
   public void testAvailable() throws IOException {
      assertThat(stringIS.available()).isEqualTo(TEST_STRING.length());

      final byte[] buffer = new byte[BUFFER_SIZE];
      stringIS.read(buffer);
      assertThat(stringIS.available()).isEqualTo(TEST_STRING.length() - BUFFER_SIZE);
   }

   @Test
   public void testMarkAndReset() throws IOException {
      final byte[] buffer = new byte[BUFFER_SIZE];

      // Read initial data
      int bytesRead = stringIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Hell");

      // Mark the current position
      stringIS.mark(0);

      // Read more data
      bytesRead = stringIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("o, W");

      // Reset to the marked position
      stringIS.reset();

      // Read again from the marked position
      bytesRead = stringIS.read(buffer);
      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("o, W");
   }

   @Test
   public void testRead() throws IOException {
      final byte[] buffer = new byte[BUFFER_SIZE];
      final int bytesRead = stringIS.read(buffer);

      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Hell");
   }

   @Test
   public void testReadToEnd() throws IOException {
      final byte[] buffer = new byte[TEST_STRING.length()];
      stringIS.read(buffer);

      assertThat(new String(buffer, StandardCharsets.UTF_8)).isEqualTo(TEST_STRING);
   }

   @Test
   @SuppressWarnings("null")
   public void testResetWithoutMark() {
      assertThatThrownBy(() -> stringIS.reset()) //
         .isInstanceOf(IOException.class) //
         .hasMessage("Mark has not been set");
   }

   @Test
   public void testSkip() throws IOException {
      final long skipped = stringIS.skip(7);
      assertThat(skipped).isEqualTo(7);

      final byte[] buffer = new byte[BUFFER_SIZE];
      final int bytesRead = stringIS.read(buffer);

      assertThat(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)).isEqualTo("Worl");
   }
}