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
package net.sf.jstuff.core.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeMap<K, V> implements Map<K, V>
{
	private final ArrayList<Map< ? extends K, ? extends V>> maps = new ArrayList<Map< ? extends K, ? extends V>>();

	public CompositeMap()
	{
		super();
	}

	public CompositeMap(final Map< ? extends K, ? extends V>... maps)
	{
		CollectionUtils.addAll(this.maps, maps);
	}

	public CompositeMap<K, V> addComposite(final Map< ? extends K, ? extends V> map)
	{
		this.maps.add(map);
		return this;
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(final Object key)
	{
		for (final Map< ? extends K, ? extends V> m : maps)
			if (m.containsKey(key)) return true;
		return false;
	}

	public boolean containsValue(final Object value)
	{
		for (final Map< ? extends K, ? extends V> m : maps)
			if (m.containsValue(value)) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public CompositeSet<Entry<K, V>> entrySet()
	{
		final CompositeSet<Entry<K, V>> entries = new CompositeSet<Entry<K, V>>();
		for (final Map< ? extends K, ? extends V> m : maps)
			entries.addComposite((Collection< ? extends Entry<K, V>>) m.entrySet());
		return entries;
	}

	public V get(final Object key)
	{
		for (final Map< ? extends K, ? extends V> m : maps)
			if (m.containsKey(key)) return m.get(key);
		return null;
	}

	public boolean isEmpty()
	{
		for (final Map< ? extends K, ? extends V> m : maps)
			if (!m.isEmpty()) return false;
		return true;
	}

	public Set<K> keySet()
	{
		final CompositeSet<K> keys = new CompositeSet<K>();
		for (final Map< ? extends K, ? extends V> m : maps)
			keys.addComposite(m.keySet());
		return keys;
	}

	public V put(final Object key, final Object value)
	{
		throw new UnsupportedOperationException();
	}

	public void putAll(final Map< ? extends K, ? extends V> map)
	{
		throw new UnsupportedOperationException();
	}

	public V remove(final Object key)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return keySet().size();
	}

	public Collection<V> values()
	{
		return new AbstractList<V>()
			{
				@Override
				public V get(final int index)
				{
					int i = 0;
					for (final Iterator<K> it = keySet().iterator(); it.hasNext(); i++)
					{
						final K key = it.next();
						if (i == index) return CompositeMap.this.get(key);
					}
					throw new IndexOutOfBoundsException("Index: " + index);
				}

				@Override
				public int size()
				{
					return CompositeMap.this.size();
				}
			};
	}
}