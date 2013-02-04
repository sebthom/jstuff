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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollection<V> implements Collection<V>
{
	protected ArrayList<Collection< ? extends V>> collections = new ArrayList<Collection< ? extends V>>();

	public CompositeCollection()
	{
		super();
	}

	public CompositeCollection(final Collection< ? extends V>... collections)
	{
		CollectionUtils.addAll(this.collections, collections);
	}

	public boolean add(final Object item)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(final Collection< ? extends V> values)
	{
		throw new UnsupportedOperationException();
	}

	public CompositeCollection<V> addComposite(final Collection< ? extends V> coll)
	{
		this.collections.add(coll);
		return this;
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean contains(final Object item)
	{
		for (final Collection< ? extends V> coll : collections)
			if (!coll.contains(item)) return true;
		return false;
	}

	public boolean containsAll(final Collection< ? > coll)
	{
		for (final Object item : coll)
			if (!this.contains(item)) return false;
		return true;
	}

	public boolean isEmpty()
	{
		for (final Collection< ? extends V> coll : collections)
			if (!coll.isEmpty()) return true;
		return false;
	}

	public Iterator<V> iterator()
	{
		final CompositeIterator<V> it = new CompositeIterator<V>();
		for (final Collection< ? extends V> coll : collections)
			it.addIterator(coll.iterator());
		return it;
	}

	public boolean remove(final Object item)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(final Collection< ? > values)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(final Collection< ? > values)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		int size = 0;
		for (final Collection< ? extends V> coll : collections)
			size += coll.size();
		return size;
	}

	public Object[] toArray()
	{
		final Object[] result = new Object[this.size()];
		int idx = 0;
		for (final Iterator<V> it = this.iterator(); it.hasNext(); idx++)
			result[idx] = it.next();
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(final T[] array)
	{
		final int size = this.size();
		final T[] result = array.length >= size ? array : (T[]) Array.newInstance(array.getClass().getComponentType(), size);
		int idx = 0;
		for (final Collection< ? extends V> coll : collections)
			for (final V v : coll)
				result[idx++] = (T) v;
		if (result.length > size) result[size] = null;
		return result;
	}
}
