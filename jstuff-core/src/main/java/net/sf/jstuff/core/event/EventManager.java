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

import java.util.concurrent.Future;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManager<T> extends EventListenable<T>
{
	@Override
	public int notify(final T event)
	{
		return super.notify(event);
	}

	@Override
	public Future<Integer> notifyAsync(final T event)
	{
		return super.notifyAsync(event);
	}
}
