/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.profiler;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SamplingMethodProfilerTest extends TestCase {
   private static final Random RANDOM = new Random();

   public static void slowMethod() {
      Threads.sleep(400);
      for (int i = 1; i < 1_000_000; i++) {
         RANDOM.nextLong();
         UUID.randomUUID();
      }
   }

   public void testSampler() throws IOException {
      final SamplingMethodProfiler profiler = new SamplingMethodProfiler(500);
      profiler.start(SamplingMethodProfilerTest.class.getName(), "slowMethod");
      try {
         slowMethod();
      } finally {
         final CallTree ct = profiler.stop();
         ct.toString(System.out, 100, 10);
      }
   }
}
