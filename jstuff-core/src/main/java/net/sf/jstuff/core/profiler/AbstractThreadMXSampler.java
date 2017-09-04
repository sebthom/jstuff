/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractThreadMXSampler {
    private static final Logger LOG = Logger.create();

    private static final ThreadMXBean TMX = ManagementFactory.getThreadMXBean();

    private ScheduledExecutorService executor;
    private final int samplingInterval;
    private final ThreadMXBean threadMBean;
    private boolean isWarningLogged = false;

    private final Queue<ThreadInfo[]> samples = new ConcurrentLinkedQueue<ThreadInfo[]>();
    private final Callable<Void> aggregator = new Callable<Void>() {
        public Void call() throws Exception {
            while (true) {
                final ThreadInfo[] sample = samples.poll();
                if (sample == null) {
                    if (executor.isShutdown())
                        return null;
                    Thread.sleep(samplingInterval);
                } else {
                    onSample(sample);
                }
            }
        };
    };

    private final Runnable sampler = new Runnable() {
        public void run() {
            final long startAt = System.currentTimeMillis();
            samples.add(threadMBean.getThreadInfo(threadMBean.getAllThreadIds(), Integer.MAX_VALUE));
            final long elapsed = System.currentTimeMillis() - startAt;
            if (elapsed > samplingInterval && !isWarningLogged) {
                isWarningLogged = true;
                LOG.warn("Sampling interval of %s ms is too low. Sampling takes %s ms", samplingInterval, elapsed);
            }
        }
    };

    public AbstractThreadMXSampler(final int samplingIntervalInMS) {
        samplingInterval = samplingIntervalInMS;
        threadMBean = TMX;
    }

    public AbstractThreadMXSampler(final int samplingIntervalInMS, final ThreadMXBean mbean) {
        samplingInterval = samplingIntervalInMS;
        threadMBean = mbean;
    }

    public boolean isSampling() {
        return executor != null;
    }

    protected abstract void onSample(final ThreadInfo[] sample);

    public synchronized void start() {
        start(Executors.newScheduledThreadPool(2));
    }

    public synchronized void start(final ScheduledExecutorService executor) {
        Args.notNull("executor", executor);
        Assert.isTrue(this.executor == null, "Sampling in progress");

        LOG.info("Starting sampling...");
        this.executor = executor;
        this.executor.submit(aggregator);
        this.executor.scheduleAtFixedRate(sampler, samplingInterval, samplingInterval, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        Assert.isFalse(executor == null, "No sampling in progress");

        LOG.info("Stopping sampling ...");
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (final InterruptedException ex) {
            Threads.handleInterruptedException(ex);
        }
        executor = null;
    }
}
