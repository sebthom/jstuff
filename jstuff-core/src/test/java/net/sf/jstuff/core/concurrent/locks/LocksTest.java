/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent.locks;

import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LocksTest extends TestCase {

   public void testLockAsCloseable() {
      final ReentrantLock lock = new ReentrantLock();
      final CloseableLock closeable = Locks.toCloseable(lock);

      assertFalse(lock.isHeldByCurrentThread());
      try (CloseableLock l = closeable.lockAndGet()) {
         assertTrue(lock.isHeldByCurrentThread());
      }
      assertFalse(lock.isHeldByCurrentThread());

      assertSame(closeable.lockAndGet(), closeable.lockAndGet());

      closeable.close();
   }
}
