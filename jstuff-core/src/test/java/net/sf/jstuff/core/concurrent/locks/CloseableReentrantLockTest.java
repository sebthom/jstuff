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
public class CloseableReentrantLockTest extends TestCase {

   @SuppressWarnings("resource")
   public void testCloseableReentrantLock() {
      final CloseableReentrantLock lock = new CloseableReentrantLock();

      assertFalse(lock.isHeldByCurrentThread());
      try (CloseableLock l = lock.lockAndGet()) {
         assertTrue(lock.isHeldByCurrentThread());
      }
      assertFalse(lock.isHeldByCurrentThread());

      assertSame(lock.lockAndGet(), lock.lockAndGet());

      lock.close();
   }
}
