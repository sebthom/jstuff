/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.rmi.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.CircuitBreaker.State;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CircuitBreakerTest {

   @Test
   public void testCircuitBreaker() {
      @SuppressWarnings("unchecked")
      final CircuitBreaker cb = CircuitBreaker.builder() //
         .name("test") //
         .failureThreshold(2) //
         .failureTrackingPeriod(2, TimeUnit.SECONDS) //
         .resetPeriod(2, TimeUnit.SECONDS) //
         .hardTrippingExceptionTypes(UnknownHostException.class) //
         .build();

      /*
       * testing best case scenario
       */
      for (int i = 0; i < 100; i++) {
         assertThat(cb.tryAcquire()).isTrue();
      }
      for (int i = 0; i < 100; i++) {
         cb.release();
      }
      assertThat(cb.tryExecute(() -> { /* */ })).isTrue();

      /*
       * testing open-state
       */
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException()); // 1st error
      cb.release();

      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException()); // 2nd error
      cb.release();

      assertThat(cb.getState()).isEqualTo(State.OPEN);
      assertThat(cb.tryAcquire()).isFalse();
      assertThat(cb.tryExecute(() -> { /* */ })).isFalse();
      assertThat(cb.getState()).isEqualTo(State.OPEN);

      /*
       * testing half-open state
       */
      Threads.sleep(3 * 1000);
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);

      assertThat(cb.tryAcquire()).isTrue();
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);
      assertThat(cb.tryAcquire()).isFalse();
      // not reporting success or failure, thus releasing the permit does not change the circuit breaker's state
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);

      assertThat(cb.tryAcquire()).isTrue();
      cb.reportSuccess();
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.CLOSE);

      /*
       * testing instant state switch
       */
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new UnknownHostException("foo.bar"));
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.OPEN);
   }
}
