/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ScalingScheduledExecutorServiceTest {

   private static final Logger LOG = Logger.create();

   static void testScalingScheduledExecutorServiceTest(final ScalingScheduledExecutorService executor) throws ExecutionException,
      InterruptedException {
      final int maxPoolSize = executor.getMaximumPoolSize();

      final var threadsExecuting = new AtomicInteger();
      final var work = new CountDownLatch(maxPoolSize + 2);

      assertThat(executor.getActiveCount()).isZero();

      LOG.info("Submitting tasks...");
      final var futures = new ArrayList<Future<Integer>>();
      for (long i = 0, l = work.getCount(); i < l; i++) {
         LOG.info("Submitting #%s", i);
         final long j = i;
         futures.add(executor.schedule(() -> {
            try {
               threadsExecuting.incrementAndGet();
               LOG.info("Executing #%s", j);
               Thread.sleep(2_000);
               work.countDown();
               return 1;
            } catch (final InterruptedException ex) {
               Thread.currentThread().interrupt();
               threadsExecuting.decrementAndGet();
               return 0;
            }
         }, 1, TimeUnit.SECONDS));
      }

      for (final Future<Integer> future : futures) {
         assertThat(future.isDone()).isFalse();
      }

      Thread.sleep(1_500);

      assertThat(executor.getActiveCount()).isEqualTo(maxPoolSize);
      assertThat(threadsExecuting.get()).isEqualTo(maxPoolSize);

      LOG.info("Waiting for task completion...");
      work.await(5, TimeUnit.SECONDS);

      for (final Future<Integer> future : futures) {
         assertThat(future.isDone()).isTrue();
         assertThat(future.get()).isEqualTo(1);
      }

      LOG.info("Waiting for thread pool cleanup...");
      Thread.sleep(1_000 + executor.getKeepAliveTime(TimeUnit.MILLISECONDS));
      assertThat(executor.getActiveCount()).isEqualTo(executor.getCorePoolSize());
   }

   @Test
   public void testAsScheduler() throws ExecutionException, InterruptedException {
      final var executor = new ScalingScheduledExecutorService(0, 3, 5, TimeUnit.SECONDS);
      try {
         testScalingScheduledExecutorServiceTest(executor);
      } finally {
         executor.shutdownNow();
      }
   }

   @Test
   public void testAsThreadPoolExecutor() throws ExecutionException, InterruptedException {
      final var executor = new ScalingScheduledExecutorService(0, 3, 5, TimeUnit.SECONDS);
      try {
         ScalingThreadPoolExecutorTest.testScalingThreadPoolExecutor(executor);
      } finally {
         executor.shutdownNow();
      }
   }
}
