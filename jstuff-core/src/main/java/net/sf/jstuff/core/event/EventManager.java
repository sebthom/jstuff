/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManager<T> implements EventListenable<T>
{
	private static final Logger LOG = Logger.get();

	private final Set<EventListener<T>> eventListeners = new LinkedHashSet<EventListener<T>>();
	private final Set<EventListener<T>> eventListenersUnmodifiable = Collections.unmodifiableSet(eventListeners);

	private ExecutorService executorService;

	public EventManager()
	{
		LOG.info("Instantiated.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addEventListener(final EventListener<T> listener)
	{
		Assert.argumentNotNull("listener", listener);

		return eventListeners.add(listener);
	}

	protected ExecutorService createExecutorService()
	{
		return Executors.newFixedThreadPool(5);
	}

	/**
	 * 
	 * @return an unmodifiable set of the registered event listeners
	 */
	public Set<EventListener<T>> getEventListeners()
	{
		return eventListenersUnmodifiable;
	}

	protected final synchronized ExecutorService getExecutorService()
	{
		if (executorService == null) executorService = createExecutorService();
		return executorService;
	}

	public int notify(final T event)
	{
		return EventUtils.notify(event, eventListeners);
	}

	public Future<Integer> notifyAsync(final T event)
	{
		final Set<EventListener<T>> copy = new LinkedHashSet<EventListener<T>>(eventListeners);
		return getExecutorService().submit(new Callable<Integer>()
			{
				public Integer call() throws Exception
				{
					return EventUtils.notify(event, copy);
				}
			});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEventListener(final EventListener<T> listener)
	{
		Assert.argumentNotNull("listener", listener);

		return eventListeners.remove(listener);
	}
}
