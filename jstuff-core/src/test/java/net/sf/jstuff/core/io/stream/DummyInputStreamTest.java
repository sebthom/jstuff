/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DummyInputStreamTest {

   @Test
   public void testDummyInputStream() throws IOException {
      try (var stream = new DummyInputStream(5123, (byte) 4)) {
         final byte[] result = IOUtils.readBytes(stream);
         assertThat(result).hasSize(5123);
         final byte[] expected = new byte[5123];
         Arrays.fill(expected, 0, 5123, (byte) 4);
         assertThat(result).isEqualTo(expected);
      }
   }
}
