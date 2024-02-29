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

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.sf.jstuff.core.concurrent.ScalingScheduledExecutorService;
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class DebouncingEventDispatcher<EVENT> implements EventDispatcher<EVENT> {
   private static final class LazyInitialized {
      private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = new ScalingScheduledExecutorService( //
         1, Math.max(1, Runtime.getRuntime().availableProcessors() - 1), //
         Duration.ofSeconds(5), //
         new BasicThreadFactory.Builder().daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
   }

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
         if (debouncedEvents.remove(event, this)) {
            try {
               delegate.fire(event).thenAccept(resultFuture::complete);
            } catch (final Exception ex) {
               resultFuture.completeExceptionally(ex);
            }
         }
      }
   }

   private final EventDispatcher<EVENT> delegate;
   private final long delayMS;
   private final ScheduledExecutorService scheduler;
   private final ConcurrentMap<EVENT, DebouncedEvent> debouncedEvents = new ConcurrentHashMap<>();

   public DebouncingEventDispatcher(final Duration delay) {
      this(delay, LazyInitialized.DEFAULT_NOTIFICATION_THREAD);
   }

   public DebouncingEventDispatcher(final Duration delay, final ScheduledExecutorService scheduler) {
      this(delay, new SyncEventDispatcher<>(), scheduler);
   }

   public DebouncingEventDispatcher(final Duration delay, final EventDispatcher<EVENT> delegate) {
      this(delay, delegate, LazyInitialized.DEFAULT_NOTIFICATION_THREAD);
   }

   public DebouncingEventDispatcher(final Duration delay, final EventDispatcher<EVENT> delegate, final ScheduledExecutorService scheduler) {
      delayMS = delay.toMillis();
      Args.min("delay", delayMS, 1);
      this.scheduler = scheduler;
      this.delegate = delegate;
   }

   @Override
   public CompletableFuture<Integer> fire(final EVENT event) {
      final long deadline = System.currentTimeMillis() + delayMS;
      return debouncedEvents.compute(event, (k, debouncedEvent) -> {
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

   @Override
   public boolean subscribe(final EventListener<EVENT> listener) {
      return delegate.subscribe(listener);
   }

   @Override
   public boolean unsubscribe(final EventListener<EVENT> listener) {
      return delegate.unsubscribe(listener);
   }

   @Override
   public void unsubscribeAll() {
      delegate.unsubscribeAll();
   }
}
