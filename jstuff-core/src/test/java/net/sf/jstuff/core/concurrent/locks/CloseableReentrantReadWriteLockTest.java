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
class CloseableReentrantReadWriteLockTest {

   @Test
   @SuppressWarnings("resource")
   void testCloseableReentrantReadWriteLock() {
      final var lock = new CloseableReentrantReadWriteLock();

      assertThat(lock.isWriteLockedByCurrentThread()).isFalse();
      try (CloseableLock l = lock.writeLock().lockAndGet()) {
         assertThat(lock.isWriteLockedByCurrentThread()).isTrue();
      }
      assertThat(lock.isWriteLockedByCurrentThread()).isFalse();

      assertThat(lock.readLock().lockAndGet()).isSameAs(lock.readLock().lockAndGet());
   }
}
