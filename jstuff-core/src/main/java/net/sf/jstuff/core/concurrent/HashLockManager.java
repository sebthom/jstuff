/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.concurrent;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.validation.Args;

/**
 * A lock manager that allows to issue thread-owned read-write locks on objects based on
 * object <b>equality</b> ( a.equals(b) ) and NOT on object identity ( a == b ).
 *
 * The implementation internally uses {@link ReentrantReadWriteLock} objects.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManager<T>
{
	private static class CleanUpTask<T> implements Runnable
	{
		private static final Logger LOG = Logger.create();

		private final WeakReference<HashLockManager<T>> ref;
		private ScheduledFuture< ? > future;

		public CleanUpTask(final HashLockManager<T> mgr)
		{
			ref = new WeakReference<HashLockManager<T>>(mgr);
		}

		public void run()
		{
			final HashLockManager<T> mgr = ref.get();
			if (mgr == null)
			{
				future.cancel(true);
				return;
			}

			try
			{
				for (final Iterator<Entry<T, ReentrantReadWriteLock>> it = mgr.locksByKey.entrySet().iterator(); it.hasNext();)
				{
					final ReentrantReadWriteLock lock = it.next().getValue();
					synchronized (lock)
					{
						final boolean isLockInUse = lock.isWriteLocked() || lock.getReadLockCount() > 0 || lock.hasQueuedThreads();
						if (!isLockInUse) it.remove();
					}
				}
			}
			catch (final Exception ex)
			{
				LOG.error("Unexpected exception occured while cleaning lock objects.", ex);
			}
		}
	}

	private static final ScheduledExecutorService CLEANUP_THREAD = Executors.newSingleThreadScheduledExecutor();

	private final ConcurrentHashMap<T, ReentrantReadWriteLock> locksByKey = new ConcurrentHashMap<T, ReentrantReadWriteLock>();

	public HashLockManager(final long lockCleanupInterval, final TimeUnit unit)
	{
		final CleanUpTask<T> cleanup = new CleanUpTask<T>(this);
		cleanup.future = CLEANUP_THREAD.scheduleAtFixedRate(cleanup, lockCleanupInterval, lockCleanupInterval, unit);
	}

	/**
	 * @param key the lock name/identifier
	 */
	public <V> V doReadLocked(final T key, final Callable<V> callable) throws Exception
	{
		Args.notNull("key", key);
		Args.notNull("callable", callable);

		lockRead(key);
		try
		{
			return callable.call();
		}
		finally
		{
			unlockRead(key);
		}
	}

	/**
	 * @param key the lock name/identifier
	 */
	public <R, A> R doReadLocked(final T key, final Invocable<R, A> invocable, final A arguments) throws Exception
	{
		Args.notNull("key", key);
		Args.notNull("invocable", invocable);

		lockRead(key);
		try
		{
			return invocable.invoke(arguments);
		}
		finally
		{
			unlockRead(key);
		}
	}

	/**
	 * @param key the lock name/identifier
	 */
	public void doReadLocked(final T key, final Runnable runnable)
	{
		Args.notNull("key", key);
		Args.notNull("runnable", runnable);

		lockRead(key);
		try
		{
			runnable.run();
		}
		finally
		{
			unlockRead(key);
		}
	}

	/**
	 * @param key the lock name/identifier
	 */
	public <V> V doWriteLocked(final T key, final Callable<V> callable) throws Exception
	{
		Args.notNull("key", key);
		Args.notNull("callable", callable);

		lockWrite(key);
		try
		{
			return callable.call();
		}
		finally
		{
			unlockWrite(key);
		}
	}

	/**
	 * @param key the lock name/identifier
	 */
	public <R, A> R doWriteLocked(final T key, final Invocable<R, A> invocable, final A arguments) throws Exception
	{
		Args.notNull("key", key);
		Args.notNull("invocable", invocable);

		lockWrite(key);
		try
		{
			return invocable.invoke(arguments);
		}
		finally
		{
			unlockWrite(key);
		}
	}

	/**
	 * @param key the lock name/identifier
	 */
	public void doWriteLocked(final T key, final Runnable runnable)
	{
		Args.notNull("key", key);
		Args.notNull("runnable", runnable);

		lockWrite(key);
		try
		{
			runnable.run();
		}
		finally
		{
			unlockWrite(key);
		}
	}

	public int getLockCount()
	{
		return locksByKey.size();
	}

	/**
	 * Acquires a non-exclusive read lock with a key equal to <code>key</code> for the current thread
	 * @param key the lock name/identifier
	 */
	public void lockRead(final T key)
	{
		Args.notNull("key", key);

		ReentrantReadWriteLock newLock = null;
		ReentrantReadWriteLock ourLock = null;

		while (true)
		{
			ReentrantReadWriteLock lockInMap = locksByKey.get(key);
			if (lockInMap == null)
			{
				if (newLock == null) newLock = new ReentrantReadWriteLock(true);
				ourLock = newLock;
			}
			else
				ourLock = lockInMap;

			synchronized (ourLock)
			{
				ourLock.readLock().lock();
				lockInMap = locksByKey.putIfAbsent(key, ourLock);
				if (lockInMap == ourLock) return;
			}
			ourLock.readLock().unlock();
		}
	}

	/**
	 * Acquires an exclusive read-write lock with a key equal to <code>key</code> for the current thread
	 * @param key the lock name/identifier
	 */
	public void lockWrite(final T key)
	{
		Args.notNull("key", key);

		ReentrantReadWriteLock newLock = null;
		ReentrantReadWriteLock ourLock = null;

		while (true)
		{
			ReentrantReadWriteLock lockInMap = locksByKey.get(key);
			if (lockInMap == null)
			{
				if (newLock == null) newLock = new ReentrantReadWriteLock(true);
				ourLock = newLock;
			}
			else
				ourLock = lockInMap;

			synchronized (ourLock)
			{
				ourLock.writeLock().lock();
				lockInMap = locksByKey.putIfAbsent(key, ourLock);
				if (lockInMap == ourLock) return;
			}
			ourLock.writeLock().unlock();
		}
	}

	/**
	 * Releases a non-exclusive read lock with a key equal <code>key</code> for the current thread
	 * @param key the lock name/identifier
	 */
	public void unlockRead(final T key)
	{
		Args.notNull("key", key);

		locksByKey.get(key).readLock().unlock();
	}

	/**
	 * Releases an exclusive read-write lock with a key equal to <code>key</code> for the current thread
	 * @param key the lock name/identifier
	 */
	public void unlockWrite(final T key)
	{
		Args.notNull("key", key);

		locksByKey.get(key).writeLock().unlock();
	}
}
