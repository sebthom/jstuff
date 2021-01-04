/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.jstuff.core.validation.Args;

/**
 * ExecutorService that blocks submission of new tasks if a given maximum number of tasks are already pending.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BlockingExecutorService extends BlockingExecutor implements ExecutorService {

   protected static final class CallableWrapper<T> implements Callable<T> {
      private final Callable<T> wrapped;
      private final Semaphore limiter;

      protected CallableWrapper(final Callable<T> wrapped, final Semaphore limiter) {
         this.wrapped = wrapped;
         this.limiter = limiter;
      }

      @Override
      public T call() throws Exception {
         try {
            return wrapped.call();
         } finally {
            limiter.release();
         }
      }
   }

   private final ExecutorService executorService;

   /**
    * @param maxWaitTime max time to wait for a tasks being added to the queue
    */
   public BlockingExecutorService(final ExecutorService executorService, final int maxQueueSize, final int maxWaitTime, final TimeUnit maxWaitTimeUnit) {
      super(executorService, maxQueueSize, maxWaitTime, maxWaitTimeUnit);
      this.executorService = executorService;
   }

   protected void aquirePermits(final int count) throws RejectedExecutionException {
      try {
         if (!limiter.tryAcquire(count, maxWaitTime, maxWaitTimeUnit))
            throw new RejectedExecutionException("Executor '" + wrapped + "' is busy!");
      } catch (final InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException(e);
      }
   }

   @Override
   public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
      return executorService.awaitTermination(timeout, unit);
   }

   @Override
   public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return executorService.invokeAll(wrapTasks(tasks));
   }

   @Override
   public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
      return executorService.invokeAll(wrapTasks(tasks), timeout, unit);
   }

   @Override
   public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return executorService.invokeAny(wrapTasks(tasks));
   }

   @Override
   public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException,
      ExecutionException, TimeoutException {
      return executorService.invokeAny(wrapTasks(tasks), timeout, unit);
   }

   @Override
   public boolean isShutdown() {
      return executorService.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return executorService.isTerminated();
   }

   @Override
   public void shutdown() {
      executorService.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      return executorService.shutdownNow();
   }

   @Override
   public <T> Future<T> submit(final Callable<T> task) throws RejectedExecutionException {
      Args.notNull("task", task);

      aquirePermit();

      try {
         return executorService.submit(new CallableWrapper<>(task, limiter));
      } catch (final RuntimeException ex) {
         limiter.release();
         throw ex;
      }
   }

   @Override
   public Future<?> submit(final Runnable task) throws RejectedExecutionException {
      Args.notNull("task", task);

      aquirePermit();

      try {
         return executorService.submit(new RunnableWrapper(task, limiter));
      } catch (final RuntimeException ex) {
         limiter.release();
         throw ex;
      }
   }

   @Override
   public <T> Future<T> submit(final Runnable task, final T result) throws RejectedExecutionException {
      Args.notNull("task", task);

      aquirePermit();

      try {
         return executorService.submit(new RunnableWrapper(task, limiter), result);
      } catch (final RuntimeException ex) {
         limiter.release();
         throw ex;
      }
   }

   protected <T> Collection<Callable<T>> wrapTasks(final Collection<? extends Callable<T>> tasks) throws RejectedExecutionException {
      Args.notNull("tasks", tasks);
      Args.noNulls("tasks", tasks);

      aquirePermits(tasks.size());

      final Collection<Callable<T>> result = new ArrayList<>(tasks.size());
      for (final Callable<T> task : tasks) {
         result.add(new CallableWrapper<>(task, limiter));
      }
      return result;
   }

}
