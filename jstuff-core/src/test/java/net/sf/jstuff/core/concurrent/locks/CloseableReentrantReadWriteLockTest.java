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
