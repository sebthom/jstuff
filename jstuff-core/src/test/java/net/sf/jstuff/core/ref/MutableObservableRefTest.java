/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class MutableObservableRefTest {

   @Test
   @Timeout(value = 5, unit = TimeUnit.SECONDS)
   void testObservers() {
      final var ref = new MutableObservableRef.Default<>("foo");
      assertThat(ref.get()).isEqualTo("foo");
      assertThat(ref.isObserved()).isFalse();

      final var counter = new AtomicInteger();
      ref.subscribe((o, n) -> { // BiConsumer
         counter.incrementAndGet();
         assertThat(o).isEqualTo("foo");
         assertThat(n).isEqualTo("bar");
      });
      ref.subscribe(n -> { // Consumer
         counter.incrementAndGet();
         assertThat(n).isEqualTo("bar");
      });
      ref.subscribe(() -> { // Runnable
         counter.incrementAndGet();
      });

      // set different value
      assertThat(ref.isObserved()).isTrue();
      ref.set("bar");
      assertThat(ref.get()).isEqualTo("bar");
      assertThat(counter.get()).isEqualTo(3);

      // set identical value
      counter.set(0);
      ref.set("bar");
      assertThat(ref.get()).isEqualTo("bar");
      assertThat(counter.get()).isZero();

      // set value without observers
      ref.unsubscribeAll();
      counter.set(0);
      assertThat(ref.isObserved()).isFalse();
      ref.set("foo");
      assertThat(ref.get()).isEqualTo("foo");
      assertThat(counter.get()).isZero();
   }

   @Test
   @Timeout(value = 5, unit = TimeUnit.SECONDS)
   void testAwait() throws InterruptedException {
      final var ref = new MutableObservableRef.Default<>("foo");
      assertThat(ref.get()).isEqualTo("foo");

      CompletableFuture.runAsync(() -> {
         Threads.sleep(500);
         ref.set("bar");
      });

      assertThat(ref.await("bar", 0, TimeUnit.SECONDS)).isFalse();
      assertThat(ref.await("bar", 1, TimeUnit.SECONDS)).isTrue();
      ref.await("bar");
   }
}
