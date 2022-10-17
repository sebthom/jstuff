/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantLockTest {

   @Test
   @SuppressWarnings("resource")
   public void testCloseableReentrantLock() {
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
