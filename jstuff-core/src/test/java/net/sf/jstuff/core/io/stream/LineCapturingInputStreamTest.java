/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class LineCapturingInputStreamTest {

   @Test
   void testLinePrefixingOuputStream() throws IOException {
      final var inputLines = List.of("Line 1", "Line 2", "Line 3");
      final var capturedLines = new ArrayList<String>();

      try (var is = new LineCapturingInputStream(new ByteArrayInputStream(Strings.join(inputLines, Strings.NEW_LINE).getBytes()), line -> {
         capturedLines.add(line);
      })) {
         IOUtils.readBytes(is);
      }

      assertThat(inputLines).isEqualTo(capturedLines);
   }
}
