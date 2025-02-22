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
class RGBATest {

   private void assertValidHexColor0(final String hex, final int expectedRed, final int expectedGreen, final int expectedBlue,
         final int expectedAlpha) {
      final RGBA color = RGBA.fromHex(hex);
      assertThat(color).isNotNull();
      assertThat(color.red).isEqualTo(expectedRed);
      assertThat(color.green).isEqualTo(expectedGreen);
      assertThat(color.blue).isEqualTo(expectedBlue);
      assertThat(color.alpha).isEqualTo(expectedAlpha);
   }

   private void assertValidHexColor(final String hex, final int expectedRed, final int expectedGreen, final int expectedBlue,
         final int expectedAlpha) {
      assertValidHexColor0(hex, expectedRed, expectedGreen, expectedBlue, expectedAlpha);
      assertValidHexColor0(hex.toLowerCase(), expectedRed, expectedGreen, expectedBlue, expectedAlpha);
   }

   @Test
   void test_fromHex_3Digits() {
      // Shorthand 3-digit format (web standard)
      assertValidHexColor("FAB", 255, 170, 187, 255);
      assertValidHexColor("FAB0", 255, 170, 187, 0);
      // Shorthand 3-digit format with # prefix  (web standard)
      assertValidHexColor("#FAB", 255, 170, 187, 255);
      assertValidHexColor("#FAB0", 255, 170, 187, 0);
      // Shorthand 3-digit format with C-style 0x prefix
      assertValidHexColor("0xFAB", 255, 170, 187, 255);
      assertValidHexColor("0xFAB0", 255, 170, 187, 0);
      // Shorthand 3-digit format with alternative x prefix
      assertValidHexColor("xFAB", 255, 170, 187, 255);
      assertValidHexColor("xFAB0", 255, 170, 187, 0);
   }

   @Test
   void test_fromHex_6Digits() {
      // Standard web color format
      assertValidHexColor("FFAABB", 255, 170, 187, 255);
      assertValidHexColor("FFAABB80", 255, 170, 187, 128);
      // Standard web color format with # prefix
      assertValidHexColor("#FFAABB", 255, 170, 187, 255);
      assertValidHexColor("#FFAABB80", 255, 170, 187, 128);
      // C-style hex color format with 0x prefix
      assertValidHexColor("0xFFAABB", 255, 170, 187, 255);
      assertValidHexColor("0xFFAABB80", 255, 170, 187, 128);
      // Alternative hex notation with x prefix
      assertValidHexColor("xFFAABB", 255, 170, 187, 255);
      assertValidHexColor("xFFAABB80", 255, 170, 187, 128);
   }

   @Test
   void test_fromHex_InvalidInput() {
      // Invalid hex string with non-hexadecimal characters
      assertThatIllegalArgumentException().isThrownBy(() -> RGBA.fromHex("#GGGGGG")).withMessageContaining("Invalid hex color code");
      // Invalid hex string (4 characters instead of 3 or 6)
      assertThatIllegalArgumentException().isThrownBy(() -> RGBA.fromHex("#FFFFF")).withMessageContaining(
         "must be 3, 4, 6 or 8 characters long");
   }

   @Test
   void test_toHex() {
      // Given
      final RGBA color = new RGBA(255, 170, 187, 128);

      // When / Then
      assertThat(color.toHex()).isEqualTo("#FFAABB80");
      assertThat(color.toHex("#")).isEqualTo("#FFAABB80");
      assertThat(color.toHex("0x")).isEqualTo("0xFFAABB80");
      assertThat(color.toHex("x")).isEqualTo("xFFAABB80");
      assertThat(color.toHex("")).isEqualTo("FFAABB80");
   }

   @Test
   void test_createLinearGradient_WithMultipleSteps() {
      // Given
      final RGBA from = new RGBA(0, 0, 0, 0); // Black
      final RGBA to = new RGBA(255, 255, 255, 255); // White
      final int steps = 5;

      // When
      final RGBA[] gradient = RGBA.createLinearGradient(from, to, 5);

      // Then
      assertThat(gradient).hasSize(steps);
      assertThat(gradient[0]).isEqualTo(from);
      assertThat(gradient[1]).isEqualTo(new RGBA(64, 64, 64, 64));
      assertThat(gradient[2]).isEqualTo(new RGBA(128, 128, 128, 128));
      assertThat(gradient[3]).isEqualTo(new RGBA(191, 191, 191, 191));
      assertThat(gradient[steps - 1]).isEqualTo(to);
   }

   @Test
   void test_createLinearGradient_WithTwoColors() {
      // Given
      final RGBA from = new RGBA(0, 0, 0, 255); // Black
      final RGBA to = new RGBA(255, 255, 255, 255); // White
      final int steps = 2;

      // When
      final RGBA[] gradient = RGBA.createLinearGradient(from, to, steps);

      // Then
      assertThat(gradient).hasSize(steps);
      assertThat(gradient[0]).isEqualTo(from);
      assertThat(gradient[1]).isEqualTo(to);
   }
}
