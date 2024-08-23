/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableRefTest {

   @Test
   public void testObservableRef() {
      final var ref = new MutableObservableRef.Default<>("foo");
      assertThat(ref.get()).isEqualTo("foo");
      assertThat(ref.isObserved()).isFalse();

      final var counter = new AtomicInteger();
      ref.subscribe((o, n) -> {
         counter.incrementAndGet();
         assertThat(o).isEqualTo("foo");
         assertThat(n).isEqualTo("bar");
      });
      ref.subscribe(n -> {
         counter.incrementAndGet();
         assertThat(n).isEqualTo("bar");
      });
      ref.subscribe(() -> {
         counter.incrementAndGet();
      });

      // set different value
      assertThat(ref.isObserved()).isTrue();
      ref.set("bar");
      assertThat(ref.get()).isEqualTo("bar");
      assertThat(counter.get()).isEqualTo(3);

      // set same value
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
}
