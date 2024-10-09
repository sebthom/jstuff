/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CountLatchTest {

   @Test
   void testCountLatch() {
      final var counter = new CountLatch(2);
      assertThat(counter.getCount()).isZero();
      assertThat(counter.getMax()).isEqualTo(2);

      counter.count();
      assertThat(counter.getCount()).isEqualTo(1);
      counter.count();
      assertThat(counter.getCount()).isEqualTo(2);
      counter.count();
      assertThat(counter.getCount()).isEqualTo(2);
   }
}
