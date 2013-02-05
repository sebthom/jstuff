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

import java.util.Collection;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Events
{
	private static final Logger LOG = Logger.create();

	/**
	 * @return the number of listeners notified successfully
	 */
	public static <EventType, EventData> int fire(final EventType type, final EventData data,
			final Collection<EventListener<EventType, EventData>> listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<EventType, EventData> listener : listeners)
			if (fire(type, data, listener)) count++;
		return count;
	}

	/**
	 * @return true if the listener was notified successfully
	 */
	public static <EventType, EventData> boolean fire(final EventType event, final EventData data,
			final EventListener<EventType, EventData> listener)
	{
		if (listener != null) try
		{
			if (listener instanceof FilteringEventListener)
			{
				final FilteringEventListener<EventType, EventData> flistener = (FilteringEventListener<EventType, EventData>) listener;
				if (flistener.accept(event, data))
					flistener.onEvent(event, data);
				else
					return false;
			}
			else
				listener.onEvent(event, data);
			return true;
		}
		catch (final RuntimeException ex)
		{
			LOG.error("Failed to notify event listener %s", ex, listener);
		}
		return false;
	}

	/**
	 * @return the number of listeners notified successfully
	 */
	public static <EventType, EventData> int fire(final EventType type, final EventData data,
			final EventListener<EventType, EventData>... listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<EventType, EventData> listener : listeners)
			if (fire(type, data, listener)) count++;
		return count;
	}

	protected Events()
	{
		super();
	}
}
