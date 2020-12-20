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

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantLockTest {

   @Test
   @SuppressWarnings("resource")
   public void testCloseableReentrantLock() {
      final CloseableReentrantLock lock = new CloseableReentrantLock();

      assertThat(lock.isHeldByCurrentThread()).isFalse();
      try (CloseableLock l = lock.lockAndGet()) {
         assertThat(lock.isHeldByCurrentThread()).isTrue();
      }
      assertThat(lock.isHeldByCurrentThread()).isFalse();

      assertThat(lock.lockAndGet()).isSameAs(lock.lockAndGet());

      lock.close();
   }
}
