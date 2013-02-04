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
import java.util.Iterator;

import net.sf.jstuff.core.Composite;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeIterator<V> extends Composite.Default<Iterator< ? extends V>> implements Iterator<V>
{
	private static final long serialVersionUID = 1L;

	public static <V> CompositeIterator<V> of(final Collection< ? extends Iterator<V>> components)
	{
		return new CompositeIterator<V>(components);
	}

	public static <V> CompositeIterator<V> of(final Iterator<V>... components)
	{
		return new CompositeIterator<V>(components);
	}

	private Iterator< ? extends V> lastItemIterator = EmptyIterator.get();

	private Iterator< ? extends V> nextItemIterator = EmptyIterator.get();

	public CompositeIterator()
	{
		super();
	}

	public CompositeIterator(final Collection< ? extends Iterator<V>> components)
	{
		super(components);
		if (this.components.size() > 0) nextItemIterator = this.components.remove(0);
	}

	public CompositeIterator(final Iterator<V>... components)
	{
		super(components);
		if (this.components.size() > 0) nextItemIterator = this.components.remove(0);
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
		if (components.size() > 0)
		{
			nextItemIterator = components.remove(0);
			prepareNextItemIterator();
		}
	}

	public void remove()
	{
		lastItemIterator.remove();
	}
}
