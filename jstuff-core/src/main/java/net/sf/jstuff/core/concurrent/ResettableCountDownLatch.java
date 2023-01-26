/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ResettableCountDownLatch extends CountDownLatch {

   private static final class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 1L;

      private final int initialCount;

      Sync(final int initialCount) {
         this.initialCount = initialCount;
         reset();
      }

      int getCount() {
         return getState();
      }

      void reset() {
         setState(initialCount);
      }

      @Override
      protected int tryAcquireShared(final int unused) {
         return getCount() == 0 ? 1 : -1;
      }

      @Override
      protected boolean tryReleaseShared(final int unused) {
         while (true) {
            final int c = getCount();
            if (c == 0)
               return false;
            final int nextC = c - 1;
            if (compareAndSetState(c, nextC))
               return nextC == 0;
         }
      }
   }

   private final Sync sync;

   public ResettableCountDownLatch(final int count) {
      super(count);
      sync = new Sync(count);
   }

   @Override
   public void await() throws InterruptedException {
      sync.acquireSharedInterruptibly(1);
   }

   @Override
   public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
      return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
   }

   @Override
   public void countDown() {
      sync.releaseShared(1);
   }

   @Override
   public long getCount() {
      return sync.getCount();
   }

   public void reset() {
      sync.reset();
   }

   @Override
   public String toString() {
      return Strings.toString(this, //
         "count", sync.getCount() //
      );
   }
}
