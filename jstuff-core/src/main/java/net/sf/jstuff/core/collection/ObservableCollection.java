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

import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableCollection<E> implements Collection<E>, EventListenable<ObservableCollection.Action, E>
{
	public static enum Action
	{
		ADD,
		REMOVE
	}

	public static <E> ObservableCollection<E> create(final Collection<E> set)
	{
		return new ObservableCollection<E>(set);
	}

	private final EventManager<Action, E> events = new EventManager<Action, E>();

	private final Collection<E> wrappedCollection;

	public ObservableCollection(final Collection<E> coll)
	{
		Args.notNull("coll", coll);
		this.wrappedCollection = coll;
	}

	public boolean add(final E item)
	{
		if (wrappedCollection.add(item))
		{
			onAdded(item);
			return true;
		}
		return false;
	}

	public boolean addAll(final Collection< ? extends E> itemsToAdd)
	{
		if (itemsToAdd == null || itemsToAdd.size() == 0) return false;
		boolean anyAdded = false;
		for (final E item : itemsToAdd)
			if (add(item)) anyAdded = true;
		return anyAdded;
	}

	public void clear()
	{
		for (final Iterator<E> it = iterator(); it.hasNext();)
			it.remove();
	}

	public boolean contains(final Object item)
	{
		return wrappedCollection.contains(item);
	}

	public boolean containsAll(final Collection< ? > items)
	{
		return wrappedCollection.containsAll(items);
	}

	public boolean isEmpty()
	{
		return wrappedCollection.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return wrappedCollection.iterator();
	}

	protected void onAdded(final E item)
	{
		events.fire(Action.ADD, item);
	}

	protected void onRemoved(final E item)
	{
		events.fire(Action.REMOVE, item);
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object item)
	{
		final boolean removed = wrappedCollection.remove(item);
		if (removed) onRemoved((E) item);
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
		return wrappedCollection.retainAll(itemsToKeep);
	}

	public int size()
	{
		return wrappedCollection.size();
	}

	public boolean subscribe(final EventListener<Action, E> listener)
	{
		return events.subscribe(listener);
	}

	public Object[] toArray()
	{
		return wrappedCollection.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return wrappedCollection.toArray(a);
	}

	public boolean unsubscribe(final EventListener<Action, E> listener)
	{
		return events.unsubscribe(listener);
	}
}
