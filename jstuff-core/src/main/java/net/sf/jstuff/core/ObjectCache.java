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
package net.sf.jstuff.core;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Thread-safe in-memory object cache.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ObjectCache<K, V>
{
	private static final class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V>
	{
		private final K key;

		private SoftValueReference(final K key, final V value, final ReferenceQueue<V> queue)
		{
			super(value, queue);
			this.key = key;
		}

		public K getKey()
		{
			return key;
		}
	}

	private static interface ValueReference<K, V>
	{
		K getKey();

		V get();
	}

	private static final class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V>
	{
		private final K key;

		private WeakValueReference(final K key, final V value, final ReferenceQueue<V> queue)
		{
			super(value, queue);
			this.key = key;
		}

		public K getKey()
		{
			return key;
		}
	}

	private final Map<K, ValueReference<K, V>> cache = new HashMap<K, ValueReference<K, V>>();
	private final ReferenceQueue<V> garbageCollectedRefs = new ReferenceQueue<V>();
	private final int maxObjectsToKeep;

	/**
	 * most recently used list.
	 * hard referencing the last n-th items to avoid their garbage collection.
	 * the first item is the latest accessed item.
	 */
	private final LinkedList<V> mru;
	private final boolean useWeakReferences;

	/**
	 * Creates a new cache keeping all objects.
	 */
	public ObjectCache()
	{
		maxObjectsToKeep = -1;
		mru = null;
		useWeakReferences = false;
	}

	public ObjectCache(final boolean useWeakValueReferences)
	{
		this(-1, useWeakValueReferences);
	}

	/**
	 * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are subject to garbage collection
	 */
	public ObjectCache(final int maxObjectsToKeep)
	{
		this(maxObjectsToKeep, false);
	}

	/**
	 * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are subject to garbage collection
	 */
	public ObjectCache(final int maxObjectsToKeep, final boolean useWeakValueReferences)
	{
		this.maxObjectsToKeep = maxObjectsToKeep;
		mru = new LinkedList<V>();
		this.useWeakReferences = useWeakValueReferences;
	}

	public boolean contains(final K key)
	{
		synchronized (cache)
		{
			expungeStaleEntries();
			final ValueReference<K, V> ref = cache.get(key);
			if (ref == null) return false;
			return ref.get() != null;
		}
	}

	@SuppressWarnings("unchecked")
	private void expungeStaleEntries()
	{
		ValueReference<K, V> ref;
		while ((ref = (ValueReference<K, V>) garbageCollectedRefs.poll()) != null)
		{
			final ValueReference<K, V> currentRefObject = cache.get(ref.getKey());
			if (currentRefObject != null && currentRefObject.get() == null) cache.remove(ref.getKey());
		}
	}

	public V get(final K key)
	{
		synchronized (cache)
		{
			expungeStaleEntries();
			final ValueReference<K, V> ref = cache.get(key);
			if (ref != null)
			{
				final V value = ref.get();

				if (value == null)
					cache.remove(key);
				else
				// udate mru list
				if (maxObjectsToKeep > 0) if (mru.size() == 0 || value != mru.getFirst())
				{
					mru.remove(value);
					mru.addFirst(value);
					if (mru.size() > maxObjectsToKeep) mru.removeLast();
				}
				return ref.get();
			}
			return null;
		}
	}

	public int getMaxObjectsToKeep()
	{
		return maxObjectsToKeep;
	}

	public void put(final K key, final V value)
	{
		final ValueReference<K, V> ref = useWeakReferences ? new WeakValueReference<K, V>(key, value, garbageCollectedRefs)
				: new SoftValueReference<K, V>(key, value, garbageCollectedRefs);

		synchronized (cache)
		{
			cache.put(key, ref);
		}
	}

	public void remove(final K key)
	{
		synchronized (cache)
		{
			cache.remove(key);
		}
	}
}