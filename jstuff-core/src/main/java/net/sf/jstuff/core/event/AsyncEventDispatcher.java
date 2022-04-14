/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class AsyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {
   private static final class LazyInitialized {
      private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
         .daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
   }

   private final Set<EventListener<EVENT>> eventListeners = new CopyOnWriteArraySet<>();

   private ExecutorService executor;

   public AsyncEventDispatcher() {
      this(LazyInitialized.DEFAULT_NOTIFICATION_THREAD);
   }

   public AsyncEventDispatcher(final ExecutorService executor) {
      Args.notNull("executor", executor);
      this.executor = executor;
   }

   @Override
   public Future<Integer> fire(final EVENT type) {
      final EventListener<EVENT>[] copy = eventListeners.toArray(new EventListener[eventListeners.size()]);
      return executor.submit(() -> Events.fire(type, copy));
   }

   @Override
   public boolean subscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.add(listener);
   }

   @Override
   public boolean unsubscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.remove(listener);
   }

   @Override
   public void unsubscribeAll() {
      eventListeners.clear();
   }
}
