/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.rmi.UnknownHostException;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.CircuitBreaker.State;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CircuitBreakerTest extends TestCase {

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
         assertTrue(cb.tryAcquire());
      }
      for (int i = 0; i < 100; i++) {
         cb.release();
      }
      assertTrue(cb.tryExecute(() -> { /* */ }));

      /*
       * testing open-state
       */
      assertTrue(cb.tryAcquire());
      cb.reportFailure(new IllegalArgumentException()); // 1st error
      cb.release();

      assertTrue(cb.tryAcquire());
      cb.reportFailure(new IllegalArgumentException()); // 2nd error
      cb.release();

      assertEquals(cb.getState(), State.OPEN);
      assertFalse(cb.tryAcquire());
      assertFalse(cb.tryExecute(() -> { /* */ }));
      assertEquals(cb.getState(), State.OPEN);

      /*
       * testing half-open state
       */
      Threads.sleep(3 * 1000);
      assertEquals(cb.getState(), State.HALF_OPEN);

      assertTrue(cb.tryAcquire());
      assertEquals(cb.getState(), State.HALF_OPEN);
      assertFalse(cb.tryAcquire());
      // not reporting success or failure, thus releasing the permit does not change the circuit breaker's state
      cb.release();
      assertEquals(cb.getState(), State.HALF_OPEN);

      assertTrue(cb.tryAcquire());
      cb.reportSuccess();
      cb.release();
      assertEquals(cb.getState(), State.CLOSE);

      /*
       * testing instant state switch
       */
      assertTrue(cb.tryAcquire());
      cb.reportFailure(new UnknownHostException("foo.bar"));
      cb.release();
      assertEquals(cb.getState(), State.OPEN);
   }
}
