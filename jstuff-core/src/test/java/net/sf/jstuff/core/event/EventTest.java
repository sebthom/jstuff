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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventTest extends TestCase
{
	public void testEvents() throws InterruptedException, ExecutionException
	{
		final EventManager<String> em = new EventManager<String>();

		final AtomicLong listener1Count = new AtomicLong();
		final EventListener<String> listener1 = new EventListener<String>()
			{
				public void onEvent(final String event)
				{
					listener1Count.incrementAndGet();
				}
			};

		assertTrue(em.addEventListener(listener1));
		assertFalse(em.addEventListener(listener1));

		final AtomicLong listener2Count = new AtomicLong();
		final EventListener<String> listener2 = new FilteringEventListener<String>()
			{
				public boolean accept(final String event)
				{
					return event != null && event.length() < 5;
				}

				public void onEvent(final String event)
				{
					listener2Count.incrementAndGet();
				}
			};

		assertTrue(em.addEventListener(listener2));
		assertFalse(em.addEventListener(listener2));

		assertEquals(2, em.notify("123"));
		assertEquals(1, listener1Count.get());
		assertEquals(1, listener2Count.get());

		assertEquals(1, em.notify("1234567890"));
		assertEquals(2, listener1Count.get());
		assertEquals(1, listener2Count.get());

		assertEquals(2, em.notifyAsync("123").get().intValue());
		assertEquals(1, em.notifyAsync("1234567890").get().intValue());
		assertEquals(4, listener1Count.get());
		assertEquals(2, listener2Count.get());
	}
}
