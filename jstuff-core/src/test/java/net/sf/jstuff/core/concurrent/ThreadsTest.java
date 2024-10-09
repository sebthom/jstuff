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
class ThreadsTest {

   @Test
   void testSleep() {
      final long now = System.currentTimeMillis();
      Threads.sleep(100);
      assertThat(System.currentTimeMillis() - now).isGreaterThanOrEqualTo(100);
   }

   @Test
   void testThreads() {
      assertThat(Threads.count()).isPositive();
      assertThat(Threads.all()).hasSize(Threads.count());
      assertThat(Threads.allSortedByPriority()).hasSize(Threads.count());
      assertThat(Threads.rootThreadGroup()).isNotNull();
   }
}
