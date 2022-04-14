/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.junit.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RandomInputStreamTest {

   @Test
   public void testDummyInputStream() throws IOException {
      try (RandomInputStream stream = new RandomInputStream(5123)) {
         final byte[] result = IOUtils.readBytes(stream);
         assertThat(result).hasSize(5123);
         final byte[] unexpected = new byte[5123];
         assertThat(result).isNotEqualTo(unexpected);
      }
   }
}
