/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class AtomicBooleansTest {

   @Test
   void testNegateMultithreaded() throws InterruptedException {
      final var ab = new AtomicBoolean(false);
      final int cpuCores = Runtime.getRuntime().availableProcessors();
      final int numThreads = Math.max(2, Math.min(cpuCores * 2, 16));
      final int togglesPerThread = 250_000;

      final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
      final CountDownLatch readyLatch = new CountDownLatch(numThreads);
      final CountDownLatch startLatch = new CountDownLatch(1);
      final CountDownLatch doneLatch = new CountDownLatch(numThreads);

      try {
         for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
               try {
                  // Signal readiness
                  readyLatch.countDown();
                  // Wait for all threads to be ready
                  startLatch.await();

                  for (int j = 0; j < togglesPerThread; j++) {
                     if (Thread.currentThread().isInterrupted()) {
                        return;
                     }
                     AtomicBooleans.negate(ab);
                  }
               } catch (final InterruptedException e) {
                  Thread.currentThread().interrupt();
               } finally {
                  doneLatch.countDown();
               }
            });
         }

         // Wait until all threads are ready
         assertThat(readyLatch.await(10, TimeUnit.SECONDS)).isTrue();

         // Signal all threads to start simultaneously
         startLatch.countDown();

         // Wait for all threads to finish (with a generous timeout)
         final boolean allThreadsCompleted = doneLatch.await(30, TimeUnit.SECONDS);
         assertThat(allThreadsCompleted).isTrue();
      } finally {
         executor.shutdownNow();
         executor.awaitTermination(30, TimeUnit.SECONDS);
      }

      final int totalToggles = numThreads * togglesPerThread; // If that number is even, the final value should equal the initial value.
      final boolean expectedValue = totalToggles % 2 != 0;
      assertThat(ab.get()).isEqualTo(expectedValue);
   }
}
