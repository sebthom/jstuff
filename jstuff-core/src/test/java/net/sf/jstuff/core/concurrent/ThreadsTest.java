/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadsTest {

   @Test
   public void testSleep() {
      final long now = System.currentTimeMillis();
      Threads.sleep(100);
      assertThat(System.currentTimeMillis() - now >= 100).isTrue();
   }

   @Test
   public void testThreads() {
      assertThat(Threads.count() > 0).isTrue();
      assertThat(Threads.all()).hasSize(Threads.count());
      assertThat(Threads.allSortedByPriority()).hasSize(Threads.count());
      assertThat(Threads.rootThreadGroup()).isNotNull();
   }
}
