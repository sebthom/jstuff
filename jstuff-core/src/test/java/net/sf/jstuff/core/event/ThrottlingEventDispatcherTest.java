/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThrottlingEventDispatcherTest {

   final int intervalMS = 100;
   final ThrottlingEventDispatcher<String> ed = new ThrottlingEventDispatcher<>(Duration.ofMillis(intervalMS));
   final AtomicLong eventCount = new AtomicLong();
   final EventListener<String> listener1 = event -> eventCount.incrementAndGet();

   @Before
   public void setup() {
      assertThat(ed.subscribe(listener1)).isTrue();
      assertThat(ed.subscribe(listener1)).isFalse();
      assertThat(eventCount.get()).isZero();
   }

   @After
   public void tearDown() {
      ed.close();
   }

   @Test
   public void testSingleEvent() throws InterruptedException, ExecutionException {
      final var future = ed.fire("123");

      Thread.sleep(intervalMS + 50);

      assertThat(eventCount.get()).isEqualTo(1);
      assertThat(future.get()).isEqualTo(1);
   }

   @Test
   public void testFireSameEventMultipleTimesWithinInterval() throws InterruptedException, ExecutionException {
      final var futureA = ed.fire("123");
      ed.fire("123");
      final var futureB = ed.fire("123");

      Thread.sleep(intervalMS + 50);

      assertThat(eventCount.get()).isEqualTo(1);
      assertThat(futureA.get()).isEqualTo(1);
      assertThat(futureA).isEqualTo(futureB);
   }

   @Test
   public void testFireSameEventMultipleTimesAcrossIntervals() throws InterruptedException {
      for (int i = 0; i < 5; i++) {
         Thread.sleep(intervalMS - 50);
         ed.fire("123");
      }

      Thread.sleep(intervalMS + 50);
      assertThat(eventCount.get()).isGreaterThan(1);
   }
}
