/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManagerTest {
   private static final Logger LOG = Logger.create();

   private static final int THREADS = 2 * Runtime.getRuntime().availableProcessors();
   private static final int ITERATIONS_PER_THREAD = 100_000;

   private final ExecutorService es = Executors.newFixedThreadPool(THREADS);
   private int sum = -1;

   final Runnable calculation = () -> {
      sum++;
      sum = sum * 2;
      sum = sum / 2;
   };

   @Test
   public void testWithHashLockManager() throws InterruptedException {
      final var lockManager = new HashLockManager<String>(100);

      final StopWatch sw = new StopWatch();
      sw.start();
      sum = 0;

      final var lockCountWasZero = new AtomicBoolean(false);
      final var lockCountWasGreaterThan1 = new AtomicBoolean(false);

      final var launch = new CountDownLatch(THREADS);

      for (int i = 0; i < THREADS; i++) {
         es.submit((Callable<@Nullable Void>) () -> {
            // intentionally generated new object to prove that synchronization is not based on lock identity but hashcode identity
            final var namedLock = new String("MY_LOCK");

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

      LOG.info("With HashLockManager:" + THREADS * ITERATIONS_PER_THREAD + " thread-safe iterations took " + sw + " sum=" + sum);
      assertThat(sum).isEqualTo(THREADS * ITERATIONS_PER_THREAD);

      Threads.await(() -> lockManager.getLockCount() == 0, 2_000); // wait for cleanup thread
      assertThat(lockManager.getLockCount()).isZero();
   }

   @Test
   public void testWithoutHashLockManager() throws InterruptedException {

      // ignore on CI systems, like Travis/GitHub Actions
      if ("true".equals(System.getenv("CI")))
         return;

      final var sw = new StopWatch();
      sw.start();
      sum = 0;

      final var launch = new CountDownLatch(THREADS);

      for (int i = 0; i < THREADS; i++) {
         es.submit((Callable<@Nullable Void>) () -> {
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
      LOG.info("Without HashLockManager:" + THREADS * ITERATIONS_PER_THREAD + " thread-unsafe iterations took " + sw + " sum=" + sum);
      assertThat(sum).isNotEqualTo(THREADS * ITERATIONS_PER_THREAD);
   }
}
