/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.function.BooleanSupplier;

/**
 * Await methods safe against spurious wake-ups.
 * <p>
 * See https://errorprone.info/bugpattern/WaitNotInLoop and https://rules.sonarsource.com/java/tag/multi-threading/RSPEC-2274
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SafeAwait {

   /**
    * @param condition condition to await for
    * @param timeoutMS the maximum time to wait in milliseconds
    * @return value from {@link Condition#await(long, TimeUnit)}
    */
   public static boolean await(final Condition condition, final long timeoutMS) throws InterruptedException {

      long waitForNS = TimeUnit.MILLISECONDS.toNanos(timeoutMS);
      final long started = System.nanoTime();
      while (waitForNS > 0) {
         if (condition.await(waitForNS, TimeUnit.NANOSECONDS))
            return true;
         waitForNS = waitForNS - (System.nanoTime() - started);
      }
      return false;
   }

   public static void await(final BooleanSupplier condition, final Object waitObject) throws InterruptedException {
      synchronized (waitObject) {
         while (!condition.getAsBoolean()) {
            waitObject.wait();
         }
      }
   }

   /**
    * @return if condition was met
    */
   public static boolean await(final BooleanSupplier condition, final Object waitObject, final long timeoutMS) throws InterruptedException {
      synchronized (waitObject) {
         long waitForNS = TimeUnit.MILLISECONDS.toNanos(timeoutMS);
         final long started = System.nanoTime();
         while (waitForNS > 0) {
            if (condition.getAsBoolean())
               return true;
            waitObject.wait(waitForNS / 1_000_000);
            waitForNS = waitForNS - (System.nanoTime() - started);
         }
         return condition.getAsBoolean();
      }
   }
}
