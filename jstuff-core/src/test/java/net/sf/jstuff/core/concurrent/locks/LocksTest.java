/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent.locks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LocksTest {

   @Test
   @SuppressWarnings("resource")
   public void testLockAsCloseable() {
      final ReentrantLock lock = new ReentrantLock();
      final CloseableLock closeable = Locks.toCloseable(lock);

      assertThat(lock.isHeldByCurrentThread()).isFalse();
      try (CloseableLock l = closeable.lockAndGet()) {
         assertThat(lock.isHeldByCurrentThread()).isTrue();
      }
      assertThat(lock.isHeldByCurrentThread()).isFalse();

      assertThat(closeable.lockAndGet()).isSameAs(closeable.lockAndGet());

      closeable.close();
   }
}
