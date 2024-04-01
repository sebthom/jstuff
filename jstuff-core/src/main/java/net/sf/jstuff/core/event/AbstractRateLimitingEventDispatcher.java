/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.ScalingScheduledExecutorService;
import net.sf.jstuff.core.concurrent.ThreadSafe;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public abstract class AbstractRateLimitingEventDispatcher<EVENT> implements EventDispatcher<EVENT> {
   protected static final class LazyInitialized {
      protected static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = new ScalingScheduledExecutorService( //
         1, Math.max(1, Runtime.getRuntime().availableProcessors() - 1), //
         Duration.ofSeconds(5), //
         new BasicThreadFactory.Builder().daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
   }

   private static final Object NULL_EVENT_KEY = new Object();

   protected final EventDispatcher<EVENT> delegate;
   protected final ScheduledExecutorService scheduler;
   protected final Function<EVENT, Object> eventKeyProvider;

   protected AbstractRateLimitingEventDispatcher( //
      final @Nullable EventDispatcher<EVENT> delegate, //
      final @Nullable Function<EVENT, Object> eventKeyProvider, //
      final @Nullable ScheduledExecutorService scheduler //
   ) {
      this.scheduler = scheduler == null ? LazyInitialized.DEFAULT_NOTIFICATION_THREAD : scheduler;
      this.delegate = delegate == null ? new SyncEventDispatcher<>() : delegate;
      this.eventKeyProvider = eventKeyProvider == null ? e -> e == null ? NULL_EVENT_KEY : e : eventKeyProvider;
   }

   public EventDispatcher<EVENT> getDelegate() {
      return delegate;
   }

   public Function<EVENT, Object> getEventKeyProvider() {
      return eventKeyProvider;
   }

   public ScheduledExecutorService getScheduler() {
      return scheduler;
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
