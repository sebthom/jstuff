/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ZippedBlockStreamTest {

   @Test
   void testZippedBlockStreamPartialRead() throws IOException {

      final var bos = new ByteArrayOutputStream();
      try (var zos = new ZippedBlockOutputStream(bos, 16, Deflater.BEST_SPEED)) {
         zos.write("Hello World! Hello World!".getBytes(UTF_8));
      }

      try (var zis = new ZippedBlockInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
         assertThat(IOUtils.readBytes(zis, 12)).hasSize(12);
         assertThat(new String(IOUtils.readBytes(zis, 13), UTF_8)).isEqualTo(" Hello World!");
      }
   }

   @Test
   void testZippedBlockStream() throws IOException {

      final String content = RandomStringUtils.insecure().next(4096);

      final var bos = new ByteArrayOutputStream();
      try (var zos = new ZippedBlockOutputStream(bos, 512)) {
         zos.write(content.getBytes(UTF_8));
      }

      try (var is = new ZippedBlockInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
         assertThat(IOUtils.toString(is, UTF_8)).isEqualTo(content);
      }
   }
}
