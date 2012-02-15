/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jstuff.core.validation.Args;

/**
 * A lock manager that allows to issue thread-owned read-write locks on objects based on
 * object <b>equality</b> ( a.equals(b) ) and NOT on object identity ( a == b ).
 * 
 * The implementation internally uses {@link ReentrantReadWriteLock} objects.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManager
{
	private final ConcurrentHashMap<Object, ReentrantReadWriteLock> locksByKey = new ConcurrentHashMap<Object, ReentrantReadWriteLock>();

	/**
	 * Get's the existing lock object or creates a new one if none exists.
	 */
	private ReentrantReadWriteLock getOrCreateLock(final Object key)
	{
		ReentrantReadWriteLock lock = locksByKey.get(key);
		if (lock == null)
		{
			final ReentrantReadWriteLock newLock = new ReentrantReadWriteLock(true);
			lock = locksByKey.putIfAbsent(key, newLock);
			if (lock == null) lock = newLock;
		}
		return lock;
	}

	/**
	 * @return true if any thread uses this lock
	 */
	private boolean isLockInUse(final ReentrantReadWriteLock lock)
	{
		return lock.isWriteLocked() || lock.getReadLockCount() > 0 || lock.hasQueuedThreads();
	}

	/**
	 * Acquires a non-exclusive read lock with a key equal to <code>key</code>
	 */
	public void lockRead(final Object key)
	{
		Args.notNull("key", key);

		final ReentrantReadWriteLock lock = getOrCreateLock(key);

		lock.readLock().lock();
		// the synchronized block required for proper removal of unused locks
		synchronized (lock)
		{
			locksByKey.put(key, lock);
		}
	}

	/**
	 * Acquires an exclusive read-write lock with a key equal to <code>key</code>
	 */
	public void lockWrite(final Object key)
	{
		Args.notNull("key", key);

		final ReentrantReadWriteLock lock = getOrCreateLock(key);

		lock.writeLock().lock();
		// the synchronized block required for proper removal of unused locks
		synchronized (lock)
		{
			locksByKey.put(key, lock);
		}
	}

	/**
	 * Releases a non-exclusive read lock with a key equal <code>key</code>
	 */
	public void unlockRead(final Object key)
	{
		Args.notNull("key", key);

		final ReentrantReadWriteLock lock = getOrCreateLock(key);

		lock.readLock().unlock();
		// the synchronized block required for proper removal of unused locks
		synchronized (lock)
		{
			if (!isLockInUse(lock)) locksByKey.remove(key);
		}
	}

	/**
	 * Releases an exclusive read-write lock with a key equal to <code>key</code>
	 */
	public void unlockWrite(final Object key)
	{
		Args.notNull("key", key);

		final ReentrantReadWriteLock lock = getOrCreateLock(key);

		lock.writeLock().unlock();
		// the synchronized block required for proper removal of unused locks
		synchronized (lock)
		{
			if (!isLockInUse(lock)) locksByKey.remove(key);
		}
	}
}
