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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface FilteringEventListener<Event> extends EventListener<Event>
{
	/**
	 * Determines if this event listener accepts the given event.
	 * The {@link #onEvent(Object)} method is only called when <code>true</code> is returned.
	 */
	boolean accept(Event type);
}
