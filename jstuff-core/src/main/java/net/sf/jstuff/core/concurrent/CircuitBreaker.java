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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.concurrent.CircuitBreaker.State;
import net.sf.jstuff.core.event.EventDispatcher;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * See:
 * <ul>
 * <li><a href="http://martinfowler.com/bliki/CircuitBreaker.html">http://martinfowler.com/bliki/CircuitBreaker.html</a>.
 * <li><a href="http://doc.akka.io/docs/akka/current/common/circuitbreaker.html">http://doc.akka.io/docs/akka/current/common/circuitbreaker.html</a>
 * </ul>
 *
 * <p>
 * Example:
 *
 * <pre>
 * CircuitBreaker cb = CircuitBreaker.builder() //
 *     .name("ldap-access") //
 *     .failureThreshold(3) //
 *     .failureExpiryPeriod(10, TimeUnit.SECONDS) //
 *     .resetPeriod(30, TimeUnit.SECONDS) //
 *     .fatalExceptions(java.net.UnknownHostException.class) //
 *     .maxConcurrent(20) //
 *     .build();
 *
 * // OPTION 1:
 * Assert.isTrue(cb.tryAcquire(), "LDAP service is not available");
 * try {
 *     // query LDAP here
 *
 *     cb.reportSuccess();
 * } catch (Exception ex) {
 *     cb.reportError(ex);
 * } finally {
 *     cb.release(); // important!! always release after acquire
 * }
 *
 * // OPTION 2:
 * Runnable ldapQuery = new Runnable() {
 *     public void run() {
 *         // query LDAP here
 *     }
 * };
 * Assert.isTrue(cb.tryExecute(ldapQuery), "LDAP service is not available");
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class CircuitBreaker implements EventListenable<State> {

    @Builder.Property(required = true)
    public interface CircuitBreakerBuilder extends Builder<CircuitBreaker> {

        /**
         * Amount of time the circuit breaker stays in {@link State#OPEN} before switching to {@link State#HALF_OPEN}
         */
        CircuitBreakerBuilder blockingPeriod(int value, final TimeUnit timeUnit);

        /**
         * Event dispatcher instance that shall be used.
         */
        @Builder.Property(required = false)
        CircuitBreakerBuilder eventDisptacher(EventDispatcher<State> value);

        /**
         * Time span in which the subsequent errors must occur.
         */
        CircuitBreakerBuilder failureExpiryPeriod(int value, final TimeUnit timeUnit);

        /**
         * Number of subsequent errors that result into switching to {@link State#OPEN}.
         */
        CircuitBreakerBuilder failureThreshold(int value);

        /**
         * Exception types that result in an instant switch to {@link State#OPEN} even if the the failure threshold hasn't been reached yet.
         */
        //@SafeVarargs
        @Builder.Property(required = false)
        CircuitBreakerBuilder fatalExceptions(Class<? extends Throwable>... value);

        /**
         * Maximum number of issued permits at the same time while in {@link State#CLOSE}.
         * <p>
         * Default is 0 which indicates no limitation.
         */
        @Builder.Property(required = false)
        CircuitBreakerBuilder maxConcurrent(int value);

        /**
         * The circuit breaker's name used for logging purposes.
         */
        CircuitBreakerBuilder name(String value);
    }

    public enum State {
        /**
         * all requests are denied
         */
        OPEN,

        /**
         * only one request at a time is permitted
         */
        HALF_OPEN,

        /**
         * all requests are allowed
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
    protected Class<? extends Throwable>[] fatalExceptions;
    protected int maxConcurrent = 0;
    protected final List<Long> failureTimestamps = new ArrayList<Long>();
    protected long blockingPeriodMS;

    /**
     * date in MS until {@link State#OPEN} is active
     */
    protected long openStateUntil = 0;

    protected int issuedPermits = 0;

    protected EventDispatcher<State> eventDispatcher;

    private State state = State.CLOSE;

    public State getState() {
        synchronized (synchronizer) {
            if (state == State.OPEN && System.currentTimeMillis() > openStateUntil) {
                switchTo(State.HALF_OPEN);
            }
            return state;
        }
    }

    protected boolean isFatalException(final Throwable ex) {
        if (fatalExceptions == null || fatalExceptions.length == 0)
            return false;

        for (final Class<? extends Throwable> fex : fatalExceptions) {
            if (fex.isInstance(ex))
                return true;
        }
        return false;
    }

    /**
     * releases 1 permit
     */
    public void release() {
        synchronized (synchronizer) {
            if (issuedPermits > 0) {
                issuedPermits--;
            } else {
                LOG.warn("An attempt was made to release a permit but no permits have been issued.");
            }
        }
    }

    /**
     * increments the subsequent failures counter
     */
    public void reportFailure() {
        reportFailure(null);
    }

    /**
     * increments the subsequent failures counter
     */
    public void reportFailure(final Throwable ex) {
        final long now = System.currentTimeMillis();

        synchronized (synchronizer) {

            if (issuedPermits < 1) {
                LOG.warn("An attempt was made to report a failure but no permits have been issued.");
                return;
            }

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

            /*
             * failure threshold reached?
             */
            if (failureTimestamps.size() >= failureThreshold) {
                LOG.warn("[%s] Switching to [%s] because failure threshold [%s] was reached...", name, State.OPEN, failureThreshold);
                openStateUntil = now + blockingPeriodMS;
                switchTo(State.OPEN);
                return;
            }

            /*
             * fatal exception?
             */
            if (ex != null && isFatalException(ex)) {
                LOG.warn("[%s] Switching to [%s] because of fatal exception [%s]...", name, State.OPEN, ex);
                openStateUntil = now + blockingPeriodMS;
                switchTo(State.OPEN);
                return;
            }
        }
    }

    /**
     * resets the subsequent failures counter
     */
    public void reportSuccess() {
        synchronized (synchronizer) {
            if (issuedPermits > 0) {
                final State state = getState();
                if (state == State.HALF_OPEN) {
                    switchTo(State.CLOSE);
                } else {
                    // forget all previous failures
                    failureTimestamps.clear();
                }
            } else {
                LOG.warn("An attempt was made to report success but no permits have been issued.");
            }
        }
    }

    /**
     * used by {@link CircuitBreakerBuilder}
     */
    protected void setBlockingPeriod(final int time, final TimeUnit timeUnit) {
        blockingPeriodMS = timeUnit.toMillis(time);
    }

    /**
     * used by {@link CircuitBreakerBuilder}
     */
    protected void setFailureExpiryPeriod(final int time, final TimeUnit timeUnit) {
        failureExpiryPeriodMS = timeUnit.toMillis(time);
    }

    public boolean subscribe(final EventListener<State> listener) {
        Assert.notNull(eventDispatcher, "No eventDispatcher configured.");

        return eventDispatcher.subscribe(listener);
    }

    protected void switchTo(final State state) {
        if (state == this.state)
            return;

        LOG.debug("[%s] Switching from [%s] to [%s]...", name, this.state, state);
        this.state = state;
        switch (state) {
            case OPEN:
                break;
            case HALF_OPEN:
                LOG.info("[%s] Switching to [HALF_OPEN]...", name);
                openStateUntil = 0;
                break;
            case CLOSE:
                LOG.info("[%s] Switching to [CLOSE]...", name);
                failureTimestamps.clear();
                openStateUntil = 0;
                break;
        }

        if (eventDispatcher != null) {
            eventDispatcher.fire(state);
        }
    }

    /**
     * Acquires 1 permit.
     *
     * <b>IMPORTANT:</b> Do not forget to call the {@link #release()} when done.
     *
     * @return <code>false</code> if no permit was issued.
     */
    public boolean tryAcquire() {
        synchronized (synchronizer) {
            final State state = getState();
            switch (state) {
                case OPEN:
                    return false;
                case HALF_OPEN:
                    if (issuedPermits > 0)
                        return false;
                case CLOSE:
                    if (maxConcurrent > 0 && maxConcurrent >= issuedPermits)
                        return false;
            }
            issuedPermits++;
        }
        return true;
    }

    /**
     * Tries to execute the given runnable after acquiring a permit.
     *
     * @return <code>true</true> if the code was executed. <code>false</code> if the code was not executed because no permit could be acquired.
     */
    public boolean tryExecute(final Callable<Void> callable) throws Exception {
        Args.notNull("callable", callable);

        if (!tryAcquire())
            return false;

        try {
            callable.call();
            reportSuccess();
            return true;
        } catch (final Exception ex) {
            reportFailure(ex);
            throw ex;
        } finally {
            release();
        }
    }

    /**
     * Tries to execute the given runnable after acquiring a permit.
     *
     * @return <code>true</true> if the code was executed. <code>false</code> if the code was not executed because no permit could be acquired.
     */
    @SuppressWarnings("unchecked")
    public <A, E extends Exception> boolean tryExecute(final Invocable<?, A, E> invocable, final A args) throws E {
        Args.notNull("invocable", invocable);

        if (!tryAcquire())
            return false;

        try {
            invocable.invoke(args);
            reportSuccess();
            return true;
        } catch (final RuntimeException ex) {
            reportFailure(ex);
            throw ex;
        } catch (final Exception ex) {
            reportFailure(ex);
            throw (E) ex;
        } finally {
            release();
        }
    }

    /**
     * Tries to execute the given runnable after acquiring a permit.
     *
     * @return <code>true</true> if the code was executed. <code>false</code> if the code was not executed because no permit could be acquired.
     */
    public boolean tryExecute(final Runnable runnable) {
        Args.notNull("runnable", runnable);

        if (!tryAcquire())
            return false;

        try {
            runnable.run();
            reportSuccess();
            return true;
        } catch (final RuntimeException ex) {
            reportFailure(ex);
            throw ex;
        } finally {
            release();
        }
    }

    public boolean unsubscribe(final EventListener<State> listener) {
        if (eventDispatcher == null)
            return false;

        return eventDispatcher.unsubscribe(listener);
    }
}
