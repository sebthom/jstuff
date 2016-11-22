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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.CircuitBreaker.AccessPermit;
import net.sf.jstuff.core.concurrent.CircuitBreaker.State;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CircuitBreakerTest extends TestCase {

    public void testCircuitBreaker() {
        final CircuitBreaker circuitBreaker = CircuitBreaker.builder() //
            .name("test") //
            .failureThreshold(2) //
            .failureExpiryPeriod(2, TimeUnit.SECONDS) //
            .resetPeriod(2, TimeUnit.SECONDS) //
            .build();

        final List<AccessPermit> issuedPermits = new ArrayList<AccessPermit>();

        /*
         * testing best case scenario
         */
        for (int i = 0; i < 100; i++) {
            issuedPermits.add(circuitBreaker.tryAcquire());
        }
        for (int i = 0; i < 100; i++) {
            issuedPermits.get(i).release();
        }
        issuedPermits.clear();

        /*
         * testing working on
         */
        AccessPermit permit = circuitBreaker.tryAcquire();
        permit.release();
        try {
            permit.reportFailure(new IllegalArgumentException());
            fail();
        } catch (final IllegalStateException ex) {
            // expected
        }
        try {
            permit.release();
            fail();
        } catch (final IllegalStateException ex) {
            // expected
        }

        /*
         * testing open-state
         */
        permit = circuitBreaker.tryAcquire();
        permit.reportFailure(new IllegalArgumentException()); // 1st error
        permit.release();

        permit = circuitBreaker.tryAcquire();
        permit.reportFailure(new IllegalArgumentException()); // 2nd error
        permit.release();

        assertTrue(circuitBreaker.getState() == State.OPEN);
        assertNull(circuitBreaker.tryAcquire());
        assertTrue(circuitBreaker.getState() == State.OPEN);

        /*
         * testing half-open state
         */
        Threads.sleep(3 * 1000);
        assertTrue(circuitBreaker.getState() == State.HALF_OPEN);

        permit = circuitBreaker.tryAcquire();
        assertNotNull(permit);
        assertTrue(circuitBreaker.getState() == State.HALF_OPEN);
        assertNull(circuitBreaker.tryAcquire());
        // not reporting success or failure, thus releasing the permit does not change the circuit breaker's state
        permit.release();
        assertTrue(circuitBreaker.getState() == State.HALF_OPEN);

        permit = circuitBreaker.tryAcquire();
        permit.reportSuccess();
        permit.release();
        assertTrue(circuitBreaker.getState() == State.CLOSE);

        /*
         * testing instant state switch
         */
        permit = circuitBreaker.tryAcquire();
        permit.reportFailure(new UnknownHostException("foo.bar"));
        permit.release();
        assertTrue(circuitBreaker.getState() == State.OPEN);
    }
}
