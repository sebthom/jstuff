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

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * Executor that blocks submission of new tasks if a given maximum number of tasks are already pending.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BlockingExecutor implements Executor {

   protected static final class RunnableWrapper implements Runnable {
      private final Runnable wrapped;
      private final Semaphore limiter;

      protected RunnableWrapper(final Runnable wrapped, final Semaphore limiter) {
         this.wrapped = wrapped;
         this.limiter = limiter;
      }

      @Override
      public void run() {
         try {
            wrapped.run();
         } finally {
            limiter.release();
         }
      }
   }

   protected final Semaphore limiter;
   protected final int maxWaitTime;
   protected final TimeUnit maxWaitTimeUnit;
   protected final Executor wrapped;

   protected final int maxPendingTasks;

   /**
    * @param maxWaitTime max time to wait for a tasks being added to the queue
    */
   public BlockingExecutor(final Executor executor, final int maxPendingTasks, final int maxWaitTime, final TimeUnit maxWaitTimeUnit) {
      Args.notNull("executor", executor);
      Args.min("maxPendingTasks", maxPendingTasks, 1);
      Args.notNegative("maxWaitTime", maxWaitTime);
      Args.notNull("maxWaitTimeUnit", maxWaitTimeUnit);

      wrapped = executor;
      this.maxPendingTasks = maxPendingTasks;
      this.maxWaitTime = maxWaitTime;
      this.maxWaitTimeUnit = maxWaitTimeUnit;
      limiter = new Semaphore(maxPendingTasks);
   }

   protected void aquirePermit() throws RejectedExecutionException {
      try {
         if (!limiter.tryAcquire(maxWaitTime, maxWaitTimeUnit))
            throw new RejectedExecutionException("Executor '" + wrapped + "' is busy!");
      } catch (final InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException(e);
      }
   }

   @Override
   public final void execute(final Runnable command) throws RejectedExecutionException {
      Args.notNull("command", command);

      aquirePermit();

      try {
         wrapped.execute(new RunnableWrapper(command, limiter));
      } catch (final RuntimeException ex) {
         limiter.release();
         throw ex;
      }
   }

   @Override
   public final String toString() {
      return Strings.toString(this, //
         "availablePermits", limiter.availablePermits(), //
         "maxPendingTasks", maxPendingTasks, //
         "maxWaitTime", maxWaitTime, //
         "maxWaitTimeUnit", maxWaitTimeUnit, //
         "wrapped", wrapped //
      );
   }
}
