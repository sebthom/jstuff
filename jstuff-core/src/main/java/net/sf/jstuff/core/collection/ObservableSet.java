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

import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableSet<E> implements Set<E>, EventListenable<ObservableSet.Action, E>
{
	public static enum Action
	{
		ADD,
		REMOVE
	}

	public static <E> ObservableSet<E> create(final Set<E> set)
	{
		return new ObservableSet<E>(set);
	}

	private final EventManager<Action, E> eventManager = new EventManager<Action, E>();

	private final Set<E> wrappedSet;

	public ObservableSet(final Set<E> set)
	{
		Args.notNull("set", set);
		this.wrappedSet = set;
	}

	public boolean add(final E item)
	{
		if (wrappedSet.add(item))
		{
			eventManager.fire(Action.ADD, item);
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
		for (final Iterator<E> it = wrappedSet.iterator(); it.hasNext();)
		{
			final E item = it.next();
			it.remove();
			eventManager.fire(Action.REMOVE, item);
		}
	}

	public boolean contains(final Object item)
	{
		return wrappedSet.contains(item);
	}

	public boolean containsAll(final Collection< ? > items)
	{
		return wrappedSet.containsAll(items);
	}

	public boolean isEmpty()
	{
		return wrappedSet.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return wrappedSet.iterator();
	}

	@SuppressWarnings("unchecked")
	public boolean remove(final Object item)
	{
		final boolean removed = wrappedSet.remove(item);
		if (removed) eventManager.fire(Action.REMOVE, (E) item);
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
		return wrappedSet.retainAll(itemsToKeep);
	}

	public int size()
	{
		return wrappedSet.size();
	}

	public boolean subscribe(final EventListener<Action, E> listener)
	{
		return eventManager.subscribe(listener);
	}

	public Object[] toArray()
	{
		return wrappedSet.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return wrappedSet.toArray(a);
	}

	public boolean unsubscribe(final EventListener<Action, E> listener)
	{
		return eventManager.unsubscribe(listener);
	}
}
