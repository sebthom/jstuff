/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectCacheTest {
   @Test
   public void testObjectCache_SoftRef_NoValuesToKeep() {
      final var cache = new ObjectCache<String, Object>();
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Threads.sleep(500);
      assertThat(cache.contains("1")).isTrue();
      assertThat(cache.contains("2")).isTrue();
      assertThat(cache.contains("3")).isTrue();
   }

   @Test
   public void testObjectCache_WeakRef_NoValuesToKeep() {
      final var cache = new ObjectCache<String, Object>(true);
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Threads.sleep(500);
      assertThat(cache.contains("1")).isFalse();
      assertThat(cache.contains("2")).isFalse();
      assertThat(cache.contains("3")).isFalse();
   }

   @Test
   public void testObjectCache_WeakRef_Last2ValuesToKeep() {
      final var cache = new ObjectCache<String, Object>(2, true);
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Threads.sleep(500);
      assertThat(cache.contains("1")).isFalse();
      assertThat(cache.contains("2")).isTrue();
      assertThat(cache.contains("3")).isTrue();
   }

   @Test
   public void testThreadSafety() throws InterruptedException {
      final int threadCount = 100;
      final int operationsPerThread = 100_000;
      final ObjectCache<Integer, String> cache = new ObjectCache<>(10);

      final var executor = Executors.newFixedThreadPool(threadCount);
      final var latch = new CountDownLatch(threadCount);
      final var exceptions = new ArrayList<Throwable>();

      for (int i = 0; i < threadCount; i++) {
         final int threadId = i;
         executor.submit(() -> {
            try {
               for (int j = 0; j < operationsPerThread; j++) {
                  final int key = threadId * operationsPerThread + j;
                  final String value = "Value-" + key;

                  // Test put and get
                  cache.put(key, value);
                  assertThat(cache.get(key)).isEqualTo(value);

                  // Test contains
                  assertThat(cache.contains(key)).isTrue();

                  // Test size and getAll
                  assertThat(cache.size()).isLessThanOrEqualTo(10);

                  final var allEntries = cache.getAll();
                  assertThat(allEntries).containsKey(key);

                  // Test remove
                  cache.remove(key);
                  assertThat(cache.contains(key)).isFalse();
                  assertThat(cache.get(key)).isNull();
               }
            } catch (final Exception e) {
               synchronized (exceptions) {
                  exceptions.add(e);
               }
            } finally {
               latch.countDown();
            }
         });
      }

      latch.await();
      executor.shutdown();

      // Check if any exception occurred during concurrent operations
      if (!exceptions.isEmpty()) {
         fail("Exceptions occurred during concurrent operations: " + exceptions, exceptions.get(0));
      }
   }
}
