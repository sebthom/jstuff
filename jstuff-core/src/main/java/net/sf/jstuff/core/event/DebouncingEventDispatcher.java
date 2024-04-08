/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public final class DebouncingEventDispatcher<EVENT> extends AbstractRateLimitingEventDispatcher<EVENT> {

   private final class DebouncedEvent {
      private final EVENT event;
      private volatile long deadline;
      private volatile Future<?> scheduledFuture = lazyNonNull();
      private final CompletableFuture<Integer> resultFuture = new CompletableFuture<>();

      DebouncedEvent(final EVENT event, final long deadline) {
         this.event = event;
         this.deadline = deadline;
      }

      void fireEvent() {
         if (rateLimitedEvents.remove(eventKeyProvider.apply(event), this)) {
            try {
               delegate.fire(event).thenAccept(resultFuture::complete);
            } catch (final Exception ex) {
               resultFuture.completeExceptionally(ex);
            }
         }
      }
   }

   public abstract static class Builder<EVENT> {
      protected @Nullable EventDispatcher<EVENT> delegate;
      protected @Nullable ScheduledExecutorService scheduler;
      protected @Nullable Function<EVENT, Object> eventKeyProvider;

      public Builder<EVENT> delegate(final EventDispatcher<EVENT> value) {
         delegate = value;
         return this;
      }

      /**
       * All events with the same event key are debounced. By default object identity is used as event key.
       */
      public Builder<EVENT> eventKeyProvider(final Function<EVENT, Object> value) {
         eventKeyProvider = value;
         return this;
      }

      public Builder<EVENT> scheduler(final ScheduledExecutorService value) {
         scheduler = value;
         return this;
      }

      public abstract DebouncingEventDispatcher<EVENT> build();
   }

   /**
    * @param eventType the type of events accepted by this dispatcher. This parameter is only used as a workaround for Java's generic type interference
    *           limitations.
    */
   public static <EVENT> Builder<EVENT> builder(@SuppressWarnings("unused") final Class<EVENT> eventType, final Duration delay) {
      return new Builder<>() {
         @Override
         public DebouncingEventDispatcher<EVENT> build() {
            return new DebouncingEventDispatcher<>(delay, delegate, eventKeyProvider, scheduler);
         }
      };
   }

   private final ConcurrentMap<Object /* EventKey */, DebouncedEvent> rateLimitedEvents = new ConcurrentHashMap<>();
   private final long delayMS;

   /**
    * Use {@link #builder(Class, Duration)} to create configurable instances.
    */
   public DebouncingEventDispatcher(final Duration delay) {
      this(delay, null, null, null);
   }

   private DebouncingEventDispatcher( //
      final Duration delay, //
      final @Nullable EventDispatcher<EVENT> delegate, //
      final @Nullable Function<EVENT, Object> eventKeyProvider, //
      final @Nullable ScheduledExecutorService scheduler //
   ) {
      super(delegate, eventKeyProvider, scheduler);

      delayMS = delay.toMillis();
      Args.greaterThan("delay", delayMS, 0);
   }

   /**
    * @return future that will hold the number of notified event listeners
    */
   @Override
   public CompletableFuture<Integer> fire(final EVENT event) {
      final long deadline = System.currentTimeMillis() + delayMS;
      return rateLimitedEvents.compute(eventKeyProvider.apply(event), (k, debouncedEvent) -> {
         if (asNullable(debouncedEvent) == null) {
            debouncedEvent = new DebouncedEvent(event, deadline);
            final var remainingMS = Math.max(0, debouncedEvent.deadline - System.currentTimeMillis());
            debouncedEvent.scheduledFuture = scheduler.schedule(debouncedEvent::fireEvent, remainingMS, TimeUnit.MILLISECONDS);
         } else {
            debouncedEvent.scheduledFuture.cancel(false);
            debouncedEvent.deadline = deadline;

            final var remainingMS = debouncedEvent.deadline - System.currentTimeMillis();
            debouncedEvent.scheduledFuture = remainingMS < 1 //
               ? scheduler.submit(debouncedEvent::fireEvent) //
               : scheduler.schedule(debouncedEvent::fireEvent, //
                  remainingMS, TimeUnit.MILLISECONDS);
         }
         return debouncedEvent;
      }).resultFuture;
   }
}
