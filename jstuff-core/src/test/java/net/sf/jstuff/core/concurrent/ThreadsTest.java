/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadsTest extends TestCase {
   public void testSleep() {
      final long now = System.currentTimeMillis();
      Threads.sleep(100);
      assertTrue(System.currentTimeMillis() - now >= 100);
   }

   public void testThreads() {
      assertEquals(0, Threads.deadlockedIDs().length);
      assertTrue(Threads.count() > 0);
      assertEquals(Threads.count(), Threads.all().length);
      assertEquals(Threads.count(), Threads.allSortedByPriority().length);
      assertNotNull(Threads.rootThreadGroup());
   }
}
