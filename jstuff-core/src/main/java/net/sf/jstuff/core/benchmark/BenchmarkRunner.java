/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.benchmark;

import java.lang.management.ManagementFactory;
import java.util.Locale;

/**
 * When running a benchmark configure a large enough heap and use G1 gc to prevent garbage collection during benchmark run.
 * E.g. use something like <code>-Xms2048M -Xmx2048M -XX:+UseG1GC -Xlog:gc:stderr</code>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class BenchmarkRunner {

   private static final Runtime RUNTIME = Runtime.getRuntime();
   private static final String SEPARATOR = "--------------------------------";

   public static void run(final int warmUpRounds, final int benchmarkRounds, final int opsPerBenchmarkRound, final Runnable benchmark)
      throws InterruptedException {
      Locale.setDefault(Locale.ENGLISH);
      System.out.println("JVM Vendor: " + System.getProperty("java.vendor"));
      System.out.println("JVM Version: " + System.getProperty("java.version"));
      System.out.println(String.format("JVM Inital Heap: %.2f MB", RUNTIME.maxMemory() / (float) 1024 / 1024));
      System.out.println(String.format("JVM Maximum Heap: %.2f MB", RUNTIME.totalMemory() / (float) 1024 / 1024));
      System.out.println("JVM Args: " + String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments()));
      System.out.println(SEPARATOR);

      System.out.println("Warmup Rounds: " + warmUpRounds);
      System.out.println("Benchmark Rounds: " + warmUpRounds);
      System.out.println("Operations per Benchmark Round: " + opsPerBenchmarkRound);
      System.out.println(SEPARATOR);

      // warm-up
      for (int r = 1; r <= warmUpRounds; r++) {
         runRound("warm-up", r, warmUpRounds, 5 * opsPerBenchmarkRound, // use more iterations to force JIT to kick in
            false, // don't try to measure heap usage as warm-up rounds will trigger gc
            benchmark);
      }

      System.out.println(SEPARATOR);

      // benchmark
      for (int r = 1; r <= benchmarkRounds; r++) {
         runRound("benchmark", r, benchmarkRounds, opsPerBenchmarkRound, true, benchmark);
      }
      System.out.println("DONE.");
   }

   private static void runRound(final String label, final int round, final int totalRounds, final int iterations,
      final boolean measureHeapUsage, final Runnable benchmark) throws InterruptedException {
      // spinning up a thread so the JVM hopefully runs the benchmark on different cores each round
      final var t = new Thread(() -> {
         System.out.println(label + " " + round + "/" + totalRounds + "...");
         if (measureHeapUsage) {
            try {
               System.gc();
               Thread.sleep(1_000);
               System.gc();
               Thread.sleep(1_000);
            } catch (final InterruptedException ex) {
               Thread.interrupted();
               throw new RuntimeException(ex);
            }
         }

         final var startFreeMem = RUNTIME.freeMemory(); // CHECKSTYLE:IGNORE .*
         final var startAt = System.currentTimeMillis();

         for (int i = 0; i < iterations; i++) {
            benchmark.run();
         }

         final var durationMS = System.currentTimeMillis() - startAt;
         final var durationMSPerIteration = durationMS / (float) iterations;
         final var iterationsPerSecond = 60_000 / durationMSPerIteration;

         if (measureHeapUsage) {
            final var heapBytesPerIteration = (startFreeMem - RUNTIME.freeMemory()) / (float) iterations;
            System.out.println(String.format(" -> result: %,5d ms/round | %,7.2f ops/s | %,5.2f ms/op | %,6.3f MB/op", durationMS,
               iterationsPerSecond, durationMSPerIteration, heapBytesPerIteration / 1024 / 1024));
         } else {
            System.out.println(String.format(" -> result: %,5d ms/round | %,7.2f ops/s | %,5.2f ms/op", durationMS, iterationsPerSecond,
               durationMSPerIteration));
         }
      });
      t.setPriority(Thread.MAX_PRIORITY);
      t.run();
      t.join();
   }

   private BenchmarkRunner() {
   }
}
