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

      long waitForMS = timeoutMS;
      final long started = System.currentTimeMillis();
      while (waitForMS > 0) {
         if (condition.await(waitForMS, TimeUnit.MILLISECONDS))
            return true;
         waitForMS = waitForMS - (System.currentTimeMillis() - started);
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
         long waitForMS = timeoutMS;
         final long started = System.currentTimeMillis();
         while (waitForMS > 0) {
            if (condition.getAsBoolean())
               return true;
            waitObject.wait(waitForMS);
            waitForMS = waitForMS - (System.currentTimeMillis() - started);
         }
         return condition.getAsBoolean();
      }
   }
}
