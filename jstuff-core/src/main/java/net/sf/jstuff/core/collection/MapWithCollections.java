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
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MapWithCollections<K, V, C extends Collection<V>> implements Map<K, C>, Serializable
{
	private static final long serialVersionUID = 1L;

	private final Map<K, C> map;
	private int initialCapacityOfCollection = 2;
	private float growthFactorOfCollection = 0.75f;

	public MapWithCollections()
	{
		map = new HashMap<K, C>();
	}

	public MapWithCollections(final int initialCapacity)
	{
		map = new HashMap<K, C>(initialCapacity);
	}

	public MapWithCollections(final int initialCapacity, final float growthFactor)
	{
		map = new HashMap<K, C>(initialCapacity, growthFactor);
	}

	public MapWithCollections(final int initialCapacity, final float growthFactor, final int initialCapacityOfCollection,
			final float growthFactorOfCollection)
	{
		map = new HashMap<K, C>(initialCapacity, growthFactor);
		this.initialCapacityOfCollection = initialCapacityOfCollection;
		this.growthFactorOfCollection = growthFactorOfCollection;
	}

	public MapWithCollections(final int initialCapacity, final int initialCapacityOfCollection)
	{
		map = new HashMap<K, C>(initialCapacity);
		this.initialCapacityOfCollection = initialCapacityOfCollection;
	}

	public void add(final K key, final V value)
	{
		C values = map.get(key);
		if (values == null)
		{
			values = createCollection(initialCapacityOfCollection, growthFactorOfCollection);
			map.put(key, values);
		}
		values.add(value);
	}

	public void addAll(final K key, final Collection<V> values)
	{
		if (values == null) return;

		C values2 = map.get(key);
		if (values2 == null)
		{
			values2 = createCollection(Math.max(initialCapacityOfCollection, values.size()), growthFactorOfCollection);
			map.put(key, values2);
		}
		values2.addAll(values);
	}

	public void addAll(final K key, final V... values)
	{
		if (values == null) return;

		C values2 = map.get(key);
		if (values2 == null)
		{
			values2 = createCollection(Math.max(initialCapacityOfCollection, values.length), growthFactorOfCollection);
			map.put(key, values2);
		}
		CollectionUtils.addAll(values2, values);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsKey(final Object key)
	{
		return map.containsKey(key);
	}

	/**
	 * Checks whether the map contains the value specified.
	 * <p>
	 * This checks the lists of all keys for the value, and thus could be slow.
	 *
	 * @param value the value to search for
	 * @return true if any of the lists referenced by the map contains the value
	 */
	public boolean containsValue(final Object value)
	{
		for (final Entry<K, C> entry : map.entrySet())
			if (entry.getValue() != null && entry.getValue().contains(value)) return true;
		return false;
	}

	public boolean containsValue(final Object key, final Object value)
	{
		final C values = map.get(key);
		return values != null && values.contains(value);
	}

	protected abstract C createCollection(final int initialCapacity, final float growthFactor);

	/**
	 * {@inheritDoc}
	 */
	public Set<Map.Entry<K, C>> entrySet()
	{
		return map.entrySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public C get(final Object key)
	{
		return map.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Iterator<V> iterator(final Object key)
	{
		final C values = map.get(key);
		return values == null ? (Iterator<V>) EmptyIterator.INSTANCE : values.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<K> keySet()
	{
		return map.keySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public C put(final K key, final C values)
	{
		return map.put(key, values);
	}

	/**
	 * Associates the specified value with the specified key in this map (optional operation).
	 * If the map previously contained a mapping for this key, the old value is replaced by the specified value.
	 * @param key key with which the specified value is to be associated
	 * @param values value to be associated with the specified key.
	 * @return previous value associated with specified key, or null if there was no mapping for key.
	 *         A null return can also indicate that the map previously associated null with the specified key,
	 *         if the implementation supports null values.
	 */
	public C put(final K key, final V... values)
	{
		final C coll = createCollection(Math.max(initialCapacityOfCollection, values.length), growthFactorOfCollection);
		CollectionUtils.addAll(coll, values);
		return put(key, coll);
	}

	/**
	 * {@inheritDoc}
	 */
	public void putAll(final Map< ? extends K, ? extends C> t)
	{
		map.putAll(t);
	}

	public boolean remove(final K key, final V value)
	{
		final C values = map.get(key);
		return values == null ? false : values.remove(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public C remove(final Object key)
	{
		return map.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return map.size();
	}

	public int size(final K key)
	{
		final C values = map.get(key);
		return values == null ? 0 : values.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<C> values()
	{
		return map.values();
	}
}
