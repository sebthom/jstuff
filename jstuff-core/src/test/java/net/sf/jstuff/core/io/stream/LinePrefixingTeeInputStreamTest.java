/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LinePrefixingTeeInputStreamTest {

   @Test
   public void testLinePrefixingOuputStream() throws IOException {
      final byte[] inputData = Strings.join(Arrays.asList("Line 1", "Line 2", "Line 3"), Strings.NEW_LINE).getBytes();

      try (FastByteArrayInputStream input = new FastByteArrayInputStream(inputData);
           FastByteArrayOutputStream branch = new FastByteArrayOutputStream();
           LinePrefixingTeeInputStream tee = new LinePrefixingTeeInputStream(input, branch, "prefix: ")) {

         assertThat(IOUtils.toString(tee)).isEqualTo("Line 1" + Strings.NEW_LINE + "Line 2" + Strings.NEW_LINE + "Line 3");
         assertThat(branch.toString()).isEqualTo("prefix: Line 1" + Strings.NEW_LINE + "prefix: Line 2" + Strings.NEW_LINE + "prefix: Line 3");
      }
   }
}
