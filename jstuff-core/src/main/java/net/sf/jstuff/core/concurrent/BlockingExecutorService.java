/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BlockingExecutorService extends BlockingExecutor implements ExecutorService {

   protected static final class CallableWrapper<T> implements Callable<T> {
      private final Callable<T> wrapped;
      private final Semaphore limiter;

      protected CallableWrapper(final Callable<T> wrapped, final Semaphore limiter) {
         this.wrapped = wrapped;
         this.limiter = limiter;
      }

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

   public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
      return executorService.awaitTermination(timeout, unit);
   }

   protected <T> Collection<Callable<T>> wrapTasks(final Collection<Callable<T>> tasks) throws RejectedExecutionException {
      Args.notNull("tasks", tasks);
      Args.noNulls("tasks", tasks);

      aquirePermits(tasks.size());

      final Collection<Callable<T>> result = new ArrayList<Callable<T>>(tasks.size());
      for (final Callable<T> task : tasks) {
         result.add(new CallableWrapper<T>(task, limiter));
      }
      return result;
   }

   public <T> List<Future<T>> invokeAll(final Collection<Callable<T>> tasks) throws InterruptedException, RejectedExecutionException {
      return executorService.invokeAll(wrapTasks(tasks));
   }

   public <T> List<Future<T>> invokeAll(final Collection<Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException,
      RejectedExecutionException {
      return executorService.invokeAll(wrapTasks(tasks), timeout, unit);
   }

   public <T> T invokeAny(final Collection<Callable<T>> tasks) throws InterruptedException, ExecutionException, RejectedExecutionException {
      return executorService.invokeAny(wrapTasks(tasks));
   }

   public <T> T invokeAny(final Collection<Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException,
      TimeoutException, RejectedExecutionException {
      return executorService.invokeAny(wrapTasks(tasks), timeout, unit);
   }

   public boolean isShutdown() {
      return executorService.isShutdown();
   }

   public boolean isTerminated() {
      return executorService.isTerminated();
   }

   public void shutdown() {
      executorService.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return executorService.shutdownNow();
   }

   public <T> Future<T> submit(final Callable<T> task) throws RejectedExecutionException {
      Args.notNull("task", task);

      aquirePermit();

      try {
         return executorService.submit(new CallableWrapper<T>(task, limiter));
      } catch (final RuntimeException ex) {
         limiter.release();
         throw ex;
      }
   }

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

}
