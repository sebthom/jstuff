/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.graphic;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class RGBTest {

   private void assertValidHexColor0(final String hex, final int expectedRed, final int expectedGreen, final int expectedBlue) {
      final RGB color = RGB.fromHex(hex);
      assertThat(color).isNotNull();
      assertThat(color.red).isEqualTo(expectedRed);
      assertThat(color.green).isEqualTo(expectedGreen);
      assertThat(color.blue).isEqualTo(expectedBlue);
   }

   private void assertValidHexColor(final String hex, final int expectedRed, final int expectedGreen, final int expectedBlue) {
      assertValidHexColor0(hex, expectedRed, expectedGreen, expectedBlue);
      assertValidHexColor0(hex.toLowerCase(), expectedRed, expectedGreen, expectedBlue);
   }

   @Test
   void test_fromHex_3Digits() {
      // Shorthand 3-digit format (web standard)
      assertValidHexColor("FAB", 255, 170, 187);
      // Shorthand 3-digit format with # prefix  (web standard)
      assertValidHexColor("#FAB", 255, 170, 187);
      // Shorthand 3-digit format with C-style 0x prefix
      assertValidHexColor("0xFAB", 255, 170, 187);
      // Shorthand 3-digit format with alternative x prefix
      assertValidHexColor("xFAB", 255, 170, 187);
   }

   @Test
   void test_fromHex_6Digits() {
      // Standard web color format
      assertValidHexColor("FFAABB", 255, 170, 187);
      // Standard web color format with # prefix
      assertValidHexColor("#FFAABB", 255, 170, 187);
      // C-style hex color format with 0x prefix
      assertValidHexColor("0xFFAABB", 255, 170, 187);
      // Alternative hex notation with x prefix
      assertValidHexColor("xFFAABB", 255, 170, 187);
   }

   @Test
   void test_fromHex_InvalidInput() {
      // Invalid hex string with non-hexadecimal characters
      assertThatIllegalArgumentException().isThrownBy(() -> RGB.fromHex("#GGGGGG")).withMessageContaining("Invalid hex color code");
      // Invalid hex string (4 characters instead of 3 or 6)
      assertThatIllegalArgumentException().isThrownBy(() -> RGB.fromHex("#FFFF")).withMessageContaining("must be 3 or 6 characters long");
   }

   @Test
   void test_toHex() {
      // Given
      final RGB color = new RGB(255, 170, 187);

      // When / Then
      assertThat(color.toHex()).isEqualTo("#FFAABB");
      assertThat(color.toHex("#")).isEqualTo("#FFAABB");
      assertThat(color.toHex("0x")).isEqualTo("0xFFAABB");
      assertThat(color.toHex("x")).isEqualTo("xFFAABB");
      assertThat(color.toHex("")).isEqualTo("FFAABB");
   }

   @Test
   void test_createLinearGradient_WithMultipleSteps() {
      // Given
      final RGB from = new RGB(0, 0, 0); // Black
      final RGB to = new RGB(255, 255, 255); // White
      final int steps = 5;

      // When
      final RGB[] gradient = RGB.createLinearGradient(from, to, 5);

      // Then
      assertThat(gradient).hasSize(steps);
      assertThat(gradient[0]).isEqualTo(from);
      assertThat(gradient[1]).isEqualTo(new RGB(64, 64, 64));
      assertThat(gradient[2]).isEqualTo(new RGB(128, 128, 128));
      assertThat(gradient[3]).isEqualTo(new RGB(191, 191, 191));
      assertThat(gradient[steps - 1]).isEqualTo(to);
   }

   @Test
   void test_createLinearGradient_WithTwoColors() {
      // Given
      final RGB from = new RGB(0, 0, 0); // Black
      final RGB to = new RGB(255, 255, 255); // White
      final int steps = 2;

      // When
      final RGB[] gradient = RGB.createLinearGradient(from, to, steps);

      // Then
      assertThat(gradient).hasSize(steps);
      assertThat(gradient[0]).isEqualTo(from);
      assertThat(gradient[1]).isEqualTo(to);
   }
}
