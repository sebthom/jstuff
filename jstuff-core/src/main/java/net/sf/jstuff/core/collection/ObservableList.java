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
public class ObservableList<E> extends ObservableCollection<E> implements List<E>
{
	public static <E> ObservableList<E> of(final List<E> list)
	{
		return new ObservableList<E>(list);
	}

	public ObservableList(final List<E> list)
	{
		super(list);
	}

	@Override
	public boolean add(final E item)
	{
		getWrapped().add(item);
		onAdded(item, size() - 1);
		return true;
	}

	public void add(final int index, final E item)
	{
		getWrapped().add(index, item);
		onAdded(item, index);
	}

	public boolean addAll(int index, final Collection< ? extends E> itemsToAdd)
	{
		if (itemsToAdd == null || itemsToAdd.size() == 0) return false;

		currentBulkAction = BulkAction.ADD_ALL;
		try
		{
			for (final E item : itemsToAdd)
				add(index++, item);
			return true;
		}
		finally
		{
			currentBulkAction = null;
		}
	}

	public E get(final int index)
	{
		return getWrapped().get(index);
	}

	@Override
	protected List<E> getWrapped()
	{
		return (List<E>) super.getWrapped();
	}

	public int indexOf(final Object item)
	{
		return getWrapped().indexOf(item);
	}

	public int lastIndexOf(final Object item)
	{
		return getWrapped().lastIndexOf(item);
	}

	public ListIterator<E> listIterator()
	{
		return listIterator(0);
	}

	public ListIterator<E> listIterator(final int index)
	{
		final ListIterator<E> it = getWrapped().listIterator(index);
		return new ListIterator<E>()
			{
				private E current;

				public boolean hasNext()
				{
					return it.hasNext();
				}

				public E next()
				{
					current = it.next();
					return current;
				}

				public boolean hasPrevious()
				{
					return it.hasPrevious();
				}

				public E previous()
				{
					current = it.previous();
					return current;
				}

				public int nextIndex()
				{
					return it.nextIndex();
				}

				public int previousIndex()
				{
					return it.previousIndex();
				}

				public void remove()
				{
					final int index = nextIndex() - 1;
					it.remove();
					onRemoved(current, index);
				}

				public void set(final E item)
				{
					if (item != current)
					{
						final int index = nextIndex() - 1;
						it.set(item);
						onRemoved(item, index);
						onAdded(current, index);
						current = item;
					}
				}

				public void add(final E item)
				{
					final int index = nextIndex();
					it.add(item);
					onAdded(current, index);
				}
			};
	}

	public E remove(final int index)
	{
		final E item = getWrapped().remove(index);
		onRemoved(item, index);
		return item;
	}

	@Override
	public boolean remove(final Object item)
	{
		final int index = indexOf(item);
		if (index == -1) return false;
		remove(index);
		return true;
	}

	public E set(final int index, final E item)
	{
		final E old = getWrapped().set(index, item);
		if (old != item)
		{
			if (old != null) onRemoved(old, index);
			onAdded(old, index);
		}
		return old;
	}

	public List<E> subList(final int fromIndex, final int toIndex)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
}
