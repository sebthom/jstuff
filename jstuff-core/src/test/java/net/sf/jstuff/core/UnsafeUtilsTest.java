/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class UnsafeUtilsTest {

   @Test
   void testAddressOf() {
      final var obj1 = new Object();
      assertThat(UnsafeUtils.addressOf(obj1)).isPositive();
      assertThat(UnsafeUtils.addressOf(obj1)).isEqualTo(UnsafeUtils.addressOf(obj1));

      final var obj2 = new Object();
      assertThat(UnsafeUtils.addressOf(obj2)).isPositive();
      assertThat(UnsafeUtils.addressOf(obj1)).isNotEqualTo(UnsafeUtils.addressOf(obj2));
   }
}
