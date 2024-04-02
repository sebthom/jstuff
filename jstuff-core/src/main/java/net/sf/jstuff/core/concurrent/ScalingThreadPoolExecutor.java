/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lazyNonNull;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Solution for http://stackoverflow.com/questions/19528304/how-to-get-the-threadpoolexecutor-to-increase-threads-to-max-before-queueing
 * <p>
 * See also:
 * <li>https://groovy-programming.com/post/26923146865
 * <li>https://dzone.com/articles/scalable-java-thread-pool-executor
 * <li>https://reachmnadeem.wordpress.com/2017/01/15/scalable-java-tpe/
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class ScalingThreadPoolExecutor extends ThreadPoolExecutor {

   private static final class ScalingQueue extends LinkedTransferQueue<Runnable> {
      private static final long serialVersionUID = 1L;

      ScalingThreadPoolExecutor executor = lazyNonNull();

      @Override
      public boolean offer(final Runnable runnable) {
         final boolean added = tryTransfer(runnable);
         if (added)
            return true;

         synchronized (this) {
            //Add to queue only, If pool size is reached max pool size allowed
            if (executor.getPoolSize() == executor.getMaximumPoolSize())
               return super.offer(runnable);
            return false;
         }
      }
   }

   /**
    * Creates a new <tt>ScalingThreadPoolExecutor</tt> with the given initial parameters and default {@link ThreadFactory} and a default
    * {@link RejectedExecutionHandler}.
    *
    * @param minPoolSize the number of threads to keep in the pool, even if they are idle
    * @param maxPoolSize the maximum number of threads to be allowed alive in the pool
    * @param keepAliveTime when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait
    *           for new tasks before terminating
    *
    * @throws IllegalArgumentException if minPoolSize, or keepAliveTime less than zero, or if maxPoolSize less than or equal to zero,
    *            or if minPoolSize greater than maxPoolSize.
    */
   public ScalingThreadPoolExecutor(final int minPoolSize, final int maxPoolSize, final Duration keepAliveTime) {
      this(minPoolSize, maxPoolSize, keepAliveTime, Executors.defaultThreadFactory());
   }

   /**
    * Creates a new <tt>ScalingThreadPoolExecutor</tt> with the given initial parameters and a default {@link RejectedExecutionHandler}.
    *
    * @param minPoolSize the number of threads to keep in the pool, even if they are idle
    * @param maxPoolSize the maximum number of threads to be allowed alive in the pool
    * @param keepAliveTime when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait
    *           for new tasks before terminating
    * @param threadFactory the factory to use when the executor creates a new thread
    *
    * @throws IllegalArgumentException if minPoolSize, or keepAliveTime less than zero, or if maxPoolSize less than or equal to zero,
    *            or if minPoolSize greater than maxPoolSize.
    */
   public ScalingThreadPoolExecutor(final int minPoolSize, final int maxPoolSize, final Duration keepAliveTime,
      final ThreadFactory threadFactory) {
      this(minPoolSize, maxPoolSize, keepAliveTime, threadFactory, new AbortPolicy());
   }

   private ScalingThreadPoolExecutor(final int minPoolSize, final int maxPoolSize, final Duration keepAliveTime,
      final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
      super(minPoolSize, maxPoolSize, keepAliveTime.toMillis(), TimeUnit.MILLISECONDS, new ScalingQueue(), threadFactory, handler);
      ((ScalingQueue) getQueue()).executor = this;
   }

   @Override
   public void setRejectedExecutionHandler(final @Nullable RejectedExecutionHandler handler) {
      throw new UnsupportedOperationException();
   }

}
