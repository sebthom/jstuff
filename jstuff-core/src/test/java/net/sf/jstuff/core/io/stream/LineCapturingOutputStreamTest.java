/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class LineCapturingOutputStreamTest {

   @Test
   void testLinePrefixingOuputStream() throws IOException {
      final var inputLines = List.of("Line 1", "Line 2", "Line 3");
      final var capturedLines = new ArrayList<String>();

      try (var is = new LineCapturingOutputStream(new ByteArrayOutputStream(), line -> {
         capturedLines.add(line);
      })) {
         is.write(Strings.join(inputLines, Strings.NEW_LINE).getBytes());
      }

      assertThat(inputLines).isEqualTo(capturedLines);
   }
}
