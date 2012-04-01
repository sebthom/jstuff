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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ObjectUtils;

/**
 * WeakIdentityHashMap backed by a WeakHashMap using ob
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 *
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
			return key == obj;
		}

		@Override
		public int hashCode()
		{
			return key.hashCode();
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
			final Object k = getKey();
			final Object v = getValue();
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

	private final WeakHashMap<KeyIdentity<K>, V> weakMap;

	private transient Set<K> keySet = null;

	private transient Set<Entry<K, V>> entrySet = null;

	public WeakIdentityHashMap()
	{
		weakMap = new WeakHashMap<KeyIdentity<K>, V>();
	}

	/**
	 * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
	 */
	public WeakIdentityHashMap(final int initialCapacity)
	{
		weakMap = new WeakHashMap<KeyIdentity<K>, V>(initialCapacity);
	}

	/**
	 * @param initialCapacity The initial capacity of the backing {@link WeakHashMap}
	 * @param growthFactor The growth factor of the backing {@link WeakHashMap}
	 */
	public WeakIdentityHashMap(final int initialCapacity, final float growthFactor)
	{
		weakMap = new WeakHashMap<KeyIdentity<K>, V>(initialCapacity, growthFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		weakMap.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsKey(final Object key)
	{
		return weakMap.containsKey(new KeyIdentity<Object>(key));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsValue(final Object value)
	{
		return weakMap.containsValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
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
					if (!weakMap.containsKey(id)) return false;

					// check if the entry's value and the current value in the map equal
					return ObjectUtils.equals(entry.getValue(), weakMap.get(id));
				}

				@Override
				public Iterator<Entry<K, V>> iterator()
				{
					// create an iterator instance that delegates to an iterator of the underlying weakMap's entry set
					return new Iterator<Entry<K, V>>()
						{
							Iterator<Entry<KeyIdentity<K>, V>> it = weakMap.entrySet().iterator();

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
					if (!weakMap.containsKey(id)) return false;

					// check the entry's value and the current value in the map equal
					if (!ObjectUtils.equals(entry.getValue(), weakMap.get(id))) return false;

					// remove the entry based on it's key
					return weakMap.remove(id) != null;
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

	/**
	 * {@inheritDoc}
	 */
	public V get(final Object key)
	{
		return weakMap.get(new KeyIdentity<Object>(key));
	}

	@Override
	public int hashCode()
	{
		return weakMap.hashCode();
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
		final Set<K> ks = keySet;
		return ks != null ? ks : (keySet = new AbstractSet<K>()
			{
				private final Set<KeyIdentity<K>> weakKeySet = weakMap.keySet();

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
					if (weakMap.containsKey(id))
					{
						weakMap.remove(id);
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

	/**
	 * {@inheritDoc}
	 */
	public V put(final K key, final V value)
	{
		weakMap.put(new KeyIdentity<K>(key), value);
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void putAll(final Map< ? extends K, ? extends V> m)
	{
		for (final Entry< ? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public V remove(final Object key)
	{
		return weakMap.remove(new KeyIdentity<Object>(key));
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
}