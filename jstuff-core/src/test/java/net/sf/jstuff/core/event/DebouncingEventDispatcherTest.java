/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DebouncingEventDispatcherTest {

   @Test
   public void testDebouncingEventDispatcher() throws InterruptedException, ExecutionException {
      final var em = new DebouncingEventDispatcher<@Nullable String>(Duration.ofMillis(100));

      final var eventCount = new AtomicLong();
      final EventListener<@Nullable String> listener1 = event -> eventCount.incrementAndGet();

      assertThat(em.subscribe(listener1)).isTrue();
      assertThat(em.subscribe(listener1)).isFalse();

      final var future1a = em.fire("123");
      em.fire("123");
      final var future1b = em.fire("123");

      Thread.sleep(150);

      em.fire("123");
      em.fire("123");
      final var future2 = em.fire("123");

      assertThat(future1a).isEqualTo(future1b);
      assertThat(future2).isNotEqualTo(future1b);

      future2.get();

      assertThat(eventCount.get()).isEqualTo(2);
   }
}
