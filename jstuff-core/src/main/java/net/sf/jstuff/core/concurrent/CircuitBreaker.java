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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.concurrent.CircuitBreaker.State;
import net.sf.jstuff.core.event.EventDispatcher;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Assert;

/**
 * Basic circuit breaker implementation.
 * <p>
 * Basic usage:
 *
 * <pre>
 * {
 *     CircuitBreaker cb = CircuitBreaker.builder() //
 *         .name("ldap-access") //
 *         .failureThreshold(2) //
 *         .failureExpiryPeriod(2, TimeUnit.SECONDS) //
 *         .resetPeriod(2, TimeUnit.SECONDS) //
 *         .build();
 *
 *     AccessPermit permit = cb.tryAcquire();
 *     if (permit == null) {
 *         throw new IllegaStateException("LDAP service is not available");
 *     }
 *     try {
 *         // query LDAP here
 *
 *         permit.reportSuccess();
 *     } catch (Exception ex) {
 *         permit.reportError(ex);
 *     } finally {
 *         permit.release();
 *     }
 * }
 * </pre>
 * <p>
 * See:
 * <ul>
 * <li><a href="http://martinfowler.com/bliki/CircuitBreaker.html">http://martinfowler.com/bliki/CircuitBreaker.html</a>.
 * <li><a href="http://doc.akka.io/docs/akka/current/common/circuitbreaker.html">http://doc.akka.io/docs/akka/current/common/circuitbreaker.html</a>
 * </ul>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class CircuitBreaker implements EventListenable<State> {

    @NotThreadSafe
    public static final class AccessPermit {
        private Boolean isAccessFailed = null;
        private CircuitBreaker cb;

        private AccessPermit(final CircuitBreaker cb) {
            this.cb = cb;
        }

        /**
         * @throws IllegalStateException if the {@link #release()} was called already
         */
        public void release() {
            Assert.notNull(cb, "This permit was already released!");
            cb.release(this);
            cb = null;
        }

        /**
         * @throws IllegalStateException if the {@link #release()} was called already
         */
        public void reportFailure() {
            reportFailure(null);
        }

        /**
         * @throws IllegalStateException if the {@link #release()} was called already
         */
        public void reportFailure(final Throwable ex) {
            Assert.notNull(cb, "This permit was already released!");
            isAccessFailed = true;
            cb.reportFailure(ex);
        }

        public void reportSuccess() {
            Assert.notNull(cb, "This permit was already released!");
            isAccessFailed = false;
        }
    }

    @Builder.Property(required = true)
    public interface CircuitBreakerBuilder extends Builder<CircuitBreaker> {

        /**
         * Event dispatcher instance that shall be used. Default is a {@link SyncEventDispatcher} instance.
         */
        @Builder.Property(required = false)
        CircuitBreakerBuilder eventDisptacher(EventDispatcher<State> value);

        /**
         * time frame in which the subsequent errors must occur
         */
        CircuitBreakerBuilder failureExpiryPeriod(int value, final TimeUnit timeUnit);

        /**
         * number of subsequent errors that result into switching to {@link State#OPEN}
         */
        CircuitBreakerBuilder failureThreshold(int value);

        /**
         * This circuit breaker's name used for logging purposes.
         */
        CircuitBreakerBuilder name(String value);

        /**
         * amount of time the circuit breaker stays in {@link State#OPEN} before switching to {@link State#HALF_OPEN}
         */
        CircuitBreakerBuilder resetPeriod(int value, final TimeUnit timeUnit);

    }

    public enum State {

        /**
         * all requests are denied
         */
        OPEN,

        /**
         * only a limited number of requests at a time is allowed
         */
        HALF_OPEN,

        /**
         * circuit breaker "CLOSE" state: all requests are allowed
         */
        CLOSE;
    }

    private static final Logger LOG = Logger.create();

    public static CircuitBreakerBuilder builder() {
        return BuilderFactory.of(CircuitBreakerBuilder.class).create();
    }

    protected final Object synchronizer = new Object();

    protected String name;

    protected long failureExpiryPeriodMS;
    protected int failureThreshold;
    protected final List<Long> failureTimestamps = new ArrayList<Long>();
    protected long resetPeriodMS;

    /**
     * date in MS until when throttling is active
     */
    protected long denyUntil = 0;

    protected final List<AccessPermit> issuedPermits = new ArrayList<AccessPermit>();

    private final EventDispatcher<State> eventDispatcher = new SyncEventDispatcher<State>();

    private State state = State.CLOSE;

    public State getState() {
        synchronized (synchronizer) {
            if (state == State.OPEN && System.currentTimeMillis() > denyUntil) {
                switchTo(State.HALF_OPEN);
            }
            return state;
        }
    }

    /**
     * Determines if the given exception shall result in an instant switch to {@link State#OPEN} ignoring the failure threshold.
     */
    protected boolean isFatalException(final Throwable ex) {
        final boolean rc = ex instanceof java.net.UnknownHostException || ex instanceof java.rmi.UnknownHostException;
        return rc;
    }

    protected void release(final AccessPermit permit) {
        synchronized (synchronizer) {
            issuedPermits.remove(permit);

            if (permit.isAccessFailed == Boolean.FALSE) {
                switchTo(State.CLOSE);
            }
        }
    }

    protected void reportFailure(final Throwable ex) {
        final long now = System.currentTimeMillis();

        synchronized (synchronizer) {

            /*
             * ignore expired failures
             */
            if (!failureTimestamps.isEmpty()) {
                final long expireFailuresBefore = now - failureExpiryPeriodMS;
                for (final Iterator<Long> it = failureTimestamps.iterator(); it.hasNext();) {
                    final Long failureTimestamp = it.next();
                    if (failureTimestamp.longValue() < expireFailuresBefore) {
                        it.remove();
                    }
                }
            }

            failureTimestamps.add(now);

            if (failureTimestamps.size() >= failureThreshold) {
                LOG.warn("[%s] Switching to [%s] because failure threshold [%s] was reached...", name, State.OPEN, failureThreshold);
                denyUntil = now + resetPeriodMS;
                switchTo(State.OPEN);
            } else if (ex != null && isFatalException(ex)) {
                LOG.warn("[%s] Switching to [%s] because of fatal exception [%s]...", name, State.OPEN, ex);
                denyUntil = now + resetPeriodMS;
                switchTo(State.OPEN);
            }
        }
    }

    /**
     * used by {@link CircuitBreakerBuilder}
     */
    protected void setFailureExpiryPeriod(final int time, final TimeUnit timeUnit) {
        failureExpiryPeriodMS = timeUnit.toMillis(time);
    }

    /**
     * used by {@link CircuitBreakerBuilder}
     */
    protected void setResetPeriod(final int time, final TimeUnit timeUnit) {
        resetPeriodMS = timeUnit.toMillis(time);
    }

    public boolean subscribe(final EventListener<State> listener) {
        return eventDispatcher.subscribe(listener);
    }

    protected void switchTo(final State state) {
        if (state == this.state)
            return;

        LOG.info("[%s] Switching to [%s]...", name, state);
        this.state = state;
        switch (state) {
            case OPEN:
                break;
            case HALF_OPEN:
                denyUntil = 0;
                break;
            case CLOSE:
                failureTimestamps.clear();
                denyUntil = 0;
                break;
        }
        eventDispatcher.fire(state);
    }

    /**
     * <b>IMPORTANT:</b> Do not forget to call the {@link AccessPermit#release()} once you are done accessing the resource.
     *
     * @return null if no permits are currently available
     */
    public AccessPermit tryAcquire() {
        synchronized (synchronizer) {
            switch (state) {
                case OPEN:
                    if (System.currentTimeMillis() < denyUntil)
                        return null;
                    switchTo(State.HALF_OPEN);
                    // now fall through "case HALF_OPEN:"
                case HALF_OPEN:
                    if (issuedPermits.size() > 0)
                        return null;
                case CLOSE:
                    // nothing
                    break;
            }

            final AccessPermit permit = new AccessPermit(this);
            issuedPermits.add(permit);
            return permit;
        }

    }

    public boolean unsubscribe(final EventListener<State> listener) {
        return eventDispatcher.unsubscribe(listener);
    }
}
