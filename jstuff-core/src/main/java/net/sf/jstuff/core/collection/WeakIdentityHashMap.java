/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 *
 */
public class WeakIdentityHashMap<K, V> implements Map<K, V>
{
	private static final class ReferenceWrapper<T>
	{
		protected final T ref;

		protected ReferenceWrapper(final T ref)
		{
			this.ref = ref;
		}

		@Override
		public boolean equals(final Object x)
		{
			return ref == x;
		}

		@Override
		public int hashCode()
		{
			return ref.hashCode();
		}
	}

	public static <K, V> WeakIdentityHashMap<K, V> create()
	{
		return new WeakIdentityHashMap<K, V>();
	}

	public static <K, V> WeakIdentityHashMap<K, V> create(final int initialCapacity)
	{
		return new WeakIdentityHashMap<K, V>(initialCapacity);
	}

	public static <K, V> WeakIdentityHashMap<K, V> create(final int initialCapacity, final float growthFactor)
	{
		return new WeakIdentityHashMap<K, V>(initialCapacity, growthFactor);
	}

	private final WeakHashMap<ReferenceWrapper<K>, V> weakMap;

	public WeakIdentityHashMap()
	{
		weakMap = new WeakHashMap<ReferenceWrapper<K>, V>();
	}

	public WeakIdentityHashMap(final int initialCapacity)
	{
		weakMap = new WeakHashMap<ReferenceWrapper<K>, V>(initialCapacity);
	}

	public WeakIdentityHashMap(final int initialCapacity, final float growthFactor)
	{
		weakMap = new WeakHashMap<ReferenceWrapper<K>, V>(initialCapacity, growthFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		weakMap.clear();
	}

	public boolean containsKey(final Object key)
	{
		return weakMap.containsKey(wrapO(key));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsValue(final Object value)
	{
		return weakMap.containsValue(value);
	}

	public Set<Map.Entry<K, V>> entrySet()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public V get(final Object key)
	{
		return weakMap.get(wrapO(key));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		return weakMap.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<K> keySet()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public V put(final K key, final V value)
	{
		weakMap.put(wrapK(key), value);
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void putAll(final Map< ? extends K, ? extends V> m)
	{
		for (final Map.Entry< ? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public V remove(final Object key)
	{
		return weakMap.remove(wrapO(key));
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return weakMap.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<V> values()
	{
		return weakMap.values();
	}

	protected ReferenceWrapper<K> wrapK(final K key)
	{
		return new ReferenceWrapper<K>(key);
	}

	protected ReferenceWrapper<Object> wrapO(final Object key)
	{
		return new ReferenceWrapper<Object>(key);
	}
}