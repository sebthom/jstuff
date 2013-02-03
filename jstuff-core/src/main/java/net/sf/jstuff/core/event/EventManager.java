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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManager<EventType, EventData> implements EventListenable<EventType, EventData>
{
	private static final Logger LOG = Logger.make();

	private final Set<EventListener<EventType, EventData>> eventListeners = new CopyOnWriteArraySet<EventListener<EventType, EventData>>();
	private final Set<EventListener<EventType, EventData>> eventListenersUnmodifiable = Collections.unmodifiableSet(eventListeners);

	private ExecutorService executorService;

	public EventManager()
	{
		LOG.info("Instantiated.");
	}

	protected ExecutorService createExecutorService()
	{
		return Executors.newFixedThreadPool(5);
	}

	public int fire(final EventType type, final EventData data)
	{
		return EventUtils.fire(type, data, eventListeners);
	}

	public Future<Integer> fireAsync(final EventType type, final EventData data)
	{
		@SuppressWarnings("unchecked")
		final EventListener<EventType, EventData>[] copy = eventListeners.toArray(new EventListener[eventListeners.size()]);

		return getExecutorService().submit(new Callable<Integer>()
			{
				public Integer call() throws Exception
				{
					return EventUtils.fire(type, data, copy);
				}
			});
	}

	/**
	 *
	 * @return an unmodifiable set of the registered event listeners
	 */
	public Set<EventListener<EventType, EventData>> getEventListeners()
	{
		return eventListenersUnmodifiable;
	}

	protected final synchronized ExecutorService getExecutorService()
	{
		if (executorService == null) executorService = createExecutorService();
		return executorService;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean subscribe(final EventListener<EventType, EventData> listener)
	{
		Args.notNull("listener", listener);
		return eventListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean unsubscribe(final EventListener<EventType, EventData> listener)
	{
		Args.notNull("listener", listener);
		return eventListeners.remove(listener);
	}

	public void unsubscribeAll()
	{
		eventListeners.clear();
	}
}
