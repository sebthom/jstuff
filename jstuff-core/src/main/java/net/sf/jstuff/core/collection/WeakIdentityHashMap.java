/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashMap<K, V> implements Map<K, V>
{
	/**
	 * Wrapper used as key in the hash map that overrides equals and hashCode to ensure key comparisons by the underlying WeakHashMap is made based on the object's identity.
	 */
	private static final class KeyIdentity<T>
	{
		protected final T key;

		protected KeyIdentity(final T key)
		{
			this.key = key;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final KeyIdentity< ? > other = (KeyIdentity< ? >) obj;
			return key == other.key;
		}

		@Override
		public int hashCode()
		{
			return System.identityHashCode(key);
		}
	}

	private static final class WeakEntryDelegator<K, V> implements Entry<K, V>
	{
		protected final Entry<KeyIdentity<K>, V> weakEntry;

		protected WeakEntryDelegator(final Entry<KeyIdentity<K>, V> weakEntry)
		{
			this.weakEntry = weakEntry;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (!(obj instanceof Entry)) return false;

			@SuppressWarnings("rawtypes")
			final Entry e = (Entry) obj;

			final Object k1 = getKey();
			final Object k2 = e.getKey();
			if (k1 == k2 || k1 != null && k1.equals(k2))
			{
				final Object v1 = getValue();
				final Object v2 = e.getValue();
				if (v1 == v2 || v1 != null && v1.equals(v2)) return true;
			}
			return false;
		}

		public K getKey()
		{
			return weakEntry.getKey().key;
		}

		public V getValue()
		{
			return weakEntry.getValue();
		}

		@Override
		public int hashCode()
		{
			final Object k = weakEntry.getKey().key;
			final Object v = weakEntry.getValue();
			return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
		}

		public V setValue(final V value)
		{
			return weakEntry.setValue(value);
		}

		@Override
		public String toString()
		{
			return getKey() + "=" + getValue();
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

	private final WeakHashMap<KeyIdentity<K>, V> map;
	private transient Set<K> keySet = null;
	private transient Set<Entry<K, V>> entrySet = null;

	public WeakIdentityHashMap()
	{
		map = new WeakHashMap<KeyIdentity<K>, V>();
	}

	/**
	 * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
	 */
	public WeakIdentityHashMap(final int initialCapacity)
	{
		map = new WeakHashMap<KeyIdentity<K>, V>(initialCapacity);
	}

	/**
	 * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
	 * @param growthFactor The growth factor of the backing {@link WeakHashMap}
	 */
	public WeakIdentityHashMap(final int initialCapacity, final float growthFactor)
	{
		map = new WeakHashMap<KeyIdentity<K>, V>(initialCapacity, growthFactor);
	}

	public void clear()
	{
		map.clear();
	}

	public boolean containsKey(final Object key)
	{
		return map.containsKey(new KeyIdentity<Object>(key));
	}

	public boolean containsValue(final Object value)
	{
		return map.containsValue(value);
	}

	public Set<Entry<K, V>> entrySet()
	{
		final Set<Entry<K, V>> es = entrySet;
		return es != null ? es : (entrySet = new AbstractSet<Entry<K, V>>()
			{
				@Override
				public void clear()
				{
					WeakIdentityHashMap.this.clear();
				}

				@Override
				public boolean contains(final Object o)
				{
					if (!(o instanceof Entry)) return false;

					@SuppressWarnings("rawtypes")
					final Entry entry = (Entry) o;

					final KeyIdentity<Object> id = new KeyIdentity<Object>(entry.getKey());

					// check if the key exists at all
					if (!map.containsKey(id)) return false;

					// check if the entry's value and the current value in the map equal
					return ObjectUtils.equals(entry.getValue(), map.get(id));
				}

				@Override
				public Iterator<Entry<K, V>> iterator()
				{
					// create an iterator instance that delegates to an iterator of the underlying weakMap's entry set
					return new Iterator<Entry<K, V>>()
						{
							Iterator<Entry<KeyIdentity<K>, V>> it = map.entrySet().iterator();

							public boolean hasNext()
							{
								return it.hasNext();
							}

							public Entry<K, V> next()
							{
								return new WeakEntryDelegator<K, V>(it.next());
							}

							public void remove()
							{
								it.remove();
							}
						};
				}

				@Override
				public boolean remove(final Object o)
				{
					if (!(o instanceof Entry)) return false;

					@SuppressWarnings("rawtypes")
					final Entry entry = (Entry) o;

					/*
					 * we only remove the entry from the hash map if identical key is contained AND value are equal
					 */
					final KeyIdentity<Object> id = new KeyIdentity<Object>(entry.getKey());

					// check the key exists at all
					if (!map.containsKey(id)) return false;

					// check the entry's value and the current value in the map equal
					if (!ObjectUtils.equals(entry.getValue(), map.get(id))) return false;

					// remove the entry based on it's key
					return map.remove(id) != null;
				}

				@Override
				public int size()
				{
					return WeakIdentityHashMap.this.size();
				}

				@Override
				public Object[] toArray()
				{
					final Collection<Entry<K, V>> c = new ArrayList<Entry<K, V>>(size());
					for (final Entry<K, V> entry : this)
						c.add(entry);
					return c.toArray();
				}

				@Override
				public <T> T[] toArray(final T[] a)
				{
					final Collection<Entry<K, V>> c = new ArrayList<Entry<K, V>>(size());
					for (final Entry<K, V> entry : this)
						c.add(entry);
					return c.toArray(a);
				}
			});
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof Map)) return false;

		final Map< ? , ? > otherMap = (Map< ? , ? >) obj;

		if (otherMap.size() != size()) return false;

		for (final Entry<K, V> entry : entrySet())
		{
			final K key = entry.getKey();
			if (!otherMap.containsKey(key) || !ObjectUtils.equals(entry.getValue(), otherMap.get(key))) return false;
		}

		return true;
	}

	public V get(final Object key)
	{
		return map.get(new KeyIdentity<Object>(key));
	}

	@Override
	public int hashCode()
	{
		return map.hashCode();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Set<K> keySet()
	{
		final Set<K> ks = keySet;
		return ks != null ? ks : (keySet = new AbstractSet<K>()
			{
				private final Set<KeyIdentity<K>> weakKeySet = map.keySet();

				@Override
				public void clear()
				{
					WeakIdentityHashMap.this.clear();
				}

				@Override
				public boolean contains(final Object o)
				{
					return containsKey(o);
				}

				@Override
				public Iterator<K> iterator()
				{
					// create an iterator instance that delegates to an iterator of the underlying weakMap's key set
					return new Iterator<K>()
						{
							private final Iterator<KeyIdentity<K>> it = weakKeySet.iterator();

							public boolean hasNext()
							{
								return it.hasNext();
							}

							public K next()
							{
								return it.next().key;
							}

							public void remove()
							{
								it.remove();
							}
						};
				}

				@Override
				public boolean remove(final Object key)
				{
					final KeyIdentity<Object> id = new KeyIdentity<Object>(key);
					if (map.containsKey(id))
					{
						map.remove(id);
						return true;
					}
					return false;
				}

				@Override
				public int size()
				{
					return WeakIdentityHashMap.this.size();
				}

				@Override
				public Object[] toArray()
				{
					final Collection<K> c = new ArrayList<K>(size());
					for (final K k : this)
						c.add(k);
					return c.toArray();
				}

				@Override
				public <T> T[] toArray(final T[] a)
				{
					final Collection<K> c = new ArrayList<K>(size());
					for (final K k : this)
						c.add(k);
					return c.toArray(a);
				}
			});
	}

	public V put(final K key, final V value)
	{
		map.put(new KeyIdentity<K>(key), value);
		return value;
	}

	public void putAll(final Map< ? extends K, ? extends V> m)
	{
		for (final Entry< ? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	public V remove(final Object key)
	{
		return map.remove(new KeyIdentity<Object>(key));
	}

	public int size()
	{
		return map.size();
	}

	public Collection<V> values()
	{
		return map.values();
	}
}