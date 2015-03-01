/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ref.FinalRef;
import net.sf.jstuff.core.ref.LazyInitializedRef;
import net.sf.jstuff.core.ref.Ref;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManager<Event> implements EventListenable<Event>
{
	private static final Logger LOG = Logger.create();

	private final Set<EventListener<Event>> eventListeners = new CopyOnWriteArraySet<EventListener<Event>>();

	private volatile Ref<ExecutorService> executorService;

	public EventManager()
	{
		this(5);
	}

	public EventManager(final ExecutorService executorService)
	{
		Args.notNull("executorService", executorService);
		this.executorService = FinalRef.of(executorService);
	}

	public EventManager(final int numberOfThreads)
	{
		executorService = new LazyInitializedRef<ExecutorService>()
			{
				@Override
				protected ExecutorService create()
				{
					LOG.info("Creating fixed thread pool with %s threads", numberOfThreads);
					return Executors.newFixedThreadPool(numberOfThreads);
				}
			};
	}

	public int fire(final Event type)
	{
		return Events.fire(type, eventListeners);
	}

	public Future<Integer> fireAsync(final Event type)
	{
		@SuppressWarnings("unchecked")
		final EventListener<Event>[] copy = eventListeners.toArray(new EventListener[eventListeners.size()]);

		return executorService.get().submit(new Callable<Integer>()
			{
				public Integer call() throws Exception
				{
					return Events.fire(type, copy);
				}
			});
	}

	@SuppressWarnings("unchecked")
	public <EventType extends Event> boolean subscribe(final EventListener<EventType> listener)
	{
		Args.notNull("listener", listener);
		return eventListeners.add((EventListener<Event>) listener);
	}

	public <EventType extends Event> boolean unsubscribe(final EventListener<EventType> listener)
	{
		Args.notNull("listener", listener);
		return eventListeners.remove(listener);
	}

	public void unsubscribeAll()
	{
		eventListeners.clear();
	}
}
