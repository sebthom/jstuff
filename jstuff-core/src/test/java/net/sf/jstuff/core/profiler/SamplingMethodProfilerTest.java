/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class SamplingMethodProfilerTest {
   private static final Random RANDOM = new Random();

   static void slowMethod() {
      Threads.sleep(400);
      for (int i = 1; i < 1_000_000; i++) {
         RANDOM.nextLong();
         UUID.randomUUID();
      }
   }

   @Test
   void testSampler() throws IOException {
      final var profiler = new SamplingMethodProfiler(500);
      profiler.start(SamplingMethodProfilerTest.class.getName(), "slowMethod");
      try {
         slowMethod();
      } finally {
         final CallTree ct = profiler.stop();
         ct.toString(System.out, 100, 10);
      }
   }
}
