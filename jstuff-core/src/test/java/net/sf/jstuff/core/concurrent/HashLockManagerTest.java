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

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.time.StopWatch;

import junit.framework.TestCase;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManagerTest extends TestCase {
   private static final Logger LOG = Logger.create();

   private static final int THREADS = 10;
   private static final int ITERATIONS_PER_THREAD = 40000;

   private final ExecutorService es = Executors.newFixedThreadPool(THREADS);
   private int sum = -1;

   final Runnable calculation = new Runnable() {
      @Override
      public void run() {
         sum++;
         sum = sum * 2;
         sum = sum / 2;
      }
   };

   public void testWithHashLockManager() throws InterruptedException {
      final HashLockManager<String> lockManager = new HashLockManager<>(100);

      final StopWatch sw = new StopWatch();
      sw.start();
      sum = 0;

      final AtomicBoolean lockCountWasZero = new AtomicBoolean(false);
      final AtomicBoolean lockCountWasGreaterThan1 = new AtomicBoolean(false);

      final CountDownLatch launch = new CountDownLatch(THREADS);

      for (int i = 0; i < THREADS; i++) {
         es.submit((Callable<Void>) () -> {
            // intentionally generated new object to proof synchronization is not based on lock identity but hashcode identity
            final String namedLock = new String("MY_LOCK");

            launch.countDown();
            launch.await();

            for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
               lockManager.executeWriteLocked(namedLock, calculation);
               final int lockCount = lockManager.getLockCount();
               if (lockCount == 0) {
                  lockCountWasZero.set(true);
               } else if (lockCount > 1) {
                  lockCountWasGreaterThan1.set(true);
               }
            }
            return null;
         });
      }
      es.shutdown();
      es.awaitTermination(60, TimeUnit.SECONDS);
      sw.stop();

      assertFalse(lockCountWasZero.get());
      assertFalse(lockCountWasGreaterThan1.get());

      LOG.info(THREADS * ITERATIONS_PER_THREAD + " thread-safe iterations took " + sw + " sum=" + sum);
      assertEquals(THREADS * ITERATIONS_PER_THREAD, sum);
      Threads.sleep(200); // wait for cleanup thread
      assertEquals(0, lockManager.getLockCount());
   }

   public void testWithoutHashLockManager() throws InterruptedException {

      // ignore on travis
      if ("true".equals(System.getenv("TRAVIS")))
         return;

      final StopWatch sw = new StopWatch();
      sw.start();
      sum = 0;

      final CountDownLatch launch = new CountDownLatch(THREADS);

      for (int i = 0; i < THREADS; i++) {
         es.submit((Callable<Void>) () -> {
            final String namedLock = new String("MY_LOCK");

            launch.countDown();
            launch.await();

            for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
               // this synchronization of course has no effect since the lock object is a different string instance for each thread
               synchronized (namedLock) {
                  calculation.run();
               }
            }
            return null;
         });
      }
      es.shutdown();
      es.awaitTermination(60, TimeUnit.SECONDS);
      sw.stop();
      LOG.info(THREADS * ITERATIONS_PER_THREAD + " thread-unsafe iterations took " + sw + " sum=" + sum);
      assertNotEquals(THREADS * ITERATIONS_PER_THREAD, sum);
   }
}
