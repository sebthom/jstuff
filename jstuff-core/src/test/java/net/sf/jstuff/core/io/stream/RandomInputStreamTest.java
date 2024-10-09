/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class RandomInputStreamTest {

   @Test
   void testDummyInputStream() throws IOException {
      try (var stream = new RandomInputStream(5123)) {
         final byte[] result = IOUtils.readBytes(stream);
         assertThat(result).hasSize(5123);
         final byte[] unexpected = new byte[5123];
         assertThat(result).isNotEqualTo(unexpected);
      }
   }
}
