/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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
public class SyncEventDispatcherTest {

   @Test
   public void testSyncEventDispatcher() throws InterruptedException, ExecutionException {
      final EventDispatcher<String> em = new SyncEventDispatcher<>();

      final AtomicLong listener1Count = new AtomicLong();
      final EventListener<String> listener1 = event -> listener1Count.incrementAndGet();

      assertThat(em.subscribe(listener1)).isTrue();
      assertThat(em.subscribe(listener1)).isFalse();

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
