/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class SyncEventDispatcherTest {

   @Test
   void testSyncEventDispatcher() throws InterruptedException, ExecutionException {
      final var em = new SyncEventDispatcher<String>();

      final var listener1Count = new AtomicLong();
      final EventListener<String> listener1 = event -> listener1Count.incrementAndGet();

      assertThat(em.subscribe(listener1)).isTrue();
      assertThat(em.subscribe(listener1)).isFalse();

      final var listener2Count = new AtomicLong();
      final FilteringEventListener<String> listener2 = FilteringEventListener.create( //
         e -> listener2Count.incrementAndGet(), //
         e -> e.length() < 5);

      assertThat(em.subscribe(listener2)).isTrue();
      assertThat(em.subscribe(listener2)).isFalse();

      assertThat(em.fire("123").get()).isEqualTo(2);
      assertThat(listener1Count.get()).isEqualTo(1);
      assertThat(listener2Count.get()).isEqualTo(1);

      assertThat(em.fire("1234567890").get()).isEqualTo(1);
      assertThat(listener1Count.get()).isEqualTo(2);
      assertThat(listener2Count.get()).isEqualTo(1);
   }
}
