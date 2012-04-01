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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Tuple implements Iterable<Object>, Serializable
{
	private static final long serialVersionUID = 1L;

	protected final List<Object> items;

	protected Tuple(final Object... items)
	{
		this.items = newArrayList(items);
	}

	public boolean contains(final Object o)
	{
		return items.contains(o);
	}

	public boolean containsAll(final Collection< ? > c)
	{
		return items.containsAll(c);
	}

	public Object get(final int index)
	{
		return items.get(index);
	}

	@SuppressWarnings("unchecked")
	public <T> T getTyped(final int index)
	{
		return (T) items.get(index);
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public Iterator<Object> iterator()
	{
		return items.iterator();
	}

	public int size()
	{
		return items.size();
	}

	public Object[] toArray()
	{
		return items.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return items.toArray(a);
	}
}
