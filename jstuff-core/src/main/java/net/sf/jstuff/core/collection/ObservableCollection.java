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

import net.sf.jstuff.core.collection.ObservableCollection.ItemAction;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableCollection<E> implements Collection<E>, EventListenable<ItemAction<E>>
{
	public static enum BulkAction
	{
		ADD_ALL,
		CLEAR,
		REMOVE_ALL,
		RETAIN_ALL
	}

	public static enum ItemActionType
	{
		ADD,
		REMOVE
	}

	public static class ItemAction<E>
	{
		/**
		 * -1 means the index is not specified
		 */
		public final int index;
		public final E item;
		public final ItemActionType type;

		public ItemAction(final ItemActionType type, final E item, final int index)
		{
			this.type = type;
			this.item = item;
			this.index = index;
		}
	}

	public static <E> ObservableCollection<E> of(final Collection<E> set)
	{
		return new ObservableCollection<E>(set);
	}

	protected BulkAction currentBulkAction;

	private final EventManager<ItemAction<E>> events = new EventManager<ItemAction<E>>();

	private final Collection<E> wrapped;

	public ObservableCollection(final Collection<E> coll)
	{
		Args.notNull("coll", coll);
		this.wrapped = coll;
	}

	public boolean add(final E item)
	{
		if (wrapped.add(item))
		{
			onAdded(item, -1);
			return true;
		}
		return false;
	}

	public boolean addAll(final Collection< ? extends E> itemsToAdd)
	{
		if (itemsToAdd == null || itemsToAdd.size() == 0) return false;

		currentBulkAction = BulkAction.ADD_ALL;
		try
		{
			boolean anyAdded = false;
			for (final E item : itemsToAdd)
				if (add(item)) anyAdded = true;
			return anyAdded;
		}
		finally
		{
			currentBulkAction = null;
		}

	}

	public void clear()
	{
		currentBulkAction = BulkAction.CLEAR;
		try
		{
			for (final Iterator<E> it = iterator(); it.hasNext();)
				it.remove();
		}
		finally
		{
			currentBulkAction = null;
		}
	}

	public boolean contains(final Object item)
	{
		return wrapped.contains(item);
	}

	public boolean containsAll(final Collection< ? > items)
	{
		return wrapped.containsAll(items);
	}

	public BulkAction getCurrentBulkAction()
	{
		return currentBulkAction;
	}

	protected Collection<E> getWrapped()
	{
		return wrapped;
	}

	public boolean isEmpty()
	{
		return wrapped.isEmpty();
	}

	public boolean isObserving(final Collection<E> collection)
	{
		return wrapped == collection;
	}

	public Iterator<E> iterator()
	{
		final Iterator<E> it = wrapped.iterator();
		return new Iterator<E>()
			{
				int index = -1;
				E item;

				public boolean hasNext()
				{
					return it.hasNext();
				}

				public E next()
				{
					index++;
					item = it.next();
					return item;
				}

				public void remove()
				{
					it.remove();
					onRemoved(item, index);
				}
			};
	}

	/**
	 * @param index negative value if index unknown
	 */
	protected void onAdded(final E item, final int index)
	{
		events.fire(new ItemAction<E>(ItemActionType.ADD, item, index));
	}

	/**
	 * @param index negative value if index unknown
	 */
	protected void onRemoved(final E item, final int index)
	{
		events.fire(new ItemAction<E>(ItemActionType.REMOVE, item, index));
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object item)
	{
		final boolean removed = wrapped.remove(item);
		if (removed) onRemoved((E) item, -1);
		return removed;
	}

	public boolean removeAll(final Collection< ? > itemsToRemove)
	{
		if (itemsToRemove == null || itemsToRemove.size() == 0) return false;

		currentBulkAction = BulkAction.REMOVE_ALL;
		try
		{
			boolean removedAny = false;
			for (final Object item : itemsToRemove)
				if (remove(item)) removedAny = true;
			return removedAny;
		}
		finally
		{
			currentBulkAction = null;
		}
	}

	public boolean retainAll(final Collection< ? > itemsToKeep)
	{
		currentBulkAction = BulkAction.RETAIN_ALL;
		try
		{
			boolean removedAny = false;
			for (final Iterator<E> it = wrapped.iterator(); it.hasNext();)
			{
				final E item = it.next();
				if (itemsToKeep == null || !itemsToKeep.contains(item))
				{
					it.remove();
					removedAny = true;
				}
			}
			return removedAny;
		}
		finally
		{
			currentBulkAction = null;
		}
	}

	public int size()
	{
		return wrapped.size();
	}

	public Object[] toArray()
	{
		return wrapped.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return wrapped.toArray(a);
	}

	public <EventType extends ItemAction<E>> boolean subscribe(final EventListener<EventType> listener)
	{
		return events.subscribe(listener);
	}

	public <EventType extends ItemAction<E>> boolean unsubscribe(final EventListener<EventType> listener)
	{
		return events.unsubscribe(listener);
	}
}
