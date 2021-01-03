/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Solution for http://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class ScalingThreadPoolExecutor extends ThreadPoolExecutor {
   private static final class ScalingQueue extends LinkedBlockingQueue<Runnable> {
      private static final long serialVersionUID = 1L;

      private ThreadPoolExecutor executor;

      @Override
      public boolean offer(final Runnable r) {
         final int requiredWorkers = executor.getActiveCount() + size();
         return requiredWorkers < executor.getPoolSize() && super.offer(r);
      }
   }

   private static final RejectedExecutionHandler FORCE_QUEUE_POLICY = (runnable, executor) -> {
      if (executor.isShutdown())
         throw new RejectedExecutionException(executor + " has been shutdown.");

      try {
         // block if queue is full instead of throwing an exception
         executor.getQueue().put(runnable);
      } catch (final InterruptedException ex) {
         Thread.currentThread().interrupt();
         throw new RejectedExecutionException(ex);
      }
   };

   private final AtomicInteger activeThreads = new AtomicInteger();

   /**
    * Creates a new <tt>ScalingThreadPoolExecutor</tt> with the given initial parameters and default thread factory and handler.
    *
    * @param minPoolSize the number of threads to keep in the pool, even if they are idle.
    * @param maxPoolSize the maximum number of threads to be allowed alive in the pool.
    * @param keepAliveTime when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks
    *           before terminating.
    * @param unit the time unit for the keepAliveTime argument.
    *
    * @throws IllegalArgumentException if minPoolSize, or keepAliveTime less than zero, or if maxPoolSize less than or equal to zero, or if minPoolSize greater
    *            than maxPoolSize.
    */
   public ScalingThreadPoolExecutor(final int minPoolSize, final int maxPoolSize, final long keepAliveTime, final TimeUnit unit) {
      super(minPoolSize, maxPoolSize, keepAliveTime, unit, new ScalingQueue(), FORCE_QUEUE_POLICY);
      ((ScalingQueue) getQueue()).executor = this;
   }

   @Override
   protected void afterExecute(final Runnable r, final Throwable t) {
      activeThreads.decrementAndGet();
   }

   @Override
   protected void beforeExecute(final Thread t, final Runnable r) {
      activeThreads.incrementAndGet();
   }

   @Override
   public int getActiveCount() {
      return activeThreads.get();
   }
}
