/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class LRUCacheWithExpirationTest {

   private static final Duration TTL = Duration.ofSeconds(1);

   private final LRUMapWithExpiration<String, String> cache = new LRUMapWithExpiration<>(3, TTL);

   @Test
   void testPutAndGet() {
      cache.put("key1", "value1");
      cache.put("key2", "value2");
      cache.put("key3", "value3");

      assertThat(cache) //
         .containsEntry("key1", "value1") //
         .containsEntry("key2", "value2") //
         .containsEntry("key3", "value3");
   }

   @Test
   void testLastRecentlyAccessed() {
      cache.put("key1", "value1");
      cache.put("key2", "value2");
      cache.put("key3", "value3");

      // access key1 to make it the most recently used
      cache.get("key1");

      // add another entry which should evict the least recently used (key2)
      cache.put("key4", "value4");

      // key2 should be evicted now
      assertThat(cache.get("key2")).isNull();

      assertThat(cache) //
         .containsEntry("key1", "value1") //
         .containsEntry("key3", "value3") //
         .containsEntry("key4", "value4");
   }

   @Test
   void testExpiration() throws InterruptedException {
      cache.put("key1", "value1");
      cache.put("key2", "value2");

      Thread.sleep(TTL.toMillis() + 200);

      cache.put("key3", "value3");
      cache.put("key4", "value4");

      assertThat(cache) //
         .containsEntry("key3", "value3") //
         .containsEntry("key4", "value4");

      assertThat(cache.get("key1")).isNull();
      assertThat(cache.get("key2")).isNull();
   }

   @Test
   void testUpdateResetsExpiration() throws InterruptedException {
      cache.put("key1", "value1");

      // wait for half the TTL
      Thread.sleep(TTL.toMillis() / 2);

      // update the entry -> should reset entry expiration
      cache.put("key1", "value2");

      // wait for half the TTL again
      Thread.sleep(TTL.toMillis() / 2);

      // the entry should still be present
      assertEquals("value2", cache.get("key1"));

      // wait for the TTL to expire
      Thread.sleep(TTL.toMillis() + 200);

      // now the entry should be expired and removed from the cache
      assertNull(cache.get("key1"));
   }

}
