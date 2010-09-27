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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.WeakHashMap;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Filter;
import net.sf.jstuff.core.StringUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CollectionUtils
{
	/**
	 * adds all items to the collection accepted by the filter
	 * @return number of items added 
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int addAll(final Collection<T> collection, final Filter<T> filter, final T... items)
	{
		Assert.argumentNotNull("collection", collection);
		Assert.argumentNotNull("filter", filter);

		if (items == null) return 0;

		int count = 0;
		for (final T item : items)
			if (filter.accept(item)) if (collection.add(item)) count++;
		return count;
	}

	/**
	 * Adds all items to the collection
	 * @return number of items added 
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int addAll(final Collection<T> collection, final T... items) throws IllegalArgumentException
	{
		Assert.argumentNotNull("collection", collection);

		if (items == null) return 0;

		int count = 0;
		for (final T item : items)
			if (collection.add(item)) count++;
		return count;
	}

	/**
	 * Converts key/value pairs defined in a string into a map.
	 * 
	 * E.g. asMap("name1=value1,name2=value2", "\"", "=")
	 */
	public static Map<String, String> asMap(final String values, final String valueSeparator,
			final String assignmentOperator)
	{
		Assert.argumentNotNull("values", values);
		Assert.argumentNotNull("valueSeparator", valueSeparator);
		Assert.argumentNotNull("assignmentOperator", assignmentOperator);

		final HashMap<String, String> map = newHashMap();
		for (final String element : StringUtils.split(values, valueSeparator))
		{
			final String[] valuePairSplitted = StringUtils.split(element, assignmentOperator);
			map.put(valuePairSplitted[0], valuePairSplitted[1]);
		}
		return map;
	}

	/**
	 * removes all items not accepted by the filter
	 * @param coll
	 * @param filter
	 * @return number of items removed
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int filter(final Collection<T> collection, final Filter<T> filter)
			throws IllegalArgumentException
	{
		Assert.argumentNotNull("collection", collection);

		int count = 0;
		for (final T item : collection)
			if (!filter.accept(item)) if (collection.remove(item)) count++;
		return count;
	}

	public static <K, V> V get(final Map<K, V> map, final K key, final V defaultValue)
	{
		if (map.containsKey(key)) return map.get(key);
		return defaultValue;
	}

	public static <K, V> ArrayList<K> keysAsArrayList(final Map<K, V> map)
	{
		return newArrayList(map.keySet());
	}

	/**
	 * Returns string in the format of "name1=value1,name2=value2" (if valueSeparator="," and assignmentOperator="=")
	 * 
	 * Opposite to {@link #asMap(String, String, String)}
	 */
	public static <K, V> CharSequence map2string(final Map<K, V> values, final String valueSeparator,
			final String assignmentOperator)
	{
		Assert.argumentNotNull("values", values);
		Assert.argumentNotNull("valueSeparator", valueSeparator);
		Assert.argumentNotNull("assignmentOperator", assignmentOperator);

		final StringBuilder sb = new StringBuilder();

		for (final Iterator<Map.Entry<K, V>> it = values.entrySet().iterator(); it.hasNext();)
		{
			final Map.Entry<K, V> entry = it.next();
			sb.append(entry.getKey());
			sb.append(assignmentOperator);
			sb.append(entry.getValue());
			if (it.hasNext()) sb.append(valueSeparator);
		}
		return sb;
	}

	public static <K> ArrayList<K> newArrayList()
	{
		return new ArrayList<K>();
	}

	public static <K> ArrayList<K> newArrayList(final Collection<K> coll)
	{
		return new ArrayList<K>(coll);
	}

	public static <K> ArrayList<K> newArrayList(final int initialSize)
	{
		return new ArrayList<K>(initialSize);
	}

	public static <K> ArrayList<K> newArrayList(final K... values)
	{
		final ArrayList<K> l = new ArrayList<K>(values.length);
		for (final K v : values)
			l.add(v);
		return l;
	}

	public static <T> Iterator<T> newEmptyIterator()
	{
		return new Iterator<T>()
			{
				public boolean hasNext()
				{
					return false;
				}

				public T next()
				{
					throw new NoSuchElementException();
				}

				public void remove()
				{
					throw new NoSuchElementException();
				}
			};
	}

	public static <K, V> HashMap<K, V> newHashMap()
	{
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMap(final int initialSize)
	{
		return new HashMap<K, V>(initialSize);
	}

	public static <K, V> HashMap<K, V> newHashMap(final Object... initialKeysAndValues)
	{
		return putAll(new HashMap<K, V>(initialKeysAndValues.length / 2), initialKeysAndValues);
	}

	public static <K> HashSet<K> newHashSet()
	{
		return new HashSet<K>();
	}

	public static <K> HashSet<K> newHashSet(final Collection<K> coll)
	{
		return new HashSet<K>(coll);
	}

	public static <K> HashSet<K> newHashSet(final int initialSize)
	{
		return new HashSet<K>(initialSize);
	}

	public static <K> HashSet<K> newHashSet(final K... values)
	{
		final HashSet<K> s = new HashSet<K>(values.length);
		for (final K v : values)
			s.add(v);
		return s;
	}

	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Object... initialKeysAndValues)
	{
		return putAll(new LinkedHashMap<K, V>(initialKeysAndValues.length / 2), initialKeysAndValues);
	}

	public static <T> ThreadLocal<ArrayList<T>> newThreadLocalArrayList()
	{
		return new ThreadLocal<ArrayList<T>>()
			{
				@Override
				public ArrayList<T> initialValue()
				{
					return new ArrayList<T>();
				}
			};
	}

	public static <K, V> ThreadLocal<HashMap<K, V>> newThreadLocalHashMap()
	{
		return new ThreadLocal<HashMap<K, V>>()
			{
				@Override
				public HashMap<K, V> initialValue()
				{
					return new HashMap<K, V>();
				}
			};
	}

	public static <T> ThreadLocal<HashSet<T>> newThreadLocalHashSet()
	{
		return new ThreadLocal<HashSet<T>>()
			{
				@Override
				public HashSet<T> initialValue()
				{
					return new HashSet<T>();
				}
			};
	}

	public static <T> ThreadLocal<IdentitySet<T>> newThreadLocalIdentitySet()
	{
		return new ThreadLocal<IdentitySet<T>>()
			{
				@Override
				public IdentitySet<T> initialValue()
				{
					return new IdentitySet<T>();
				}
			};
	}

	public static <T> ThreadLocal<LinkedList<T>> newThreadLocalLinkedList()
	{
		return new ThreadLocal<LinkedList<T>>()
			{
				@Override
				public LinkedList<T> initialValue()
				{
					return new LinkedList<T>();
				}
			};
	}

	public static <K, V> ThreadLocal<WeakHashMap<K, V>> newThreadLocalWeakHashMap()
	{
		return new ThreadLocal<WeakHashMap<K, V>>()
			{
				@Override
				public WeakHashMap<K, V> initialValue()
				{
					return new WeakHashMap<K, V>();
				}
			};
	}

	public static <T> ThreadLocal<WeakHashSet<T>> newThreadLocalWeakHashSet()
	{
		return new ThreadLocal<WeakHashSet<T>>()
			{
				@Override
				public WeakHashSet<T> initialValue()
				{
					return new WeakHashSet<T>();
				}
			};
	}

	public static <K, V> TreeMap<K, V> newTreeMap()
	{
		return new TreeMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> newTreeMap(final Comparator< ? super K> keyComparator)
	{
		return new TreeMap<K, V>(keyComparator);
	}

	public static <K, V> TreeMap<K, V> newTreeMap(final Comparator< ? super K> keyComparator,
			final Object... initialKeysAndValues)
	{
		return putAll(new TreeMap<K, V>(keyComparator), initialKeysAndValues);
	}

	public static <K, V> TreeMap<K, V> newTreeMap(final Object... initialKeysAndValues)
	{
		return putAll(new TreeMap<K, V>(), initialKeysAndValues);
	}

	@SuppressWarnings("unchecked")
	public static <K, V, T extends Map<K, V>> T putAll(final T map, final Object... keysAndValues)
	{
		Assert.argumentNotNull("map", map);

		boolean nextIsValue = false;
		K key = null;
		for (final Object obj : keysAndValues)
			if (nextIsValue)
			{
				map.put(key, (V) obj);
				nextIsValue = false;
			}
			else
			{
				key = (K) obj;
				nextIsValue = true;
			}
		return map;
	}

	protected CollectionUtils()
	{
		super();
	}
}
