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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.CollectionUtils.MapDiff.EntryValueDiff;
import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.functional.IsEqual;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class CollectionUtils
{
	public static class MapDiff<K, V> implements Serializable
	{
		public static class EntryValueDiff<K, V> implements Serializable
		{
			private static final long serialVersionUID = 1L;

			public final Map<K, V> leftMap;
			public final Map<K, V> rightMap;

			public final K key;
			public final V leftValue;
			public final V rightValue;

			public EntryValueDiff(final Map<K, V> leftMap, final Map<K, V> rightMap, final K key, final V leftValue, final V rightValue)
			{
				this.leftMap = leftMap;
				this.rightMap = rightMap;
				this.key = key;
				this.leftValue = leftValue;
				this.rightValue = rightValue;
			}

			@Override
			public String toString()
			{
				return EntryValueDiff.class.getSimpleName() + " [key=" + key + ", leftValue=" + leftValue + ", rightValue=" + rightValue
						+ "]";
			}
		}

		private static final long serialVersionUID = 1L;

		public final Map<K, V> leftMap;

		public final Map<K, V> rightMap;
		public final List<EntryValueDiff<K, V>> entryValueDiffs = newArrayList();
		public final Map<K, V> leftOnlyEntries = newHashMap();
		public final Map<K, V> rightOnlyEntries = newHashMap();

		public MapDiff(final Map<K, V> leftMap, final Map<K, V> rightMap)
		{
			this.leftMap = leftMap;
			this.rightMap = rightMap;
		}

		public boolean isDifferent()
		{
			return entryValueDiffs.size() > 0 || leftOnlyEntries.size() > 0 || rightOnlyEntries.size() > 0;
		}

		@Override
		public String toString()
		{
			return MapDiff.class.getSimpleName() + " [entryValueDiffs=" + entryValueDiffs + ", leftOnlyEntries=" + leftOnlyEntries
					+ ", rightOnlyEntries=" + rightOnlyEntries + "]";
		}
	}

	/**
	 * adds all items to the collection accepted by the filter
	 * @return number of items added
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int addAll(final Collection<T> collection, final Accept<T> filter, final T... items)
	{
		Args.notNull("collection", collection);
		Args.notNull("filter", filter);

		if (items == null) return 0;

		int count = 0;
		for (final T item : items)
			if (filter.accept(item) && collection.add(item)) count++;
		return count;
	}

	/**
	 * Adds all items to the collection
	 * @return number of items added
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int addAll(final Collection<T> collection, final T... items) throws IllegalArgumentException
	{
		Args.notNull("collection", collection);

		if (items == null) return 0;

		int count = 0;
		for (final T item : items)
			if (collection.add(item)) count++;
		return count;
	}

	public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap)
	{
		return diff(leftMap, rightMap, IsEqual.DEFAULT);
	}

	public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap, final IsEqual< ? super V> isEqual)
	{
		Args.notNull("leftMap", leftMap);
		Args.notNull("rightMap", rightMap);
		Args.notNull("isEqual", isEqual);

		final MapDiff<K, V> mapDiff = new MapDiff<K, V>(leftMap, rightMap);
		final Set<K> processedLeftKeys = newHashSet(Math.max(leftMap.size(), rightMap.size()));

		/*
		 * process the entries of the left map
		 */
		for (final Entry<K, V> leftEntry : leftMap.entrySet())
		{
			final K leftKey = leftEntry.getKey();
			final V leftValue = leftEntry.getValue();

			if (rightMap.containsKey(leftKey))
			{
				final V rightValue = rightMap.get(leftKey);
				if (!isEqual.isEqual(leftValue, rightValue))
					mapDiff.entryValueDiffs.add(new EntryValueDiff<K, V>(leftMap, rightMap, leftKey, leftValue, rightValue));
			}
			else
				mapDiff.leftOnlyEntries.put(leftKey, leftValue);
			processedLeftKeys.add(leftKey);
		}

		/*
		 * process remaining entries of the right map
		 */
		for (final Entry<K, V> rightEntry : rightMap.entrySet())
		{
			final K rightKey = rightEntry.getKey();

			if (processedLeftKeys.contains(rightKey)) continue;
			mapDiff.rightOnlyEntries.put(rightKey, rightEntry.getValue());
		}

		return mapDiff;
	}

	/**
	 * Returns a new list with all items accepted by the filter
	 * @throws IllegalArgumentException if <code>accept == null</code>
	 */
	public static <T> List<T> filter(final Collection<T> collection, final Accept<T> accept) throws IllegalArgumentException
	{
		if (collection == null) return null;

		Args.notNull("accept", accept);

		final List<T> result = newArrayList();
		for (final T item : collection)
			if (accept.accept(item)) result.add(item);
		return result;
	}

	/**
	 * removes all items not accepted by the filter
	 * @param collection
	 * @param accept
	 * @return number of items removed
	 * @throws IllegalArgumentException if <code>accept == null</code>
	 */
	public static <T> int filterInPlace(final Collection<T> collection, final Accept<T> accept) throws IllegalArgumentException
	{
		if (collection == null) return 0;

		Args.notNull("accept", accept);

		int count = 0;
		for (final T item : collection)
			if (!accept.accept(item) && collection.remove(item)) count++;
		return count;
	}

	public static <K, V> V get(final Map<K, V> map, final K key, final V defaultValue)
	{
		Args.notNull("map", map);

		if (map.containsKey(key)) return map.get(key);
		return defaultValue;
	}

	public static <K, V> ArrayList<K> keysAsArrayList(final Map<K, V> map)
	{
		Args.notNull("map", map);

		return newArrayList(map.keySet());
	}

	/**
	 * Returns string in the format of "name1=value1,name2=value2" (if valueSeparator="," and assignmentOperator="=")
	 *
	 * Opposite to {@link #toMap(String, String, String)}
	 */
	public static <K, V> CharSequence map2string(final Map<K, V> values, final String valueSeparator, final String assignmentOperator)
	{
		Args.notNull("values", values);
		Args.notNull("valueSeparator", valueSeparator);
		Args.notNull("assignmentOperator", assignmentOperator);

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
		// faster than Collections.addAll(result, array);
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

	public static <K, V> TreeMap<K, V> newTreeMap(final Comparator< ? super K> keyComparator, final Object... initialKeysAndValues)
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
		Args.notNull("map", map);

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

	public static <T> Iterable<T> toIterable(final Iterator<T> it)
	{
		Args.notNull("it", it);

		return new Iterable<T>()
			{
				public Iterator<T> iterator()
				{
					return it;
				}
			};
	}

	public static <T> List<T> toList(final Iterator<T> it)
	{
		Args.notNull("it", it);

		final List<T> result = newArrayList();
		while (it.hasNext())
			result.add(it.next());
		return result;
	}

	/**
	 * Converts key/value pairs defined in a string into a map.
	 *
	 * E.g. asMap("name1=value1,name2=value2", "\"", "=")
	 */
	public static Map<String, String> toMap(final String values, final String valueSeparator, final String assignmentOperator)
	{
		Args.notNull("values", values);
		Args.notNull("valueSeparator", valueSeparator);
		Args.notNull("assignmentOperator", assignmentOperator);

		final HashMap<String, String> map = newHashMap();
		for (final String element : StringUtils.split(values, valueSeparator))
		{
			final String[] valuePairSplitted = StringUtils.split(element, assignmentOperator);
			map.put(valuePairSplitted[0], valuePairSplitted[1]);
		}
		return map;
	}

	public static <S, T> List<T> transform(final List<S> source, final Function< ? super S, ? extends T> op)
	{
		if (source == null) return null;

		final List<T> target = newArrayList(source.size());
		for (final S sourceItem : source)
			target.add(op.apply(sourceItem));
		return target;
	}

	protected CollectionUtils()
	{
		super();
	}
}
