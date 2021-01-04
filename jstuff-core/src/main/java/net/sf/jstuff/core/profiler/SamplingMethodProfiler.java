/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SamplingMethodProfiler {
   private static final Logger LOG = Logger.create();

   private final AbstractThreadMXSampler sampler;

   private String profiledClassName;
   private String profiledMethod;
   private CallTree root;

   public SamplingMethodProfiler(final int samplingIntervalInMS) {
      sampler = new AbstractThreadMXSampler(samplingIntervalInMS) {
         @Override
         protected void onSample(final ThreadInfo[] sample) {
            processSample(sample);
         }
      };
   }

   public SamplingMethodProfiler(final int samplingIntervalInMS, final ThreadMXBean mbean) {
      sampler = new AbstractThreadMXSampler(samplingIntervalInMS, mbean) {
         @Override
         protected void onSample(final ThreadInfo[] sample) {
            processSample(sample);
         }
      };
   }

   protected final void processSample(final ThreadInfo[] threadsState) {
      for (final ThreadInfo threadInfo : threadsState) {
         final boolean isThreadExecuting = threadInfo.getThreadState() == State.RUNNABLE && threadInfo.getLockName() == null;

         CallTree child = null;

         final StackTraceElement[] stack = threadInfo.getStackTrace();
         for (int i = stack.length - 1; i >= 0; i--) {
            final StackTraceElement ste = stack[i];
            if (child == null) {
               if (ste.getClassName().equals(profiledClassName) && ste.getMethodName().equals(profiledMethod)) {
                  child = root.markSeen(profiledClassName, profiledMethod, ste.getLineNumber(), isThreadExecuting);
               }
            } else {
               child = child.markSeen(ste.getClassName(), ste.getMethodName(), ste.getLineNumber(), isThreadExecuting);
            }
         }
      }
   }

   public synchronized void start(final String profiledClassName, final String profiledMethod) {
      Args.notNull("profiledClass", profiledClassName);
      Args.notNull("profiledMethod", profiledMethod);

      Assert.isFalse(sampler.isSampling(), "Sampling in progress");

      LOG.info("Starting sampling of %s#%s()", profiledClassName, profiledMethod);
      this.profiledClassName = profiledClassName;
      this.profiledMethod = profiledMethod;
      root = new CallTree();
      sampler.start();
   }

   public synchronized CallTree stop() {
      Assert.isTrue(sampler.isSampling(), "No sampling in progress");

      LOG.info("Stopping sampling of %s#%s()...", profiledClassName, profiledMethod);
      sampler.stop();
      final CallTree result = root;
      root = null;
      return result;
   }
}
