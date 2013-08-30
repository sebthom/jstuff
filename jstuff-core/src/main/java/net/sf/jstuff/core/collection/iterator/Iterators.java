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
package net.sf.jstuff.core.collection.iterator;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Iterators
{
	public static <T> ArrayIterator<T> array(final T... array)
	{
		return new ArrayIterator<T>(array);
	}

	public static <V> CompositeIterator<V> composite(final Collection< ? extends Iterator<V>> components)
	{
		return new CompositeIterator<V>(components);
	}

	public static <V> CompositeIterator<V> composite(final Iterator<V>... components)
	{
		return new CompositeIterator<V>(components);
	}

	public static boolean contains(final Iterator< ? > iterator, final Object searchFor)
	{
		Args.notNull("iterator", iterator);
		while (iterator.hasNext())
		{
			final Object elem = iterator.next();
			if (ObjectUtils.equals(elem, searchFor)) return true;
		}
		return false;
	}

	public static boolean containsIdentical(final Iterator< ? > iterator, final Object searchFor)
	{
		Args.notNull("iterator", iterator);
		while (iterator.hasNext())
			if (searchFor == iterator.next()) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> EmptyIterator<T> empty()
	{
		return (EmptyIterator<T>) EmptyIterator.INSTANCE;
	}

	public static <T> SingleObjectIterator<T> single(final T object)
	{
		return new SingleObjectIterator<T>(object);
	}

	public static int size(final Iterator< ? > iterator)
	{
		Args.notNull("iterator", iterator);
		int size = 0;
		while (iterator.hasNext())
		{
			size++;
			iterator.next();
		}
		return size;
	}

	public static <T> Iterable<T> toIterable(final Iterator<T> it)
	{
		return new Iterable<T>()
			{
				public Iterator<T> iterator()
				{
					return it;
				}
			};
	}

	public static <T> UnmodifiableIterator<T> unmodifiable(final Iterator<T> delegate)
	{
		return new UnmodifiableIterator<T>(delegate);
	}

	public static <T> UnmodifiableListIterator<T> unmodifiable(final ListIterator<T> delegate)
	{
		return new UnmodifiableListIterator<T>(delegate);
	}
}
