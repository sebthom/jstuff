/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.concurrent.CircuitBreaker.State;
import net.sf.jstuff.core.event.EventDispatcher;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.fluent.Fluent;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * See:
 * <ul>
 * <li><a href="http://martinfowler.com/bliki/CircuitBreaker.html">http://martinfowler.com/bliki/CircuitBreaker.html</a>
 * <li><a href=
 * "http://doc.akka.io/docs/akka/current/common/circuitbreaker.html">http://doc.akka.io/docs/akka/current/common/circuitbreaker.html</a>
 * <li><a href=
 * "https://docs.wso2.com/display/MSF4J200/Implementing+a+Circuit+Breaker">https://docs.wso2.com/display/MSF4J200/Implementing+a+Circuit+Breaker</a>
 * </ul>
 *
 * <p>
 * Example:
 *
 * <pre>
 * CircuitBreaker cb = CircuitBreaker.builder() //
 *    .name("ldap-access") //
 *    .failureThreshold(3) //
 *    .failureTrackingPeriod(10, TimeUnit.SECONDS) //
 *    .resetPeriod(30, TimeUnit.SECONDS) //
 *    .hardTrippingExceptionTypes(java.net.UnknownHostException.class) //
 *    .maxConcurrent(20) //
 *    .build();
 *
 * // OPTION 1:
 * if (cb.tryAcquire()) {
 *    try {
 *       // query LDAP here
 *
 *       cb.reportSuccess();
 *    } catch (Exception ex) {
 *       cb.reportError(ex);
 *    } finally {
 *       cb.release(); // IMPORTANT: always release after acquire was successful
 *    }
 * } else
 *    throw new IllegalStateException("LDAP service is not available");
 *
 * // OPTION 2:
 * Runnable ldapQuery = new Runnable() {
 *    public void run() {
 *       // query LDAP here
 *    }
 * };
 * if (!cb.tryExecute(ldapQuery))
 *    throw new IllegalStateException("LDAP service is not available");
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class CircuitBreaker implements EventListenable<State> {

   @Builder.Property(required = true)
   public interface CircuitBreakerBuilder extends Builder<CircuitBreaker> {

      /**
       * Event dispatcher instance that shall be used.
       */
      @Fluent
      @Builder.Property(required = false)
      CircuitBreakerBuilder eventDispatcher(EventDispatcher<State> value);

      /**
       * Number of subsequent errors that trip {@link State#OPEN}.
       */
      @Fluent
      CircuitBreakerBuilder failureThreshold(int value);

      /**
       * Time span in which the subsequent errors must occur.
       */
      @Fluent
      CircuitBreakerBuilder failureTrackingPeriod(int value, TimeUnit timeUnit);

      /**
       * Types of exceptions that trip {@link State#OPEN} instantly ignoring the failure threshold.
       */
      @Fluent
      @Builder.Property(required = false)
      CircuitBreakerBuilder hardTrippingExceptionTypes(@SuppressWarnings("unchecked") Class<? extends Throwable>... value);

      /**
       * Maximum number of issued permits at the same time while in {@link State#CLOSE}.
       * <p>
       * Default is {@link Integer#MAX_VALUE} indicating no limitation.
       */
      @Fluent
      @Builder.Property(required = false)
      CircuitBreakerBuilder maxConcurrent(int value);

      /**
       * The circuit breaker's name used for logging purposes.
       */
      @Fluent
      CircuitBreakerBuilder name(String value);

      /**
       * Amount of time the circuit breaker stays in {@link State#OPEN} before switching to {@link State#HALF_OPEN}
       */
      @Fluent
      CircuitBreakerBuilder resetPeriod(int value, TimeUnit timeUnit);
   }

   public enum State {
      /**
       * Permits are issued as long {@link CircuitBreaker#getMaxConcurrent()} is not reached.
       */
      CLOSE,

      /**
       * Only one permit at a time is issued.
       */
      HALF_OPEN,

      /**
       * No permits are issued.
       */
      OPEN
   }

   private static final Logger LOG = Logger.create();

   public static CircuitBreakerBuilder builder() {
      return BuilderFactory.of(CircuitBreakerBuilder.class).create();
   }

   protected CircuitBreaker() {
   }

   protected int activePermits;

   @Nullable
   protected EventDispatcher<State> eventDispatcher;
   protected int failureThreshold;
   protected List<Long> failureTimestamps = new ArrayList<>();
   protected long failureTrackingPeriodMS;

   protected Class<? extends Throwable> @Nullable [] hardTrippingExceptionTypes;
   protected long inOpenStateUntil = -1;
   protected int maxConcurrent = Integer.MAX_VALUE;
   protected String name = lateNonNull();
   protected long resetPeriodMS;
   protected State state = State.CLOSE;
   protected Object synchronizer = new Object();
   protected int tripCount;

   public int getActivePermits() {
      return activePermits;
   }

   public int getMaxConcurrent() {
      return maxConcurrent;
   }

   public State getState() {
      synchronized (synchronizer) {
         if (state == State.OPEN && System.currentTimeMillis() > inOpenStateUntil) {
            switchToHALF_OPEN();
         }
         return state;
      }
   }

   public int getTripCount() {
      return tripCount;
   }

   protected boolean isFatalException(final Throwable ex) {
      final var hardTrippingExceptionTypes = this.hardTrippingExceptionTypes;
      if (hardTrippingExceptionTypes == null || hardTrippingExceptionTypes.length == 0)
         return false;

      for (final Class<? extends Throwable> fex : hardTrippingExceptionTypes) {
         if (fex.isInstance(ex))
            return true;
      }
      return false;
   }

   /**
    * Releases 1 permit. Call this method after {@link #tryAcquire()} returned <code>true</code>.
    */
   public void release() {
      synchronized (synchronizer) {
         if (activePermits > 0) {
            activePermits--;
         } else {
            LOG.warn("An attempt was made to release a permit but no permits have been issued.");
         }
      }
   }

   /**
    * Increments the subsequent failures counter.
    */
   public void reportFailure() {
      reportFailure(null);
   }

   /**
    * Increments the subsequent failures counter.
    */
   public void reportFailure(final @Nullable Throwable ex) {
      final long now = System.currentTimeMillis();

      synchronized (synchronizer) {

         if (activePermits < 1) {
            LOG.warn("An attempt was made to report a failure but no permits have been issued.");
            return;
         }

         /*
          * dropping recorded failures outside the tracking time window
          */
         if (!failureTimestamps.isEmpty()) {
            final long expireFailuresBefore = now - failureTrackingPeriodMS;
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
            LOG.warn("[%s] Tripping [%s] because failure threshold [%s] was reached...", name, State.OPEN, failureThreshold);
            switchToOPEN(now);
            return;
         }

         /*
          * fatal exception?
          */
         if (ex != null && isFatalException(ex)) {
            LOG.warn("[%s] Hard tripping [%s] because of fatal exception [%s]...", name, State.OPEN, ex);
            switchToOPEN(now);
         }
      }
   }

   /**
    * Resets the subsequent failures counter and switches the circuit breaker to {@link State#CLOSE} when currently in
    * {@link State#HALF_OPEN}
    */
   public void reportSuccess() {
      synchronized (synchronizer) {
         if (activePermits > 0) {
            final State state = getState();
            if (state == State.HALF_OPEN) {
               switchToCLOSE();
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
   protected void setFailureTrackingPeriod(final int time, final TimeUnit timeUnit) {
      failureTrackingPeriodMS = timeUnit.toMillis(time);
   }

   /**
    * used by {@link CircuitBreakerBuilder}
    */
   protected void setResetPeriod(final int time, final TimeUnit timeUnit) {
      resetPeriodMS = timeUnit.toMillis(time);
   }

   @Override
   public boolean subscribe(final EventListener<State> listener) {
      Assert.notNull(eventDispatcher, "No eventDispatcher configured.");

      return asNonNull(eventDispatcher).subscribe(listener);
   }

   protected void switchToCLOSE() { // CHECKSTYLE:IGNORE AbbreviationAsWordInName
      if (state == State.CLOSE)
         return;

      LOG.info("[%s] Switching from [%s] to [%s]...", name, state, State.CLOSE);
      state = State.CLOSE;
      tripCount++;
      failureTimestamps.clear();

      if (eventDispatcher != null) {
         eventDispatcher.fire(state);
      }
   }

   protected void switchToHALF_OPEN() { // CHECKSTYLE:IGNORE AbbreviationAsWordInName
      if (state == State.HALF_OPEN)
         return;

      LOG.info("[%s] Switching from [%s] to [%s]...", name, state, State.HALF_OPEN);
      state = State.HALF_OPEN;

      if (eventDispatcher != null) {
         eventDispatcher.fire(state);
      }
   }

   protected void switchToOPEN(final long trippedAt) {
      if (state == State.OPEN)
         return;

      LOG.debug("[%s] Switching from [%s] to [%s]...", name, state, State.HALF_OPEN);
      state = State.OPEN;
      inOpenStateUntil = trippedAt + resetPeriodMS;

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
               if (activePermits > 0)
                  return false;
               break;
            case CLOSE:
               if (activePermits >= maxConcurrent)
                  return false;
         }
         activePermits++;
      }
      return true;
   }

   /**
    * Tries to execute the given runnable after acquiring a permit.
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
    */
   public boolean tryExecute(final Callable<?> callable) throws Exception {
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
    * @param errorTimeout if the execution of the code takes longer than <code>errorTimeout</code> and error is reported
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
    */
   public boolean tryExecute(final Callable<?> callable, final int errorTimeout, final TimeUnit timeUnit) throws Exception {
      Args.notNull("callable", callable);
      Args.notNull("timeUnit", timeUnit);

      if (!tryAcquire())
         return false;

      try {
         final long start = System.currentTimeMillis();
         callable.call();
         if (System.currentTimeMillis() - start > timeUnit.toMillis(errorTimeout)) {
            reportFailure();
         } else {
            reportSuccess();
         }
         return true;
      } catch (final Exception ex) {
         reportFailure(ex);
         throw ex;
      } finally {
         release();
      }
   }

   /**
    * Tries to execute the given invocable after acquiring a permit.
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
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
    * Tries to execute the given invocable after acquiring a permit.
    *
    * @param errorTimeout if the execution of the code takes longer than <code>errorTimeout</code> and error is reported
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
    */
   public <A, E extends Exception> boolean tryExecute(final Invocable<?, A, E> invocable, final A args, final int errorTimeout,
         final TimeUnit timeUnit) throws E {
      Args.notNull("invocable", invocable);
      Args.notNull("timeUnit", timeUnit);

      if (!tryAcquire())
         return false;

      try {
         final long start = System.currentTimeMillis();
         invocable.invoke(args);
         if (System.currentTimeMillis() - start > timeUnit.toMillis(errorTimeout)) {
            reportFailure();
         } else {
            reportSuccess();
         }
         return true;
      } catch (final RuntimeException ex) {
         reportFailure(ex);
         throw ex;
      } finally {
         release();
      }
   }

   /**
    * Tries to execute the given runnable after acquiring a permit.
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
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

   /**
    * Tries to execute the given runnable after acquiring a permit.
    *
    * @param errorTimeout if the execution of the code takes longer than <code>errorTimeout</code> and error is reported
    *
    * @return <code>true</code> if the code was executed. <code>false</code> if the code was not executed because no permit could be
    *         acquired.
    */
   public boolean tryExecute(final Runnable runnable, final int errorTimeout, final TimeUnit timeUnit) {
      Args.notNull("runnable", runnable);
      Args.notNull("timeUnit", timeUnit);

      if (!tryAcquire())
         return false;

      try {
         final long start = System.currentTimeMillis();
         runnable.run();
         if (System.currentTimeMillis() - start > timeUnit.toMillis(errorTimeout)) {
            reportFailure();
         } else {
            reportSuccess();
         }
         return true;
      } catch (final RuntimeException ex) {
         reportFailure(ex);
         throw ex;
      } finally {
         release();
      }
   }

   @Override
   public boolean unsubscribe(final EventListener<State> listener) {
      final var eventDispatcher = this.eventDispatcher;
      if (eventDispatcher == null)
         return false;

      return eventDispatcher.unsubscribe(listener);
   }
}
