/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SyncEventDispatcherTest {

   @Test
   public void testSyncEventDispatcher() {
      final var em = new SyncEventDispatcher<String>();

      final var listener1Count = new AtomicLong();
      final EventListener<String> listener1 = event -> listener1Count.incrementAndGet();

      assertThat(em.subscribe(listener1)).isTrue();
      assertThat(em.subscribe(listener1)).isFalse();

      final var listener2Count = new AtomicLong();
      final var listener2 = new FilteringEventListener<String>() {
         @Override
         public boolean accept(@Nullable final String event) {
            return event != null && event.length() < 5;
         }

         @Override
         public void onEvent(@Nullable final String event) {
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
