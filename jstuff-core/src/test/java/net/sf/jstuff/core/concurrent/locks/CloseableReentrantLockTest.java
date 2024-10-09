/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CloseableReentrantLockTest {

   @Test
   @SuppressWarnings("resource")
   void testCloseableReentrantLock() {
      final var lock = new CloseableReentrantLock();

      assertThat(lock.isHeldByCurrentThread()).isFalse();
      try (CloseableLock l = lock.lockAndGet()) {
         assertThat(lock.isHeldByCurrentThread()).isTrue();
      }
      assertThat(lock.isHeldByCurrentThread()).isFalse();

      assertThat(lock.lockAndGet()).isSameAs(lock.lockAndGet());

      lock.close();
   }
}
