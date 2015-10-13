/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeSet<V> extends CompositeCollection<V> implements Set<V>
{
	private static final long serialVersionUID = 1L;

	public static <V> CompositeSet<V> of(final Collection<Set< ? extends V>> components)
	{
		return new CompositeSet<V>(components);
	}

	public static <V> CompositeSet<V> of(final Set< ? extends V>... components)
	{
		return new CompositeSet<V>(components);
	}

	public CompositeSet()
	{
		super();
	}

	public CompositeSet(final Collection<Set< ? extends V>> components)
	{
		super(components);
	}

	public CompositeSet(final Set< ? extends V>... sets)
	{
		super(sets);
	}

	@Override
	public boolean equals(final Object obi)
	{
		if (obi == this) return true;
		if (!(obi instanceof Set)) return false;
		final Collection< ? > c = (Collection< ? >) obi;
		if (c.size() != size()) return false;
		try
		{
			return containsAll(c);
		}
		catch (final ClassCastException ignore)
		{
			return false;
		}
		catch (final NullPointerException ignore)
		{
			return false;
		}
	}

	private Set<V> getSnapshot()
	{
		final LinkedHashSet<V> values = new LinkedHashSet<V>();
		for (final Collection< ? extends V> coll : components)
			values.addAll(coll);
		return values;
	}

	@Override
	public int hashCode()
	{
		int h = 0;

		for (final V obj : this)
			if (obj != null) h += obj.hashCode();
		return h;
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
