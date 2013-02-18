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
package net.sf.jstuff.core;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GCTrackerTest extends TestCase
{
	private volatile int garbageCollected;

	public void testGCTracker() throws InterruptedException
	{
		final GCTracker tracker = new GCTracker();

		final Runnable countGC = new Runnable()
			{
				public void run()
				{
					garbageCollected++;
				}
			};

		final int objects = 10000;
		final Thread t1 = new Thread()
			{
				@Override
				public void run()
				{
					for (int i = 0; i < objects; i++)
					{
						System.out.println("T1 " + i);
						tracker.track(new Object(), countGC);
					}
				};
			};
		final Thread t2 = new Thread()
			{
				@Override
				public void run()
				{
					for (int i = 0; i < objects; i++)
					{
						System.err.println("T2 " + i);
						tracker.track(new Object(), countGC);
					}
				};
			};
		t1.start();
		t2.start();
		t1.join();
		t2.join();
		System.gc();
		Thread.sleep(500);
		assertEquals(2 * objects, garbageCollected);
	}
}
