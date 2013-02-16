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
package net.sf.jstuff.core.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Future;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManagerWithHistory<Event> extends EventManager<Event>
{
	private LinkedList<Event> eventHistory;

	public EventManagerWithHistory()
	{
		initEventHistory();
	}

	protected void addEventToHistory(final Event event)
	{
		eventHistory.add(event);
	}

	public void clearHistory()
	{
		eventHistory.clear();
	}

	@Override
	public int fire(final Event event)
	{
		addEventToHistory(event);

		return super.fire(event);
	}

	@Override
	public Future<Integer> fireAsync(final Event event)
	{
		addEventToHistory(event);

		return super.fireAsync(event);
	}

	protected Iterator<Event> getEventHistory()
	{
		return eventHistory.iterator();
	}

	protected void initEventHistory()
	{
		eventHistory = new LinkedList<Event>();
	}

	/**
	 * Sends all recorded events to the given listeners in case it was not added already.
	 */
	public boolean subscribeAndReplayHistory(final EventListener<Event> listener)
	{
		Args.notNull("listener", listener);

		if (super.subscribe(listener))
		{
			for (final Iterator<Event> it = getEventHistory(); it.hasNext();)
			{
				final Event event = it.next();
				listener.onEvent(event);
			}
			return true;
		}

		return false;
	}
}
