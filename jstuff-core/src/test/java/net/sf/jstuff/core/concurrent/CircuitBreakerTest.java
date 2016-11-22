/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
        assertTrue(cb.tryExecute(new Runnable() {
            public void run() {
            }
        }));

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
        assertFalse(cb.tryExecute(new Runnable() {
            public void run() {
            }
        }));
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
