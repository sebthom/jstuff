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
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManagerTest extends TestCase
{
	private int count;

	private static final int THREADS = 20;
	private static final int INCREASE_BY = 500;

	private final ExecutorService es = Executors.newFixedThreadPool(THREADS);

	private final HashLockManager lockManager = new HashLockManager();

	public void testWithHashLockManager() throws InterruptedException
	{
		count = 0;
		for (int i = 0; i < THREADS; i++)
			es.execute(new Runnable()
				{
					public void run()
					{
						for (int i = 0; i < INCREASE_BY; i++)
							lockManager.doWriteLocked(new String("MY_LOCK"), new Runnable()
								{
									public void run()
									{
										count++;
										ThreadUtils.sleep(1);
									}
								});
					}
				});
		Thread.sleep(10);
		assertEquals(1, lockManager.getLockCount());
		es.shutdown();
		es.awaitTermination(30, TimeUnit.SECONDS);
		assertEquals(THREADS * INCREASE_BY, count);
		assertEquals(0, lockManager.getLockCount());
	}

	public void testWithoutHashLockManager() throws InterruptedException
	{
		count = 0;
		for (int i = 0; i < THREADS; i++)
			es.execute(new Runnable()
				{
					public void run()
					{
						for (int i = 0; i < INCREASE_BY; i++)
						{
							count++;
							ThreadUtils.sleep(1);
						}
					}
				});
		es.shutdown();
		es.awaitTermination(30, TimeUnit.SECONDS);
		assertFalse(count == THREADS * INCREASE_BY);
	}
}
