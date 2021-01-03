/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io.stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockStreamTest {

   @Test
   public void testZippedBlockStreamPartialRead() throws IOException {

      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try (ZippedBlockOutputStream zos = new ZippedBlockOutputStream(bos, 16, Deflater.BEST_SPEED)) {
         zos.write("Hello World! Hello World!".getBytes(UTF_8));
      }

      try (ZippedBlockInputStream zis = new ZippedBlockInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
         assertThat(IOUtils.readBytes(zis, 12)).hasSize(12);
         assertThat(new String(IOUtils.readBytes(zis, 13), UTF_8)).isEqualTo(" Hello World!");
      }
   }

   @Test
   public void testZippedBlockStream() throws IOException {

      final String content = RandomStringUtils.random(4096);

      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try (OutputStream zos = new ZippedBlockOutputStream(bos, 512)) {
         zos.write(content.getBytes(UTF_8));
      }

      try (InputStream is = new ZippedBlockInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
         assertThat(IOUtils.toString(is, UTF_8)).isEqualTo(content);
      }
   }
}
