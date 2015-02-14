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
package net.sf.jstuff.core.concurrent;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadsTest extends TestCase
{
	public void testSleep()
	{
		final long now = System.currentTimeMillis();
		Threads.sleep(100);
		assertTrue(System.currentTimeMillis() - now >= 100);
	}

	public void testThreads()
	{
		assertEquals(0, Threads.deadlockedIDs().length);
		assertTrue(Threads.count() > 0);
		assertEquals(Threads.count(), Threads.all().length);
		assertEquals(Threads.count(), Threads.allPrioritized().length);
		assertNotNull(Threads.rootThreadGroup());
	}
}
