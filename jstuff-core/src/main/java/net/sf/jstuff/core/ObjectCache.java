/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ObjectCache<K, V>
{
	public static <K, V> ThreadLocal<ObjectCache<K, V>> newThreadLocalObjectCache()
	{
		return newThreadLocalObjectCache(-1);
	}

	public static <K, V> ThreadLocal<ObjectCache<K, V>> newThreadLocalObjectCache(final int maxElementsToKeep)
	{
		return new ThreadLocal<ObjectCache<K, V>>()
			{
				@Override
				public ObjectCache<K, V> initialValue()
				{
					return new ObjectCache<K, V>(maxElementsToKeep);
				}
			};
	}

	private final Map<K, SoftReference<V>> objectsByKey = new HashMap<K, SoftReference<V>>();
	private final LinkedList<V> objectsLastAccessed = new LinkedList<V>();
	private final int maxObjectsToKeep;

	/**
	 * Creates a new cache keeping all objects.
	 */
	public ObjectCache()
	{
		maxObjectsToKeep = -1;
	}

	/**
	 * @param maxObjectsToKeep the number of cached objects that should stay in memory when GC 
	 * starts removing SoftReferences to free memory 
	 */
	public ObjectCache(final int maxObjectsToKeep)
	{
		this.maxObjectsToKeep = maxObjectsToKeep;
	}

	public void compact()
	{
		for (final Map.Entry<K, SoftReference<V>> entry : objectsByKey.entrySet())
		{
			final SoftReference<V> ref = entry.getValue();
			if (ref.get() == null) objectsByKey.remove(entry.getKey());
		}
	}

	public boolean contains(final K key)
	{
		return objectsByKey.containsKey(key);
	}

	public V get(final K key)
	{
		final SoftReference<V> softReference = objectsByKey.get(key);
		if (softReference != null)
		{
			final V value = softReference.get();

			if (value == null)
				objectsByKey.remove(key);
			else if (maxObjectsToKeep > 0 && objectsLastAccessed.size() > 0 && value != objectsLastAccessed.getFirst())
			{
				objectsLastAccessed.remove(value);
				objectsLastAccessed.addFirst(value);
				if (objectsLastAccessed.size() > maxObjectsToKeep) objectsLastAccessed.removeLast();
			}
			return softReference.get();
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
		objectsByKey.put(key, new SoftReference<V>(value));
	}

	public void remove(final K key)
	{
		objectsByKey.remove(key);
	}
}