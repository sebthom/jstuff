/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LocksTest {

   @Test
   @SuppressWarnings("resource")
   public void testLockAsCloseable() {
      final var lock = new ReentrantLock();
      final var closeable = Locks.toCloseable(lock);

      assertThat(lock.isHeldByCurrentThread()).isFalse();
      try (CloseableLock l = closeable.lockAndGet()) {
         assertThat(lock.isHeldByCurrentThread()).isTrue();
      }
      assertThat(lock.isHeldByCurrentThread()).isFalse();

      assertThat(closeable.lockAndGet()).isSameAs(closeable.lockAndGet());

      closeable.close();
   }
}
