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

   @Test
   void shouldCreateLinearGradientWithTwoColors() {
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

   @Test
   void shouldCreateLinearGradientWithMultipleSteps() {
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
   void shouldThrowExceptionForInvalidSteps() {
      // Given
      final RGB from = new RGB(0, 0, 0);
      final RGB to = new RGB(255, 255, 255);
      final int steps = 1;

      // When / Then
      assertThatIllegalArgumentException().isThrownBy(() -> RGB.createLinearGradient(from, to, steps)).withMessage(
         "[steps] must be 2 or greater but is 1");
   }
}
