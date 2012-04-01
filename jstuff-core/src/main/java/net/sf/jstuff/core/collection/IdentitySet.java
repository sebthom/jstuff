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

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class IdentitySet<E> implements Set<E>, Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	public static <E> IdentitySet<E> create()
	{
		return new IdentitySet<E>();
	}

	public static <E> IdentitySet<E> create(final int initialCapacity)
	{
		return new IdentitySet<E>(initialCapacity);
	}

	public static <E> IdentitySet<E> create(final int initialCapacity, final float growthFactor)
	{
		return new IdentitySet<E>(initialCapacity, growthFactor);
	}

	private transient Map<Integer, E> map;
	private float growthFactor = 0.75f;

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public IdentitySet()
	{
		map = new HashMap<Integer, E>(16, growthFactor);
	}

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * the given initial capacity and the default load factor (0.75).
	 */
	public IdentitySet(final int initialCapacity)
	{
		map = new HashMap<Integer, E>(initialCapacity, growthFactor);
	}

	public IdentitySet(final int initialCapacity, final float growthFactor)
	{
		map = new HashMap<Integer, E>(initialCapacity, growthFactor);
		this.growthFactor = growthFactor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean add(final E o)
	{
		final int hash = System.identityHashCode(o);
		return map.put(hash, o) == null;
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

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdentitySet<E> clone() throws CloneNotSupportedException
	{
		final IdentitySet<E> copy = new IdentitySet<E>(this.size(), growthFactor);
		copy.addAll(this);
		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.containsKey(hash);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
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
		return map.values().iterator();
	}

	/**
	 * Reads the <tt>IdentitySet</tt> instance from a stream.
	 */
	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException
	{
		// materialize any hidden serialization magic
		ois.defaultReadObject();

		// materialize the size
		final int size = ois.readInt();

		// materialize the elements
		map = newHashMap(size);
		for (int i = 0; i < size; i++)
		{
			final E o = (E) ois.readObject();
			final int hash = System.identityHashCode(o);
			map.put(hash, o);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.remove(hash) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(final Collection< ? > c)
	{
		boolean modified = false;
		for (final Object e : c)
			if (remove(e)) modified = true;
		return modified;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
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
		return map.values().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(final T[] a)
	{
		return map.values().toArray(a);
	}

	/**
	 * Writes state of this <tt>IdentitySet</tt> instance to a stream.
	 */
	private void writeObject(final ObjectOutputStream oos) throws java.io.IOException
	{
		// serialize any hidden serialization magic
		oos.defaultWriteObject();

		// serialize the set's size
		oos.writeInt(map.size());

		// serialize the set's elements
		for (final E e : map.values())
			oos.writeObject(e);
	}
}