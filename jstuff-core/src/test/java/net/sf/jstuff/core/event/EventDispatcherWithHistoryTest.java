/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventDispatcherWithHistoryTest {

   @Test
   public void testEventDispatcherWithHistory() throws InterruptedException, ExecutionException {
      final EventDispatcherWithHistory<String> em = new EventDispatcherWithHistory<>(new SyncEventDispatcher<String>());

      assertThat(em.fire("123").get()).isZero();
      assertThat(em.fire("1234567890").get()).isZero();

      final AtomicLong listener1Count = new AtomicLong();
      final EventListener<String> listener1 = event -> listener1Count.incrementAndGet();

      assertThat(em.subscribe(listener1)).isTrue();
      assertThat(em.subscribe(listener1)).isFalse();
      assertThat(listener1Count.get()).isZero();

      final AtomicLong listener2Count = new AtomicLong();
      final EventListener<String> listener2 = new FilteringEventListener<String>() {
         @Override
         public boolean accept(final String event) {
            return event != null && event.length() < 5;
         }

         @Override
         public void onEvent(final String event) {
            listener2Count.incrementAndGet();
         }
      };

      assertThat(em.subscribeAndReplayHistory(listener2)).isTrue();
      assertThat(em.subscribeAndReplayHistory(listener2)).isFalse();
      assertThat(listener2Count.get()).isEqualTo(2);
   }
}
