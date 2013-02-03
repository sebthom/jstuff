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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventTest extends TestCase
{
	public enum EventType
	{
		FOO,
		BAR
	}

	public void testEvents() throws InterruptedException, ExecutionException
	{
		final EventManager<EventType, String> em = new EventManager<EventType, String>();

		final AtomicLong listener1Count = new AtomicLong();
		final EventListener<EventType, String> listener1 = new EventListener<EventType, String>()
			{
				public void onEvent(final EventType type, final String data)
				{
					listener1Count.incrementAndGet();
				}
			};

		assertTrue(em.subscribe(listener1));
		assertFalse(em.subscribe(listener1));

		final AtomicLong listener2Count = new AtomicLong();
		final EventListener<EventType, String> listener2 = new FilteringEventListener<EventType, String>()
			{
				public boolean accept(final EventType type, final String event)
				{
					return event != null && event.length() < 5;
				}

				public void onEvent(final EventType type, final String event)
				{
					listener2Count.incrementAndGet();
				}
			};

		assertTrue(em.subscribe(listener2));
		assertFalse(em.subscribe(listener2));

		assertEquals(2, em.fire(EventType.FOO, "123"));
		assertEquals(1, listener1Count.get());
		assertEquals(1, listener2Count.get());

		assertEquals(1, em.fire(EventType.FOO, "1234567890"));
		assertEquals(2, listener1Count.get());
		assertEquals(1, listener2Count.get());

		assertEquals(2, em.fireAsync(EventType.BAR, "123").get().intValue());
		assertEquals(1, em.fireAsync(EventType.BAR, "1234567890").get().intValue());
		assertEquals(4, listener1Count.get());
		assertEquals(2, listener2Count.get());
	}
}
