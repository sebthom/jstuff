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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingList<V> extends DelegatingCollection<V> implements List<V>
{
	private static final long serialVersionUID = 1L;

	private final List<V> delegate;

	public DelegatingList(final List<V> delegate)
	{
		super(delegate);
		this.delegate = delegate;
	}

	public void add(final int index, final V element)
	{
		delegate.add(index, element);
	}

	public boolean addAll(final int index, final Collection< ? extends V> c)
	{
		return delegate.addAll(index, c);
	}

	public V get(final int index)
	{
		return delegate.get(index);
	}

	public int indexOf(final Object o)
	{
		return delegate.indexOf(o);
	}

	public int lastIndexOf(final Object o)
	{
		return delegate.lastIndexOf(o);
	}

	public ListIterator<V> listIterator()
	{
		return delegate.listIterator();
	}

	public ListIterator<V> listIterator(final int index)
	{
		return delegate.listIterator(index);
	}

	public V remove(final int index)
	{
		return delegate.remove(index);
	}

	public V set(final int index, final V element)
	{
		return delegate.set(index, element);
	}

	public List<V> subList(final int fromIndex, final int toIndex)
	{
		return delegate.subList(fromIndex, toIndex);
	}

}
