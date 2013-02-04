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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeIterator<V> implements Iterator<V>, Serializable
{
	private static final long serialVersionUID = 1L;

	private final LinkedList<Iterator< ? extends V>> iterators = new LinkedList<Iterator< ? extends V>>();
	private Iterator< ? extends V> lastItemIterator = EmptyIterator.get();
	private Iterator< ? extends V> nextItemIterator = EmptyIterator.get();

	public CompositeIterator()
	{
		super();
	}

	public CompositeIterator(final Collection<Iterator< ? extends V>> iterators)
	{
		this.iterators.addAll(iterators);
		if (this.iterators.size() > 0) nextItemIterator = this.iterators.removeFirst();
	}

	public CompositeIterator(final Iterator< ? extends V>... iterators)
	{
		CollectionUtils.addAll(this.iterators, iterators);
		if (this.iterators.size() > 0) nextItemIterator = this.iterators.removeFirst();
	}

	public void addIterator(final Iterator< ? extends V> iterator)
	{
		iterators.add(iterator);
	}

	public boolean hasNext()
	{
		prepareNextItemIterator();
		return nextItemIterator.hasNext();
	}

	public V next()
	{
		prepareNextItemIterator();
		final V item = nextItemIterator.next();
		lastItemIterator = nextItemIterator;
		return item;
	}

	protected void prepareNextItemIterator()
	{
		if (nextItemIterator.hasNext()) return;
		if (iterators.size() > 0)
		{
			nextItemIterator = this.iterators.removeFirst();
			prepareNextItemIterator();
		}
	}

	public void remove()
	{
		lastItemIterator.remove();
	}
}
