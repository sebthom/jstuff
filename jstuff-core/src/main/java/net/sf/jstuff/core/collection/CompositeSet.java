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
import java.util.Set;

/**
 * Unmodifiable composite set
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeSet<V> extends CompositeCollection<V> implements Set<V>
{
	public CompositeSet()
	{
		super();
	}

	public CompositeSet(final Set<V>... sets)
	{
		super(sets);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == null) return false;
		if (obj.getClass() != this.getClass()) return false;
		@SuppressWarnings("unchecked")
		final CompositeSet<V> other = (CompositeSet<V>) obj;
		return getSnapshot().equals(other.getSnapshot());
	}

	private Set<V> getSnapshot()
	{
		final LinkedSet<V> values = new LinkedSet<V>();
		for (final Collection< ? extends V> coll : collections)
			values.addAll(coll);
		return values;
	}

	@Override
	public int hashCode()
	{
		return getSnapshot().hashCode();
	}

	@Override
	public Iterator<V> iterator()
	{
		// to avoid duplicate elements from the different backing sets we dump the values of all into a new set
		final Iterator<V> it = getSnapshot().iterator();
		return new Iterator<V>()
			{
				public boolean hasNext()
				{
					return it.hasNext();
				}

				public V next()
				{
					return it.next();
				}

				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
	}

	@Override
	public int size()
	{
		return getSnapshot().size();
	}
}
