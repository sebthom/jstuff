/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public final class ThrottlingEventDispatcher<EVENT> extends AbstractRateLimitingEventDispatcher<EVENT> implements AutoCloseable {

   private final class ThrottledEvent {
      private final EVENT event;
      private final CompletableFuture<Integer> resultFuture = new CompletableFuture<>();

      ThrottledEvent(final EVENT event) {
         this.event = event;
      }

      void fireEvent() {
         if (rateLimitedEvents_.remove(eventKeyProvider.apply(event), this)) {
            try {
               delegate.fire(event).thenAccept(resultFuture::complete);
            } catch (final Exception ex) {
               resultFuture.completeExceptionally(ex);
            }
         }
      }
   }

   @SuppressWarnings("hiding")
   public abstract static class Builder<EVENT> {
      protected @Nullable EventDispatcher<EVENT> delegate;
      protected @Nullable ScheduledExecutorService scheduler;
      protected @Nullable Function<EVENT, Object> eventKeyProvider;

      public Builder<EVENT> delegate(final EventDispatcher<EVENT> value) {
         delegate = value;
         return this;
      }

      /**
       * All events with the same event key are throttled. By default object identity is used as event key.
       */
      public Builder<EVENT> eventKeyProvider(final Function<EVENT, Object> value) {
         eventKeyProvider = value;
         return this;
      }

      public Builder<EVENT> scheduler(final ScheduledExecutorService value) {
         scheduler = value;
         return this;
      }

      public abstract ThrottlingEventDispatcher<EVENT> build();
   }

   /**
    * @param eventType the type of events accepted by this dispatcher. This parameter is only used as a workaround for Java's generic type interference
    *           limitations.
    */
   public static <EVENT> Builder<EVENT> builder(@SuppressWarnings("unused") final Class<EVENT> eventType, final Duration interval) {
      return new Builder<>() {
         @Override
         public ThrottlingEventDispatcher<EVENT> build() {
            return new ThrottlingEventDispatcher<>(interval, delegate, eventKeyProvider, scheduler);
         }
      };
   }

   private volatile ConcurrentMap<Object /* EventKey */, ThrottledEvent> rateLimitedEvents = new ConcurrentHashMap<>();
   private ConcurrentMap<Object /* EventKey */, ThrottledEvent> rateLimitedEvents_ = rateLimitedEvents;
   private final ScheduledFuture<?> scheduled;
   private final long intervalMS;

   /**
    * Use {@link #builder(Class, Duration)} to create configurable instances.
    */
   public ThrottlingEventDispatcher(final Duration interval) {
      this(interval, null, null, null);
   }

   private ThrottlingEventDispatcher(//
      final Duration interval, //
      final @Nullable EventDispatcher<EVENT> delegate, //
      final @Nullable Function<EVENT, Object> eventKeyProvider, //
      final @Nullable ScheduledExecutorService scheduler //
   ) {
      super(delegate, eventKeyProvider, scheduler);

      intervalMS = interval.toMillis();
      Args.greaterThan("interval", intervalMS, 0);

      scheduled = this.scheduler.scheduleAtFixedRate(() -> {
         if (rateLimitedEvents.isEmpty())
            return;
         rateLimitedEvents_ = rateLimitedEvents;
         rateLimitedEvents = new ConcurrentHashMap<>();
         rateLimitedEvents_.values().forEach(ThrottledEvent::fireEvent);
         rateLimitedEvents_ = rateLimitedEvents;
      }, 0, intervalMS, TimeUnit.MILLISECONDS);
   }

   @Override
   public void close() {
      scheduled.cancel(true);
   }

   /**
    * @return future that will hold the number of notified event listeners
    */
   @Override
   public CompletableFuture<Integer> fire(final EVENT event) {
      if (scheduled.isCancelled())
         throw new IllegalStateException("This event dispatcher is closed.");
      return rateLimitedEvents.computeIfAbsent(eventKeyProvider.apply(event), throttledEvent -> new ThrottledEvent(event)).resultFuture;
   }
}
