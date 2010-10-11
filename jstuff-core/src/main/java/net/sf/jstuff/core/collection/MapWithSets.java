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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithSets<K, V> implements Map<K, Set<V>>
{
	public static <K, V> MapWithSets<K, V> create()
	{
		return new MapWithSets<K, V>();
	}

	public static <K, V> MapWithSets<K, V> create(final int initialCapacity, final int initialSetCapacity)
	{
		return new MapWithSets<K, V>(initialCapacity, initialSetCapacity);
	}

	private final Map<K, Set<V>> map;
	private int initialSetCapacity = 2;
	private float setGrowthFactor = 0.75f;

	public MapWithSets()
	{
		map = new HashMap<K, Set<V>>();
	}

	public MapWithSets(final int initialCapacity)
	{
		map = new HashMap<K, Set<V>>(initialCapacity);
	}

	public MapWithSets(final int initialCapacity, final float growthFactor)
	{
		map = new HashMap<K, Set<V>>(initialCapacity, growthFactor);
	}

	public MapWithSets(final int initialCapacity, final float growthFactor, final int initialSetCapacity,
			final float setGrowthFactor)
	{
		map = new HashMap<K, Set<V>>(initialCapacity, growthFactor);
		this.initialSetCapacity = initialSetCapacity;
		this.setGrowthFactor = setGrowthFactor;
	}

	public MapWithSets(final int initialCapacity, final int initialSetCapacity)
	{
		map = new HashMap<K, Set<V>>(initialCapacity);
		this.initialSetCapacity = initialSetCapacity;
	}

	public boolean add(final K key, final V value)
	{
		Set<V> values = map.get(key);
		if (values == null)
		{
			values = createSet(initialSetCapacity, setGrowthFactor);
			map.put(key, values);
		}
		return values.add(value);
	}

	public boolean addAll(final K key, final Collection<V> values)
	{
		Set<V> values2 = map.get(key);
		if (values2 == null)
		{
			values2 = createSet(initialSetCapacity, setGrowthFactor);
			map.put(key, values2);
		}
		return values2.addAll(values);
	}

	public void clear()
	{
		map.clear();
	}

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
		for (final Entry<K, Set<V>> entry : map.entrySet())
			if (entry.getValue() != null && entry.getValue().contains(value)) return true;
		return false;
	}

	public boolean containsValue(final Object key, final Object value)
	{
		final Set<V> values = map.get(key);
		return values != null && values.contains(value);
	}

	protected Set<V> createSet(final int initialCapacity, final float growthFactor)
	{
		return new HashSet<V>(initialCapacity, growthFactor);
	}

	public Set<Map.Entry<K, Set<V>>> entrySet()
	{
		return map.entrySet();
	}

	public Set<V> get(final Object key)
	{
		return map.get(key);
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Iterator<V> iterator(final Object key)
	{
		final Set<V> values = map.get(key);
		return values == null ? (Iterator<V>) CollectionUtils.newEmptyIterator() : values.iterator();
	}

	public Set<K> keySet()
	{
		return map.keySet();
	}

	public Set<V> put(final K key, final Set<V> values)
	{
		return map.put(key, values);
	}

	public void putAll(final Map< ? extends K, ? extends Set<V>> t)
	{
		map.putAll(t);
	}

	public boolean remove(final K key, final V value)
	{
		final Set<V> values = map.get(key);
		return values == null ? false : values.remove(value);
	}

	public Set<V> remove(final Object key)
	{
		return map.remove(key);
	}

	public int size()
	{
		return map.size();
	}

	public int size(final K key)
	{
		final Set<V> values = map.get(key);
		return values == null ? 0 : values.size();
	}

	public Collection<Set<V>> values()
	{
		return map.values();
	}
}
