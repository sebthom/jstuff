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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithLists<K, V> implements Map<K, List<V>>
{
	private final Map<K, List<V>> map = new HashMap<K, List<V>>();

	public void add(final K key, final V value)
	{
		List<V> values = map.get(key);
		if (values == null)
		{
			values = CollectionUtils.newArrayList();
			map.put(key, values);
		}
		values.add(value);
	}

	public void addAll(final K key, final Collection<V> values)
	{
		List<V> values2 = map.get(key);
		if (values2 == null)
		{
			values2 = CollectionUtils.newArrayList();
			map.put(key, values2);
		}
		values2.addAll(values);
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
		for (final Entry<K, List<V>> entry : map.entrySet())
			if (entry.getValue() != null && entry.getValue().contains(value)) return true;
		return false;
	}

	public boolean containsValue(final Object key, final Object value)
	{
		final List<V> values = map.get(key);
		return values != null && values.contains(value);
	}

	public Set<Map.Entry<K, List<V>>> entrySet()
	{
		return map.entrySet();
	}

	public List<V> get(final Object key)
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
		final List<V> values = map.get(key);
		return values == null ? (Iterator<V>) CollectionUtils.newEmptyIterator() : values.iterator();
	}

	public Set<K> keySet()
	{
		return map.keySet();
	}

	public List<V> put(final K key, final List<V> values)
	{
		return map.put(key, values);
	}

	public void putAll(final Map< ? extends K, ? extends List<V>> t)
	{
		map.putAll(t);
	}

	public boolean remove(final K key, final V value)
	{
		final List<V> values = map.get(key);
		return values == null ? false : values.remove(value);
	}

	public List<V> remove(final Object key)
	{
		return map.remove(key);
	}

	public int size()
	{
		return map.size();
	}

	public int size(final K key)
	{
		final List<V> values = map.get(key);
		return values == null ? 0 : values.size();
	}

	public Collection<List<V>> values()
	{
		return map.values();
	}
}
