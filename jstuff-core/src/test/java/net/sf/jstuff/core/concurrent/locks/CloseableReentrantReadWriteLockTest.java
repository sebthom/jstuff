/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantReadWriteLockTest {

   @Test
   @SuppressWarnings("resource")
   public void testCloseableReentrantReadWriteLock() {
      final CloseableReentrantReadWriteLock lock = new CloseableReentrantReadWriteLock();

      assertThat(lock.isWriteLockedByCurrentThread()).isFalse();
      try (CloseableLock l = lock.writeLock().lockAndGet()) {
         assertThat(lock.isWriteLockedByCurrentThread()).isTrue();
      }
      assertThat(lock.isWriteLockedByCurrentThread()).isFalse();

      assertThat(lock.readLock().lockAndGet()).isSameAs(lock.readLock().lockAndGet());
   }
}
