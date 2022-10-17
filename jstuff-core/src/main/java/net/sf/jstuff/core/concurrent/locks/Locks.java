/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Locks {

   public static void lockAll(final @Nullable Collection<? extends Lock> locks) {
      if (locks == null || locks.isEmpty())
         return;

      final var locked = new ArrayList<Lock>(locks.size());
      try {
         for (final Lock l : locks) {
            l.lock();
            locked.add(l);
         }
      } catch (final RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
   }

   public static void lockAll(final Lock @Nullable... locks) {
      if (locks == null || locks.length == 0)
         return;

      final var locked = new ArrayList<Lock>(locks.length);
      try {
         for (final Lock l : locks) {
            l.lock();
            locked.add(l);
         }
      } catch (final RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
   }

   public static void lockInterruptiblyAll(final @Nullable Collection<? extends Lock> locks) throws InterruptedException {
      if (locks == null || locks.isEmpty())
         return;

      final var locked = new ArrayList<Lock>(locks.size());
      try {
         for (final Lock l : locks) {
            l.lockInterruptibly();
            locked.add(l);
         }
      } catch (final InterruptedException | RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
   }

   public static void lockInterruptiblyAll(final Lock @Nullable... locks) throws InterruptedException {
      if (locks == null || locks.length == 0)
         return;

      final var locked = new ArrayList<Lock>(locks.length);
      try {
         for (final Lock l : locks) {
            l.lockInterruptibly();
            locked.add(l);
         }
      } catch (final InterruptedException | RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
   }

   public static CloseableLock toCloseable(final Lock lock) {
      Args.notNull("lock", lock);

      if (lock instanceof CloseableLock)
         return (CloseableLock) lock;

      return new CloseableLock() {
         @Override
         public boolean equals(final @Nullable Object obj) {
            return lock.equals(obj);
         }

         @Override
         public int hashCode() {
            return lock.hashCode();
         }

         @Override
         public void lock() {
            lock.lock();
         }

         @Override
         public void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
         }

         @Override
         public Condition newCondition() {
            return lock.newCondition();
         }

         @Override
         public String toString() {
            return lock.toString();
         }

         @Override
         public boolean tryLock() {
            return lock.tryLock();
         }

         @Override
         public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
            return lock.tryLock(time, unit);
         }

         @Override
         public void unlock() {
            lock.unlock();
         }
      };
   }

   public static CloseableReentrantLock toCloseable(final ReentrantLock lock) {
      Args.notNull("lock", lock);

      if (lock instanceof CloseableLock)
         return (CloseableReentrantLock) lock;

      return new CloseableReentrantLock() {
         private static final long serialVersionUID = 1L;

         @Override
         public boolean equals(final @Nullable Object obj) {
            return lock.equals(obj);
         }

         @Override
         public int getHoldCount() {
            return lock.getHoldCount();
         }

         @Override
         public int getWaitQueueLength(final Condition condition) {
            return lock.getWaitQueueLength(condition);
         }

         @Override
         public int hashCode() {
            return lock.hashCode();
         }

         @Override
         public boolean hasWaiters(final Condition condition) {
            return lock.hasWaiters(condition);
         }

         @Override
         public boolean isHeldByCurrentThread() {
            return lock.isHeldByCurrentThread();
         }

         @Override
         public boolean isLocked() {
            return lock.isLocked();
         }

         @Override
         public void lock() {
            lock.lock();
         }

         @Override
         public void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
         }

         @Override
         public Condition newCondition() {
            return lock.newCondition();
         }

         @Override
         public String toString() {
            return lock.toString();
         }

         @Override
         public boolean tryLock() {
            return lock.tryLock();
         }

         @Override
         public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
            return lock.tryLock(time, unit);
         }

         @Override
         public void unlock() {
            lock.unlock();
         }
      };
   }

   public static boolean tryLockAll(final @Nullable Collection<? extends Lock> locks) {
      if (locks == null || locks.isEmpty())
         return true;

      final var locked = new ArrayList<Lock>(locks.size());
      try {
         for (final Lock l : locks) {
            if (!l.tryLock()) {
               unlockAll(locked);
               return false;
            }
            locked.add(l);
         }
      } catch (final RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
      return true;
   }

   public static boolean tryLockAll(final Lock @Nullable... locks) {
      if (locks == null || locks.length == 0)
         return true;

      final var locked = new ArrayList<Lock>(locks.length);
      try {
         for (final Lock l : locks) {
            if (!l.tryLock()) {
               unlockAll(locked);
               return false;
            }
            locked.add(l);
         }
      } catch (final RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
      return true;
   }

   public static boolean tryLockAll(final long time, final TimeUnit unit, final @Nullable Collection<? extends Lock> locks)
      throws InterruptedException {
      if (locks == null || locks.isEmpty())
         return true;

      final var locked = new ArrayList<Lock>(locks.size());
      final long stopAt = System.nanoTime() + unit.toNanos(time);
      try {
         for (final Lock l : locks) {
            final long waitFor = stopAt - System.nanoTime();
            if (!l.tryLock(waitFor, TimeUnit.NANOSECONDS)) {
               unlockAll(locked);
               return false;
            }
            locked.add(l);
         }
      } catch (final InterruptedException | RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
      return true;
   }

   public static boolean tryLockAll(final long time, final TimeUnit unit, final Lock @Nullable... locks) throws InterruptedException {
      if (locks == null || locks.length == 0)
         return true;

      final var locked = new ArrayList<Lock>(locks.length);
      final long stopAt = System.nanoTime() + unit.toNanos(time);
      try {
         for (final Lock l : locks) {
            final long waitFor = stopAt - System.nanoTime();
            if (!l.tryLock(waitFor, TimeUnit.NANOSECONDS)) {
               unlockAll(l);
               return false;
            }
            locked.add(l);
         }
      } catch (final InterruptedException | RuntimeException ex) {
         unlockAll(locked);
         throw ex;
      }
      return true;
   }

   public static void unlockAll(final Iterable<? extends Lock> locks) {
      for (final Lock l : locks) {
         l.unlock();
      }
   }

   public static void unlockAll(final Lock... locks) {
      for (final Lock l : locks) {
         l.unlock();
      }
   }
}
