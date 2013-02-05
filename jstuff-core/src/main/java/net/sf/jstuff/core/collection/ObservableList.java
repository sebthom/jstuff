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
import java.util.List;
import java.util.ListIterator;

import net.sf.jstuff.core.collection.ObservableCollection.Action;
import net.sf.jstuff.core.collection.ObservableList.ActionData;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableList<E> implements List<E>, EventListenable<Action, ActionData<E>>
{
	public static class ActionData<E>
	{
		public final E item;
		public final int index;

		public ActionData(final E item, final int index)
		{
			this.item = item;
			this.index = index;
		}
	}

	public static <E> ObservableList<E> create(final List<E> list)
	{
		return new ObservableList<E>(list);
	}

	private final EventManager<Action, ActionData<E>> events = new EventManager<Action, ActionData<E>>();
	private final List<E> wrappedList;

	public ObservableList(final List<E> list)
	{
		this.wrappedList = list;
	}

	public boolean add(final E item)
	{
		wrappedList.add(item);
		onAdded(item, wrappedList.size() - 1);
		return true;
	}

	public void add(final int index, final E item)
	{
		wrappedList.add(index, item);
		onAdded(item, index);
	}

	public boolean addAll(final Collection< ? extends E> itemsToAdd)
	{
		if (itemsToAdd == null || itemsToAdd.size() == 0) return false;
		for (final E item : itemsToAdd)
			add(item);
		return true;
	}

	public boolean addAll(int index, final Collection< ? extends E> itemsToAdd)
	{
		if (itemsToAdd == null || itemsToAdd.size() == 0) return false;
		for (final E item : itemsToAdd)
			add(index++, item);
		return true;
	}

	public void clear()
	{
		for (final Iterator<E> it = iterator(); it.hasNext();)
			it.remove();
	}

	public boolean contains(final Object item)
	{
		return wrappedList.contains(item);
	}

	public boolean containsAll(final Collection< ? > items)
	{
		return wrappedList.containsAll(items);
	}

	public E get(final int index)
	{
		return wrappedList.get(index);
	}

	public int indexOf(final Object item)
	{
		return wrappedList.indexOf(item);
	}

	public boolean isEmpty()
	{
		return wrappedList.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return wrappedList.iterator();
	}

	public int lastIndexOf(final Object item)
	{
		return wrappedList.lastIndexOf(item);
	}

	public ListIterator<E> listIterator()
	{
		return wrappedList.listIterator();
	}

	public ListIterator<E> listIterator(final int index)
	{
		return wrappedList.listIterator(index);
	}

	protected void onAdded(final E item, final int index)
	{
		events.fire(Action.ADD, new ActionData<E>(item, index));
	}

	protected void onRemoved(final E item, final int index)
	{
		events.fire(Action.REMOVE, new ActionData<E>(item, index));
	}

	public E remove(final int index)
	{
		final E item = wrappedList.remove(index);
		onRemoved(item, index);
		return item;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object item)
	{
		final boolean removed = wrappedList.remove(item);
		if (removed) onRemoved((E) item, wrappedList.size());
		return removed;
	}

	public boolean removeAll(final Collection< ? > itemsToRemove)
	{
		if (itemsToRemove == null || itemsToRemove.size() == 0) return false;
		boolean removedAny = false;
		for (final Object item : itemsToRemove)
			if (remove(item)) removedAny = true;
		return removedAny;
	}

	public boolean retainAll(final Collection< ? > itemsToKeep)
	{
		return wrappedList.retainAll(itemsToKeep);
	}

	public E set(final int index, final E item)
	{
		final E old = wrappedList.set(index, item);
		if (old != item)
		{
			if (old != null) onRemoved(old, index);
			onAdded(old, index);
		}
		return old;
	}

	public int size()
	{
		return wrappedList.size();
	}

	public List<E> subList(final int fromIndex, final int toIndex)
	{
		return wrappedList.subList(fromIndex, toIndex);
	}

	public boolean subscribe(final EventListener<Action, ActionData<E>> listener)
	{
		return events.subscribe(listener);
	}

	public Object[] toArray()
	{
		return wrappedList.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return wrappedList.toArray(a);
	}

	public boolean unsubscribe(final EventListener<Action, ActionData<E>> listener)
	{
		return events.unsubscribe(listener);
	}
}
