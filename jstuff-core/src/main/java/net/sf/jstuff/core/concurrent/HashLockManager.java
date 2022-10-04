/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * A lock manager that allows to issue thread-owned read-write locks on objects based on
 * object <b>equality</b> ( a.equals(b) ) and NOT on object identity ( a == b ).
 *
 * The implementation internally uses {@link ReentrantReadWriteLock} objects.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class HashLockManager<KeyType> {
   private static class CleanUpTask<T> implements Runnable {
      private static final Logger LOG = Logger.create();

      private final WeakReference<@Nullable HashLockManager<T>> ref;

      @Nullable
      private ScheduledFuture<?> future;

      CleanUpTask(final HashLockManager<T> mgr) {
         ref = new WeakReference<>(mgr);
      }

      @Override
      public void run() {
         final HashLockManager<T> mgr = ref.get();
         if (mgr == null) {
            // if the corresponding HashLockManager was garbage collected we can cancel the scheduled execution of cleanup task
            asNonNull(future).cancel(true);
            return;
         }

         try {
            for (final Iterator<Entry<T, ReentrantReadWriteLock>> it = mgr.locksByKey.entrySet().iterator(); it.hasNext();) {
               final ReentrantReadWriteLock lock = it.next().getValue();
               synchronized (lock) { // exclusive access to the lock object
                  final boolean isLockInUse = lock.isWriteLocked() || lock.getReadLockCount() > 0 || lock.hasQueuedThreads();
                  if (!isLockInUse) {
                     it.remove();
                  }
               }
            }
         } catch (final Exception ex) {
            LOG.error(ex, "Unexpected exception occured while cleaning lock objects.");
         }
      }
   }

   private static final class LazyInitialized {
      private static final ScheduledExecutorService DEFAULT_CLEANUP_THREAD = Executors.newSingleThreadScheduledExecutor(
         new BasicThreadFactory.Builder().daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("HashLockManager-thread").build());
   }

   private final ConcurrentMap<KeyType, ReentrantReadWriteLock> locksByKey = new ConcurrentHashMap<>();

   public HashLockManager(final int intervalMS) {
      this(intervalMS, LazyInitialized.DEFAULT_CLEANUP_THREAD);
   }

   public HashLockManager(final int intervalMS, final ScheduledExecutorService executor) {
      Args.notNull("executor", executor);
      final CleanUpTask<KeyType> cleanup = new CleanUpTask<>(this);
      cleanup.future = executor.scheduleWithFixedDelay(cleanup, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
   }

   /**
    * @param key the lock name/identifier
    */
   public <V> V executeReadLocked(final KeyType key, final Callable<V> callable) throws Exception {
      Args.notNull("key", key);
      Args.notNull("callable", callable);

      lockRead(key);
      try {
         return callable.call();
      } finally {
         unlockRead(key);
      }
   }

   /**
    * @param key the lock name/identifier
    */
   public <R, A, E extends Exception> R executeReadLocked(final KeyType key, final Invocable<R, A, E> invocable, final A arguments)
      throws E {
      Args.notNull("key", key);
      Args.notNull("invocable", invocable);

      lockRead(key);
      try {
         return invocable.invoke(arguments);
      } finally {
         unlockRead(key);
      }
   }

   /**
    * @param key the lock name/identifier
    */
   public void executeReadLocked(final KeyType key, final Runnable runnable) {
      Args.notNull("key", key);
      Args.notNull("runnable", runnable);

      lockRead(key);
      try {
         runnable.run();
      } finally {
         unlockRead(key);
      }
   }

   /**
    * @param key the lock name/identifier
    */
   public <V> V executeWriteLocked(final KeyType key, final Callable<V> callable) throws Exception {
      Args.notNull("key", key);
      Args.notNull("callable", callable);

      lockWrite(key);
      try {
         return callable.call();
      } finally {
         unlockWrite(key);
      }
   }

   /**
    * @param key the lock name/identifier
    */
   public <R, A, E extends Exception> R executeWriteLocked(final KeyType key, final Invocable<R, A, E> invocable, final A args) throws E {
      Args.notNull("key", key);
      Args.notNull("invocable", invocable);

      lockWrite(key);
      try {
         return invocable.invoke(args);
      } finally {
         unlockWrite(key);
      }
   }

   /**
    * @param key the lock name/identifier
    */
   public void executeWriteLocked(final KeyType key, final Runnable runnable) {
      Args.notNull("key", key);
      Args.notNull("runnable", runnable);

      lockWrite(key);
      try {
         runnable.run();
      } finally {
         unlockWrite(key);
      }
   }

   public int getLockCount() {
      return locksByKey.size();
   }

   /**
    * Acquires a non-exclusive read lock with a key equal to <code>key</code> for the current thread
    *
    * @param key the lock name/identifier
    */
   public void lockRead(final KeyType key) {
      Args.notNull("key", key);

      ReentrantReadWriteLock newLock = null;

      while (true) {
         ReentrantReadWriteLock lockCandidate = locksByKey.get(key);
         if (lockCandidate == null) {
            if (newLock == null) {
               newLock = new ReentrantReadWriteLock(true); // lazy instantiation of a new lock object
            }
            lockCandidate = newLock;
         }

         synchronized (lockCandidate) { // exclusive access to the lock object (required because of CleanUpTask)
            lockCandidate.readLock().lock();
            // check if the lock instance in the map for the given key is the one we locked
            if (lockCandidate == locksByKey.putIfAbsent(key, lockCandidate))
               return;
            lockCandidate.readLock().unlock();
         }
      }
   }

   /**
    * Acquires an exclusive read-write lock with a key equal to <code>key</code> for the current thread
    *
    * @param key the lock name/identifier
    */
   public void lockWrite(final KeyType key) {
      Args.notNull("key", key);

      ReentrantReadWriteLock newLock = null;

      while (true) {
         ReentrantReadWriteLock lockCandidate = locksByKey.get(key);
         if (lockCandidate == null) {
            if (newLock == null) {
               newLock = new ReentrantReadWriteLock(true); // lazy instantiation of a new lock object
            }
            lockCandidate = newLock;
         }

         synchronized (lockCandidate) { // exclusive access to the lock object (required because of CleanUpTask)
            lockCandidate.writeLock().lock();
            // check if the lock instance in the map for the given key is the one we locked
            if (lockCandidate == locksByKey.putIfAbsent(key, lockCandidate))
               return;
            lockCandidate.writeLock().unlock();
         }
      }
   }

   /**
    * Releases a non-exclusive read lock with a key equal <code>key</code> for the current thread
    *
    * @param key the lock name/identifier
    */
   public void unlockRead(final KeyType key) {
      Args.notNull("key", key);

      final var lock = locksByKey.get(key);
      if (lock == null)
         throw new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread. key: " + key);
      lock.readLock().unlock();
   }

   /**
    * Releases an exclusive read-write lock with a key equal to <code>key</code> for the current thread
    *
    * @param key the lock name/identifier
    */
   public void unlockWrite(final KeyType key) {
      Args.notNull("key", key);

      final var lock = locksByKey.get(key);
      if (lock == null)
         throw new IllegalMonitorStateException("attempt to unlock write lock, not locked by current thread. key: " + key);
      lock.writeLock().unlock();
   }
}
