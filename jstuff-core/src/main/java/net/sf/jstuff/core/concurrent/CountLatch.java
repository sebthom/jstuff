/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CountLatch {

   private final ResettableCountDownLatch countDown;
   private final int max;

   public CountLatch(final int max) {
      Args.min("max", max, 0);

      this.max = max;
      countDown = new ResettableCountDownLatch(max);
   }

   public void await() throws InterruptedException {
      countDown.await();
   }

   public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
      return countDown.await(timeout, unit);
   }

   /**
    * Increments the count of the latch, releasing all waiting threads if
    * the count reaches {@link #getMax()}.
    *
    * <p>
    * If the current count equals {@link #getMax()} nothing happens.
    */
   public void count() {
      countDown.countDown();
   }

   public long getCount() {
      return max - countDown.getCount();
   }

   public int getMax() {
      return max;
   }

   public void reset() {
      countDown.reset();
   }

   @Override
   public String toString() {
      return Strings.toString(this, //
         "count", getCount(), //
         "max", max //
      );
   }
}
