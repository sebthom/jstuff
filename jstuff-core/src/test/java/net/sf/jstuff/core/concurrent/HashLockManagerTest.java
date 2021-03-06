/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManagerTest {
   private static final Logger LOG = Logger.create();

   private static final int THREADS = 10;
   private static final int ITERATIONS_PER_THREAD = 40000;

   private final ExecutorService es = Executors.newFixedThreadPool(THREADS);
   private int sum = -1;

   final Runnable calculation = () -> {
      sum++;
      sum = sum * 2;
      sum = sum / 2;
   };

   @Test
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

      assertThat(lockCountWasZero.get()).isFalse();
      assertThat(lockCountWasGreaterThan1.get()).isFalse();

      LOG.info(THREADS * ITERATIONS_PER_THREAD + " thread-safe iterations took " + sw + " sum=" + sum);
      assertThat(sum).isEqualTo(THREADS * ITERATIONS_PER_THREAD);
      Threads.sleep(200); // wait for cleanup thread
      assertThat(lockManager.getLockCount()).isZero();
   }

   @Test
   public void testWithoutHashLockManager() throws InterruptedException {

      // ignore on CI systems, like Travis/GitHub Actions
      if ("true".equals(System.getenv("CI")))
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
      assertThat(sum).isNotEqualTo(THREADS * ITERATIONS_PER_THREAD);
   }
}
