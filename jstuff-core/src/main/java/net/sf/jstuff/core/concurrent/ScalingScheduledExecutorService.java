/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.ref.MutableRef;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class ScalingScheduledExecutorService extends ScalingThreadPoolExecutor implements ScheduledExecutorService {

   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

   public ScalingScheduledExecutorService(final int minPoolSize, final int maxPoolSize, final Duration keepAliveTime) {
      super(minPoolSize, maxPoolSize, keepAliveTime);
   }

   public ScalingScheduledExecutorService(final int minPoolSize, final int maxPoolSize, final Duration keepAliveTime,
         final ThreadFactory threadFactory) {
      super(minPoolSize, maxPoolSize, keepAliveTime, threadFactory);
   }

   @Override
   public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
      final long startAt = System.currentTimeMillis();
      if (!scheduler.awaitTermination(timeout, unit))
         return false;
      final long elapsed = System.currentTimeMillis() - startAt;
      return super.awaitTermination(unit.toMillis(timeout) - elapsed, TimeUnit.MILLISECONDS);
   }

   private <V> ScheduledFuture<V> createDelegatingFuture(final ScheduledFuture<?> scheduledFuture,
         final MutableRef<@Nullable Future<V>> workFuture) {

      return new ScheduledFuture<>() {
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
            final Future<V> f = workFuture.get();
            if (f == null)
               throw new IllegalStateException("workFuture.get() returned null.");
            return f.get();
         }

         @Override
         public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            final long startAt = System.currentTimeMillis();
            scheduledFuture.get(timeout, unit);
            final long elapsed = System.currentTimeMillis() - startAt;
            final Future<V> f = workFuture.get();
            if (f == null)
               throw new IllegalStateException("workFuture.get() returned null.");
            return f.get(unit.toMillis(timeout) - elapsed, TimeUnit.MILLISECONDS);
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
      final var workFuture = MutableRef.of((@Nullable Future<V>) null);

      final var scheduledFuture = scheduler.schedule(() -> {
         workFuture.set(submit(callable));
         return null;
      }, delay, unit);

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
      final var workFuture = MutableRef.of((@Nullable Future<Object>) null);

      @SuppressWarnings("unchecked")
      final var scheduledFuture = scheduler.schedule(() -> {
         workFuture.set((Future<Object>) submit(command));
         return null;
      }, delay, unit);

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
      final var workFuture = MutableRef.of((@Nullable Future<Object>) null);

      @SuppressWarnings("unchecked")
      final var scheduledFuture = scheduler.scheduleAtFixedRate( //
         () -> workFuture.set((Future<Object>) submit(command)), //
         initialDelay, period, unit //
      );

      return createDelegatingFuture(scheduledFuture, workFuture);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
         final TimeUnit unit) {
      final var workFuture = MutableRef.of((@Nullable Future<Object>) null);

      @SuppressWarnings("unchecked")
      final var scheduledFuture = scheduler.scheduleWithFixedDelay( //
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
