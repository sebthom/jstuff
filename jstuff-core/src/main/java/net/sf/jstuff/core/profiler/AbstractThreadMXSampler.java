/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractThreadMXSampler {
   private static final Logger LOG = Logger.create();

   private @Nullable ScheduledExecutorService executor;
   private final int samplingInterval;
   private final ThreadMXBean threadMBean;
   private boolean isWarningLogged;

   private final Queue<ThreadInfo[]> samples = new ConcurrentLinkedQueue<>();
   private final Callable<@Nullable Void> aggregator = new Callable<>() {
      @Override
      public @Nullable Void call() throws Exception {
         while (true) {
            final ThreadInfo[] sample = samples.poll();
            if (sample == null) {
               final var executor = AbstractThreadMXSampler.this.executor;
               if (executor == null || executor.isShutdown())
                  return null;
               Thread.sleep(samplingInterval);
            } else {
               onSample(sample);
            }
         }
      }
   };

   private final Runnable sampler = new Runnable() {
      @Override
      public void run() {
         final long startAt = System.nanoTime();
         samples.add(threadMBean.getThreadInfo(threadMBean.getAllThreadIds(), Integer.MAX_VALUE));
         final long elapsedNanos = System.nanoTime() - startAt;
         final long elapsedMS = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
         if (elapsedMS > samplingInterval && !isWarningLogged) {
            isWarningLogged = true;
            LOG.warn("Sampling interval of %s ms is too low. Sampling takes %s ms", samplingInterval, elapsedMS);
         }
      }
   };

   protected AbstractThreadMXSampler(final int samplingIntervalInMS) {
      samplingInterval = samplingIntervalInMS;
      final var tmx = ManagementFactory.getThreadMXBean();
      if (tmx == null)
         throw new IllegalStateException("ManagementFactory.getThreadMXBean() returned null!");
      threadMBean = tmx;
   }

   protected AbstractThreadMXSampler(final int samplingIntervalInMS, final ThreadMXBean mbean) {
      samplingInterval = samplingIntervalInMS;
      threadMBean = mbean;
   }

   public boolean isSampling() {
      return executor != null;
   }

   protected abstract void onSample(ThreadInfo[] sample);

   public synchronized void start() {
      start(Executors.newScheduledThreadPool(2));
   }

   public synchronized void start(final ScheduledExecutorService executor) {
      Args.notNull("executor", executor);
      Assert.isTrue(this.executor == null, "Sampling in progress");

      LOG.info("Starting sampling...");
      this.executor = executor;
      executor.submit(aggregator);
      executor.scheduleAtFixedRate(sampler, samplingInterval, samplingInterval, TimeUnit.MILLISECONDS);
   }

   public synchronized void stop() {
      final var executor = this.executor;
      if (executor == null)
         throw new IllegalStateException("No sampling in progress");

      LOG.info("Stopping sampling ...");
      executor.shutdown();
      try {
         executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (final InterruptedException ex) {
         Threads.handleInterruptedException(ex);
      }
      this.executor = null;
   }
}
