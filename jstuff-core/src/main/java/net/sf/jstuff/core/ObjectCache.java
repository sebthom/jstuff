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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ObjectCache<K, V>
{
	private static final class SoftValueReference<K, V> extends SoftReference<V>
	{
		protected final K key;

		protected SoftValueReference(final K key, final V value, final ReferenceQueue<V> queue)
		{
			super(value, queue);
			this.key = key;
		}
	}

	private static final class WeakValueReference<K, V> extends WeakReference<V>
	{
		protected final K key;

		protected WeakValueReference(final K key, final V value, final ReferenceQueue<V> queue)
		{
			super(value, queue);
			this.key = key;
		}
	}

	public static <K, V> ThreadLocal<ObjectCache<K, V>> newThreadLocalObjectCache()
	{
		return newThreadLocalObjectCache(-1);
	}

	public static <K, V> ThreadLocal<ObjectCache<K, V>> newThreadLocalObjectCache(final int maxObjectsToKeep)
	{
		return new ThreadLocal<ObjectCache<K, V>>()
			{
				@Override
				public ObjectCache<K, V> initialValue()
				{
					return new ObjectCache<K, V>(maxObjectsToKeep);
				}
			};
	}

	private final ReferenceQueue<V> refsWithGarbageCollectedValues = new ReferenceQueue<V>();
	private final int maxObjectsToKeep;

	private final Map<K, Reference<V>> objectsByKey = new HashMap<K, Reference<V>>();
	/**
	 * hard referencing the last n-th items to avoid their garbage collection.
	 * the first item is the latest accessed item.
	 */
	private final LinkedList<V> objectsLastAccessed;
	private final boolean useWeakReferences;

	/**
	 * Creates a new cache keeping all objects.
	 */
	public ObjectCache()
	{
		maxObjectsToKeep = -1;
		objectsLastAccessed = null;
		useWeakReferences = false;
	}

	public ObjectCache(final boolean useWeakValueReferences)
	{
		maxObjectsToKeep = -1;
		objectsLastAccessed = new LinkedList<V>();
		this.useWeakReferences = useWeakValueReferences;
	}

	/**
	 * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are subject to garbage collection
	 */
	public ObjectCache(final int maxObjectsToKeep)
	{
		this.maxObjectsToKeep = maxObjectsToKeep;
		objectsLastAccessed = new LinkedList<V>();
		useWeakReferences = false;
	}

	/**
	 * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are subject to garbage collection
	 */
	public ObjectCache(final int maxObjectsToKeep, final boolean useWeakValueReferences)
	{
		this.maxObjectsToKeep = maxObjectsToKeep;
		objectsLastAccessed = new LinkedList<V>();
		this.useWeakReferences = useWeakValueReferences;
	}

	@SuppressWarnings("unchecked")
	private void cleanup()
	{
		Reference< ? extends V> ref;
		while ((ref = refsWithGarbageCollectedValues.poll()) != null)
		{
			final K key;
			if (useWeakReferences)
				key = ((WeakValueReference<K, V>) ref).key;
			else
				key = ((SoftValueReference<K, V>) ref).key;
			final Reference< ? > currentRefObject = objectsByKey.get(key);
			if (currentRefObject != null && currentRefObject.get() == null) objectsByKey.remove(key);
		}
	}

	public boolean contains(final K key)
	{
		cleanup();
		final Reference<V> ref = objectsByKey.get(key);
		if (ref == null) return false;
		return ref.get() != null;
	}

	public V get(final K key)
	{
		cleanup();
		final Reference<V> ref = objectsByKey.get(key);
		if (ref != null)
		{
			final V value = ref.get();

			if (value == null)
				objectsByKey.remove(key);
			else //
			if (maxObjectsToKeep > 0) synchronized (objectsLastAccessed)
			{
				if (objectsLastAccessed.size() == 0 || value != objectsLastAccessed.getFirst())
				{
					objectsLastAccessed.remove(value);
					objectsLastAccessed.addFirst(value);
					if (objectsLastAccessed.size() > maxObjectsToKeep) objectsLastAccessed.removeLast();
				}
			}
			return ref.get();
		}
		return null;
	}

	public int getMaxObjectsToKeep()
	{
		return maxObjectsToKeep;
	}

	public void put(final K key, final V value)
	{
		objectsByKey.remove(key);
		objectsByKey.put(key, useWeakReferences ? new WeakValueReference<K, V>(key, value, refsWithGarbageCollectedValues)
				: new SoftValueReference<K, V>(key, value, refsWithGarbageCollectedValues));
	}

	public void remove(final K key)
	{
		objectsByKey.remove(key);
	}
}