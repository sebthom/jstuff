/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class LimitedInputStreamTest {

   @Test
   @SuppressWarnings({"resource", "unused"})
   void testLimitedInputStream() throws IOException {

      final var in = new ByteArrayInputStream(new byte[100]);
      final var inLimited = new LimitedInputStream(in, 50, false);

      assertThat(in.available()).isEqualTo(100);
      assertThat(inLimited.available()).isEqualTo(50);

      inLimited.read();
      assertThat(in.available()).isEqualTo(99);
      assertThat(inLimited.available()).isEqualTo(49);

      inLimited.skip(4);
      assertThat(in.available()).isEqualTo(95);
      assertThat(inLimited.available()).isEqualTo(45);

      inLimited.mark(10);
      inLimited.read(new byte[10]);
      assertThat(in.available()).isEqualTo(85);
      assertThat(inLimited.available()).isEqualTo(35);
      inLimited.reset();

      assertThat(inLimited.readAllBytes()).hasSize(45);
      assertThat(in.available()).isEqualTo(50);
      assertThat(inLimited.available()).isZero();

      inLimited.close();
      assertThat(in.read()).isNotEqualTo(-1);
   }
}
