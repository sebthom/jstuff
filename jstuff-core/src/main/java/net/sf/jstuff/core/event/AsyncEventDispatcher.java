/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.sf.jstuff.core.concurrent.ScalingScheduledExecutorService;
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class AsyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {
   private static final class LazyInitialized {
      private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = new ScalingScheduledExecutorService( //
         1, Math.max(1, Runtime.getRuntime().availableProcessors() - 1), //
         Duration.ofSeconds(5), //
         BasicThreadFactory.builder().daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
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
   public CompletableFuture<Integer> fire(final EVENT type) {
      final EventListener<EVENT>[] copy = eventListeners.toArray(EventListener[]::new);
      return CompletableFuture.supplyAsync(() -> Events.fire(type, copy), executor);
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
