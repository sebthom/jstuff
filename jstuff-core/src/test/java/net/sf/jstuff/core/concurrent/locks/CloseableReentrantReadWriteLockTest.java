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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantReadWriteLockTest extends TestCase {

   @SuppressWarnings("resource")
   public void testCloseableReentrantReadWriteLock() {
      final CloseableReentrantReadWriteLock lock = new CloseableReentrantReadWriteLock();

      assertFalse(lock.isWriteLockedByCurrentThread());
      try (CloseableLock l = lock.writeLock().lockAndGet()) {
         assertTrue(lock.isWriteLockedByCurrentThread());
      }
      assertFalse(lock.isWriteLockedByCurrentThread());

      assertSame(lock.readLock().lockAndGet(), lock.readLock().lockAndGet());
   }
}
