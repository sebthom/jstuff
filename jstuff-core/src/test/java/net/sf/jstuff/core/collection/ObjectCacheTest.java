/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

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
}
