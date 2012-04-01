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

import static java.lang.Boolean.TRUE;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SoftHashSet<E> implements Set<E>
{
	public static <E> SoftHashSet<E> create()
	{
		return new SoftHashSet<E>();
	}

	public static <E> SoftHashSet<E> create(final int initialCapacity)
	{
		return new SoftHashSet<E>(initialCapacity);
	}

	public static <E> SoftHashSet<E> create(final int initialCapacity, final float growthFactor)
	{
		return new SoftHashSet<E>(initialCapacity, growthFactor);
	}

	private transient WeakHashMap<E, Object> map;

	/**
	 * Constructs a new, empty <tt>WeakHashSet</tt>; the backing <tt>WeakHashMap</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public SoftHashSet()
	{
		map = new WeakHashMap<E, Object>();
	}

	/**
	 * Constructs a new, empty <tt>WeakHashSet</tt>; the backing <tt>WeakHashMap</tt> instance has
	 * the given initial capacity and the default growth factor (0.75).
	 */
	public SoftHashSet(final int initialCapacity)
	{
		map = new WeakHashMap<E, Object>(initialCapacity);
	}

	/**
	 * Constructs a new, empty <tt>WeakHashSet</tt>; the backing <tt>WeakHashMap</tt> instance has
	 * the given initial capacity and the given growth factor.
	 */
	public SoftHashSet(final int initialCapacity, final float growthFactor)
	{
		map = new WeakHashMap<E, Object>(initialCapacity, growthFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean add(final E o)
	{
		return map.put(o, TRUE) == null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(final Collection< ? extends E> c)
	{
		int count = 0;
		for (final E e : c)
			if (add(e)) count++;
		return count > 0;
	}

	public void clear()
	{
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Object o)
	{
		return map.containsKey(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(final Collection< ? > c)
	{
		return map.keySet().containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o)
	{
		if (o == this) return true;

		if (!(o instanceof Set< ? >)) return false;

		final Set< ? > set = (Set< ? >) o;

		if (set.size() != size()) return false;

		return containsAll(set);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		int hash = 0;
		for (final E e : map.keySet())
			if (e != null) hash += e.hashCode();
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator()
	{
		return map.keySet().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(final Object o)
	{
		return map.remove(o) == TRUE;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(final Collection< ? > c)
	{
		return map.keySet().removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(final Collection< ? > c)
	{
		return map.keySet().retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return map.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray()
	{
		return map.keySet().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(final T[] a)
	{
		return map.keySet().toArray(a);
	}
}
