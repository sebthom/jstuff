/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadDumperTest {

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
   @Test
   @SuppressWarnings("deprecation")
   public void testDumpThreads() {
      startThreads();

      assertThat(Threads.deadlockedIds()).hasSize(4);
      assertThat(Threads.deadlocked()).hasSize(4);

      {
         final StringBuilder out = new StringBuilder();
         final ThreadDumper threadDumper = ThreadDumper.builder() //
            .withDeadlockReport(true) //
            .withFooterPrinter(p -> p.append("#### THREAD DUMP END ####")) //
            .build();
         threadDumper.dumpThreads(out);

         System.out.println(out);

         assertThat(Strings.contains(out, "Finalizer")).isTrue();

         assertThat(Strings.contains(out, "Found 2 deadlocks.")).isTrue();
         assertThat(Strings.endsWith(out, "#### THREAD DUMP END ####")).isTrue();
      }

      {
         final StringBuilder out = new StringBuilder();
         final ThreadDumper threadDumper = ThreadDumper.builder() //
            .withDeadlockReport(false) //
            .withThreadFilter(t -> !"Finalizer".equals(t.getThreadName())) //
            .withFooterPrinter(p -> p.append("#### THREAD DUMP END ####")) //
            .build();
         threadDumper.dumpThreads(out);

         assertThat(Strings.contains(out, "Finalizer")).isFalse();

         assertThat(Strings.contains(out, "Found 2 deadlocks.")).isFalse();
         assertThat(Strings.endsWith(out, "#### THREAD DUMP END ####")).isTrue();
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

         assertThat(Strings.contains(out, "Finalizer")).isFalse();

         assertThat(Strings.contains(out, "Found 1 deadlock.")).isTrue();
         assertThat(Strings.endsWith(out, "#### THREAD DUMP END ####")).isTrue();
      }

      assertThat(Threads.deadlockedIds()).hasSize(2);
   }
}
