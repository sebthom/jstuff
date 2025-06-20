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
    * Waits until the given {@link Condition} returns true or the timeout elapses.
    * This method handles spurious wake-ups by recomputing the remaining wait time.
    *
    * @param condition a callback that performs a timed wait and returns true if the condition is met
    * @param timeoutMS maximum wait time in milliseconds
    * @return true if the condition was met within the timeout, false otherwise
    * @throws InterruptedException if the thread is interrupted while waiting
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

   /**
    * Waits indefinitely until the BooleanSupplier returns true.
    *
    * @param condition a predicate to check
    * @param waitObject object on which to wait/notify
    * @throws InterruptedException if interrupted while waiting
    */
   public static void await(final BooleanSupplier condition, final Object waitObject) throws InterruptedException {
      synchronized (waitObject) {
         while (!condition.getAsBoolean()) {
            waitObject.wait();
         }
      }
   }

   /**
    * Waits until the BooleanSupplier returns true or the timeout elapses.
    * This method handles spurious wake-ups and ensures the remaining wait time is correctly updated.
    *
    * @param condition a predicate to check
    * @param waitObject object on which to wait/notify
    * @param timeoutMS maximum wait time in milliseconds
    * @return true if the condition was met within the timeout, false otherwise
    * @throws InterruptedException if interrupted while waiting
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
