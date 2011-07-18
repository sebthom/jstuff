/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManagerWithHistory<EventType> extends EventManager<EventType>
{
	private static final Logger LOG = Logger.get();

	private LinkedList<EventType> eventHistory;

	public EventManagerWithHistory()
	{
		LOG.info("Instantiated.");
		initEventHistory();
	}

	/**
	 * Sends all recorded events to the given listeners in case it was not added already.
	 */
	public boolean subscribeAndReplayHistory(final EventListener<EventType> listener)
	{
		Assert.argumentNotNull("listener", listener);

		if (super.subscribe(listener))
		{
			for (final Iterator<EventType> it = getEventHistory(); it.hasNext();)
				listener.onEvent(it.next());
			return true;
		}

		return false;
	}

	protected void addEventToHistory(final EventType event)
	{
		eventHistory.add(event);
	}

	public void clearHistory()
	{
		eventHistory.clear();
	}

	protected Iterator<EventType> getEventHistory()
	{
		return eventHistory.iterator();
	}

	@Override
	public int dispatch(final EventType event)
	{
		addEventToHistory(event);

		return super.dispatch(event);
	}

	@Override
	public Future<Integer> dispatchAsync(final EventType event)
	{
		addEventToHistory(event);

		return super.dispatchAsync(event);
	}

	protected void initEventHistory()
	{
		eventHistory = new LinkedList<EventType>();
	}
}
