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

import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;
import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadDumperTest extends TestCase {

   private void startThreads() {

      /*
       * start sleeping thread
       */
      new Thread(() -> {
         Threads.sleep(Integer.MAX_VALUE);
      }, "sleeping").start();

      /*
       * start threads that deadlock on ReentrantLock
       */
      {
         final ReentrantLock lock1 = new ReentrantLock();
         final ReentrantLock lock2 = new ReentrantLock();

         new Thread(() -> {
            lock1.lock();
            try {
               Threads.sleep(100);
               lock2.lock();
            } finally {
               lock1.unlock();
            }
         }, "deadlocked_ReentrantLock1").start();
         new Thread(() -> {
            lock2.lock();
            try {
               Threads.sleep(100);
               lock1.lock();
            } finally {
               lock2.unlock();
            }
         }, "deadlocked_ReentrantLock2").start();
      }

      /*
       * start threads that deadlock on ReentrantLock
       */
      {
         final Object lock1 = new Object();
         final Object lock2 = new Object();

         new Thread(() -> {
            synchronized (lock1) {
               Threads.sleep(100);
               synchronized (lock2) {
                  // bla bla
               }
            }
         }, "deadlocked_synchronized1").start();
         new Thread(() -> {
            synchronized (lock2) {
               Threads.sleep(100);
               synchronized (lock1) {
                  // bla bla
               }
            }
         }, "deadlocked_synchronized2").start();
      }

      Threads.sleep(500);
   }

   /*
    * https://blog.tier1app.com/2014/11/26/thread-dump-analysis/
    */
   @SuppressWarnings("deprecation")
   public void testDumpThreads() {
      startThreads();

      assertEquals(4, Threads.deadlockedIds().length);
      assertEquals(4, Threads.deadlocked().length);

      {
         final StringBuilder out = new StringBuilder();
         final ThreadDumper threadDumper = ThreadDumper.builder() //
            .withDeadlockReport(true) //
            .withFooterPrinter(p -> p.append("#### THREAD DUMP END ####")) //
            .build();
         threadDumper.dumpThreads(out);

         System.out.println(out);

         assertTrue(Strings.contains(out, "Finalizer"));

         assertTrue(Strings.contains(out, "Found 2 deadlocks."));
         assertTrue(Strings.endsWith(out, "#### THREAD DUMP END ####"));
      }

      {
         final StringBuilder out = new StringBuilder();
         final ThreadDumper threadDumper = ThreadDumper.builder() //
            .withDeadlockReport(false) //
            .withThreadFilter(t -> !"Finalizer".equals(t.getThreadName())) //
            .withFooterPrinter(p -> p.append("#### THREAD DUMP END ####")) //
            .build();
         threadDumper.dumpThreads(out);

         assertFalse(Strings.contains(out, "Finalizer"));

         assertFalse(Strings.contains(out, "Found 2 deadlocks."));
         assertTrue(Strings.endsWith(out, "#### THREAD DUMP END ####"));
      }

      // end threads that deadlock on "Lock.lock()"
      for (final Thread deadlocked : Threads.deadlocked()) {
         deadlocked.stop(); // does not end threads that deadlock on "synchronized(lock)"
      }

      {
         final StringBuilder out = new StringBuilder();
         final ThreadDumper threadDumper = ThreadDumper.builder() //
            .withDeadlockReport(true) //
            .withThreadFilter(t -> false) //
            .withFooterPrinter(p -> p.append("#### THREAD DUMP END ####")) //
            .build();
         threadDumper.dumpThreads(out);

         System.out.println(out);

         assertFalse(Strings.contains(out, "Finalizer"));

         assertTrue(Strings.contains(out, "Found 1 deadlock."));
         assertTrue(Strings.endsWith(out, "#### THREAD DUMP END ####"));
      }

      assertEquals(2, Threads.deadlockedIds().length);

      Threads.sleep(1_000_000_000);
   }
}
