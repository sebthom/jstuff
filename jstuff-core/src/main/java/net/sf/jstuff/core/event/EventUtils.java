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

import java.util.Collection;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class EventUtils
{
	private static final Logger LOG = Logger.get();

	/**
	 * @return the number of listeners notified successfully
	 */
	public static <T> int dispatch(final T event, final Collection<EventListener<T>> listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<T> listener : listeners)
			if (dispatch(event, listener)) count++;
		return count;
	}

	/**
	 * @return true if the listener was notified successfully
	 */
	public static <T> boolean dispatch(final T event, final EventListener<T> listener)
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

	/**
	 * @return the number of listeners notified successfully
	 */
	public static <T> int dispatch(final T event, final EventListener<T>... listeners)
	{
		int count = 0;
		if (listeners != null) for (final EventListener<T> listener : listeners)
			if (dispatch(event, listener)) count++;
		return count;
	}

	protected EventUtils()
	{
		super();
	}
}
