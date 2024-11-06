/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io.stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class LineTransformingOutputStreamTest {

   @Test
   void shouldFlushRemainingDataOnClose() throws IOException {
      // Arrange
      final Function<String, String> transformer = String::trim;
      final String input = "  line without newline  ";
      final String expectedOutput = "line without newline";

      // Act
      final String output = transform(transformer, input);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldHandleChunckedWrites() throws IOException {
      // Arrange
      final Function<String, String> transformer = new Function<>() {
         private int lineNumber = 1;

         @Override
         public String apply(final String line) {
            return lineNumber++ + ": " + line;
         }
      };
      final String[] inputs = {"First line\nSe", "cond line\nThi", "rd line"};
      final String expectedOutput = "1: First line\n2: Second line\n3: Third line";

      // Act
      final String output = transform(transformer, inputs);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldHandleDifferentLineEndings() throws IOException {
      // Arrange
      final Function<String, String> transformer = line -> "[" + line + "]";
      final var input = "Line one\r\nLine two\nLine three\rLine four";
      final var expectedOutput = "[Line one\r\n][Line two\n][Line three\r][Line four]";

      // Act
      final String output = transform(transformer, input);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldHandleEmptyInput() throws IOException {
      // Arrange
      final Function<String, String> transformer = String::toUpperCase;
      final String input = "";
      final String expectedOutput = "";

      // Act
      final String output = transform(transformer, input);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldHandleLargeInput() throws IOException {
      // Arrange
      final Function<String, String> transformer = String::toUpperCase;
      final var inputBuilder = new StringBuilder();
      final var expectedBuilder = new StringBuilder();
      final int numberOfLines = 50_000;

      for (int i = 1; i <= numberOfLines; i++) {
         inputBuilder.append("line ").append(i).append("\n");
         expectedBuilder.append("LINE ").append(i).append("\n");
      }

      final String input = inputBuilder.toString();
      final String expectedOutput = expectedBuilder.toString();

      // Act
      final String output = transform(transformer, input);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldHandleOnlyLineBreaks() throws IOException {
      // Arrange
      final Function<String, String> transformer = line -> "[" + line + "]";
      final String input = "\n\r\n\r\n\n";
      final String expectedOutput = "[\n][\r\n][\r\n][\n]";

      // Act
      final String output = transform(transformer, input);

      // Assert
      assertThat(output).isEqualTo(expectedOutput);
   }

   @Test
   void shouldTransformLinesToUpperCase() throws IOException {
      // Arrange & Act
      final String output = transform(String::toUpperCase, "Hello\nWorld\nThis is a test.");

      // Assert
      assertThat(output).isEqualTo("HELLO\nWORLD\nTHIS IS A TEST.");
   }

   private String transform(final Function<String, String> transformer, final String... inputs) throws IOException {
      try (var baos = new FastByteArrayOutputStream();
           var transformingStream = new LineTransformingOutputStream(baos, transformer)) {
         for (final var input : inputs) {
            transformingStream.write(input.getBytes(UTF_8));
         }
         transformingStream.flush();
         return baos.toString(UTF_8);
      }
   }
}
