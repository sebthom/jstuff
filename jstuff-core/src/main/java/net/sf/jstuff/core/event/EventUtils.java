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

import java.util.Collection;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class EventUtils
{
	private static final Logger LOG = Logger.get();

	public static <T> int notify(final T event, final Collection<EventListener<T>> listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<T> listener : listeners)
			if (notify(event, listener)) count++;
		return count;
	}

	public static <T> boolean notify(final T event, final EventListener<T> listener)
	{
		if (listener != null) try
		{
			if (listener instanceof FilteringEventListener)
			{
				final FilteringEventListener<T> flistener = (FilteringEventListener<T>) listener;
				if (flistener.accept(event))
					flistener.onEvent(event);
				else
					return false;
			}
			else
				listener.onEvent(event);
			return true;
		}
		catch (final RuntimeException ex)
		{
			LOG.error("Failed to notify event listener %s", ex, listener);
		}
		return false;
	}

	public static <T> int notify(final T event, final EventListener<T>... listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<T> listener : listeners)
			if (notify(event, listener)) count++;
		return count;
	}

	private EventUtils()
	{
		super();
	}
}
