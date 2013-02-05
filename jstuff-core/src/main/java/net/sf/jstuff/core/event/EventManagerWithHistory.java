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

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.collection.Tuple2;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManagerWithHistory<EventType, EventData> extends EventManager<EventType, EventData>
{
	private static final Logger LOG = Logger.create();

	private LinkedList<Tuple2<EventType, EventData>> eventHistory;

	public EventManagerWithHistory()
	{
		LOG.info("Instantiated.");
		initEventHistory();
	}

	protected void addEventToHistory(final EventType type, final EventData data)
	{
		eventHistory.add(Tuple2.create(type, data));
	}

	public void clearHistory()
	{
		eventHistory.clear();
	}

	@Override
	public int fire(final EventType type, final EventData data)
	{
		addEventToHistory(type, data);

		return super.fire(type, data);
	}

	@Override
	public Future<Integer> fireAsync(final EventType type, final EventData data)
	{
		addEventToHistory(type, data);

		return super.fireAsync(type, data);
	}

	protected Iterator<Tuple2<EventType, EventData>> getEventHistory()
	{
		return eventHistory.iterator();
	}

	protected void initEventHistory()
	{
		eventHistory = new LinkedList<Tuple2<EventType, EventData>>();
	}

	/**
	 * Sends all recorded events to the given listeners in case it was not added already.
	 */
	public boolean subscribeAndReplayHistory(final EventListener<EventType, EventData> listener)
	{
		Args.notNull("listener", listener);

		if (super.subscribe(listener))
		{
			for (final Iterator<Tuple2<EventType, EventData>> it = getEventHistory(); it.hasNext();)
			{
				final Tuple2<EventType, EventData> event = it.next();
				listener.onEvent(event.get1(), event.get2());
			}
			return true;
		}

		return false;
	}
}
