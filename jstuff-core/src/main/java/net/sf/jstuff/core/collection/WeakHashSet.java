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

import static java.lang.Boolean.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakHashSet<E> implements Set<E>, Cloneable
{
	public static <E> WeakHashSet<E> create()
	{
		return new WeakHashSet<E>();
	}

	public static <E> WeakHashSet<E> create(final int initialCapacity)
	{
		return new WeakHashSet<E>(initialCapacity);
	}

	public static <E> WeakHashSet<E> create(final int initialCapacity, final float growthFactor)
	{
		return new WeakHashSet<E>(initialCapacity, growthFactor);
	}

	private WeakHashMap<E, Boolean> map;

	public WeakHashSet()
	{
		this(16);
	}

	public WeakHashSet(final int initialCapacity)
	{
		this(16, 0.75f);
	}

	public WeakHashSet(final int initialCapacity, final float growthFactor)
	{
		map = new WeakHashMap<E, Boolean>(initialCapacity, growthFactor);
	}

	public boolean add(final E o)
	{
		return map.put(o, TRUE) == null;
	}

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

	@Override
	protected WeakHashSet<E> clone() throws CloneNotSupportedException
	{
		final WeakHashSet<E> copy = new WeakHashSet<E>(this.size());
		copy.addAll(this);
		return copy;
	}

	public boolean contains(final Object o)
	{
		return map.containsKey(o);
	}

	public boolean containsAll(final Collection< ? > c)
	{
		return map.keySet().containsAll(c);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (o == this) return true;

		if (!(o instanceof Set< ? >)) return false;

		final Set< ? > set = (Set< ? >) o;

		if (set.size() != size()) return false;

		return containsAll(set);
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		for (final E e : map.keySet())
			if (e != null) hash += e.hashCode();
		return hash;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return map.keySet().iterator();
	}

	public boolean remove(final Object o)
	{
		return map.remove(o) == TRUE;
	}

	public boolean removeAll(final Collection< ? > c)
	{
		return map.keySet().removeAll(c);
	}

	public boolean retainAll(final Collection< ? > c)
	{
		return map.keySet().retainAll(c);
	}

	public int size()
	{
		return map.size();
	}

	public Object[] toArray()
	{
		return map.keySet().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return map.keySet().toArray(a);
	}
}
