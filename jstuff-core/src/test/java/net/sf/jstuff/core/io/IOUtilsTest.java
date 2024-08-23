/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOUtilsTest {

   @Test
   public void testReadBytes() throws IOException {
      final var is = new ByteArrayInputStream("Hello World!".getBytes());
      assertThat(IOUtils.readBytes(is, 5)).hasSize(5);

      is.reset();
      assertThat(new String(IOUtils.readBytes(is, 12))).isEqualTo("Hello World!");
   }
}
