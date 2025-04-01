/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.rmi.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.CircuitBreaker.State;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CircuitBreakerTest {

   @SuppressWarnings("unchecked")
   final CircuitBreaker cb = CircuitBreaker.builder() //
      .name("test") //
      .failureThreshold(2) //
      .failureTrackingPeriod(1, TimeUnit.SECONDS) //
      .resetPeriod(1, TimeUnit.SECONDS) //
      .hardTrippingExceptionTypes(UnknownHostException.class) //
      .build();

   @Test
   void testSuccessfulAcquisitionAndRelease() {
      // Best-case scenario: acquiring and releasing permits
      for (int i = 0; i < 100; i++) {
         assertThat(cb.tryAcquire()).isTrue();
      }
      for (int i = 0; i < 100; i++) {
         cb.release();
      }
      assertThat(cb.tryExecute(() -> { /* no-op */ })).isTrue();
   }

   @Test
   void testTransitionToOpen() {
      // Triggering OPEN state through failure reporting
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException()); // 1st error
      cb.release();

      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException()); // 2nd error
      cb.release();

      assertThat(cb.getTripCount()).isEqualTo(1);

      assertThat(cb.getState()).isEqualTo(State.OPEN);
      assertThat(cb.tryAcquire()).isFalse();
      assertThat(cb.tryExecute(() -> { /* no-op */ })).isFalse();
      assertThat(cb.getState()).isEqualTo(State.OPEN);
   }

   @Test
   void testTransitionToHalfOpenAndClose() throws InterruptedException {
      // Trigger OPEN state first
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException());
      cb.release();

      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new IllegalArgumentException());
      cb.release();

      // Wait for the reset period
      Thread.sleep(2_000);
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);

      // In HALF_OPEN state, only one permit should be available
      assertThat(cb.tryAcquire()).isTrue();
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);
      assertThat(cb.tryAcquire()).isFalse();
      // Not reporting success or failure, thus releasing the permit does not change the circuit breaker's state
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);

      // Reporting success should transition to CLOSE
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportSuccess();
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.CLOSE);
   }

   @Test
   void testHardTrippingException() {
      // Trigger immediate open with a fatal exception
      assertThat(cb.tryAcquire()).isTrue();
      cb.reportFailure(new UnknownHostException("foo.bar"));
      cb.release();
      assertThat(cb.getState()).isEqualTo(State.OPEN);
   }
}
