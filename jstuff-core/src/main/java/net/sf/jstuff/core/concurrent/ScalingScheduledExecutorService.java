/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.jstuff.core.ref.MutableRef;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class ScalingScheduledExecutorService extends ScalingThreadPoolExecutor implements ScheduledExecutorService {

   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

   public ScalingScheduledExecutorService(final int minPoolSize, final int maxPoolSize, final long keepAliveTime, final TimeUnit unit) {
      super(minPoolSize, maxPoolSize, keepAliveTime, unit);
   }

   @Override
   public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
      final long startAt = System.currentTimeMillis();
      if (!scheduler.awaitTermination(timeout, unit))
         return false;
      final long elapsed = System.currentTimeMillis() - startAt;
      return super.awaitTermination(unit.toMillis(timeout) - elapsed, TimeUnit.MILLISECONDS);
   }

   private <V> ScheduledFuture<V> createDelegatingFuture(final ScheduledFuture<?> scheduledFuture, final MutableRef<Future<V>> workFuture) {
      return new ScheduledFuture<V>() {
         @Override
         public boolean cancel(final boolean mayInterruptIfRunning) {
            final Future<V> f = workFuture.get();
            if (f == null)
               return scheduledFuture.cancel(mayInterruptIfRunning);
            return f.cancel(mayInterruptIfRunning);
         }

         @Override
         public int compareTo(final Delayed o) {
            return scheduledFuture.compareTo(o);
         }

         @Override
         public V get() throws InterruptedException, ExecutionException {
            scheduledFuture.get();
            return workFuture.get().get();
         }

         @Override
         public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            final long startAt = System.currentTimeMillis();
            scheduledFuture.get(timeout, unit);
            final long elapsed = System.currentTimeMillis() - startAt;
            return workFuture.get().get(unit.toMillis(timeout) - elapsed, TimeUnit.MILLISECONDS);
         }

         @Override
         public long getDelay(final TimeUnit unit) {
            return scheduledFuture.getDelay(unit);
         }

         @Override
         public boolean isCancelled() {
            final Future<V> f = workFuture.get();
            if (f == null)
               return scheduledFuture.isCancelled();
            return f.isCancelled();
         }

         @Override
         public boolean isDone() {
            final Future<V> f = workFuture.get();
            if (f == null)
               return scheduledFuture.isDone();
            return f.isDone();
         }
      };
   }

   @Override
   public boolean isShutdown() {
      return scheduler.isShutdown() && super.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return scheduler.isTerminated() && super.isTerminated();
   }

   @Override
   public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
      final MutableRef<Future<V>> workFuture = new MutableRef<>();

      final ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
         workFuture.set(submit(callable));
         return null;
      }, delay, unit);

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
      final MutableRef<Future<Object>> workFuture = new MutableRef<>();

      @SuppressWarnings("unchecked")
      final ScheduledFuture<?> scheduledFuture = scheduler.schedule(() -> {
         workFuture.set((Future<Object>) submit(command));
         return null;
      }, delay, unit);

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
      final MutableRef<Future<Object>> workFuture = new MutableRef<>();

      @SuppressWarnings("unchecked")
      final ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate( //
         () -> workFuture.set((Future<Object>) submit(command)), //
         initialDelay, period, unit //
      );

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
      final TimeUnit unit) {
      final MutableRef<Future<Object>> workFuture = new MutableRef<>();

      @SuppressWarnings("unchecked")
      final ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay( //
         () -> workFuture.set((Future<Object>) submit(command)), //
         initialDelay, delay, unit //
      );

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public void shutdown() {
      scheduler.shutdown();
      super.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      final List<Runnable> runnables = scheduler.shutdownNow();
      runnables.addAll(super.shutdownNow());
      return runnables;
   }

}
