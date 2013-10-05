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
import net.sf.jstuff.core.Logger;

import org.apache.commons.lang3.time.StopWatch;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashLockManagerTest extends TestCase
{
	private static Logger LOG = Logger.create();

	private static final int THREADS = 20;
	private static final int ITERATIONS_PER_THREAD = 2500;

	private final ExecutorService es = Executors.newFixedThreadPool(THREADS);
	private int sum = -1;

	final Runnable calculation = new Runnable()
		{
			public void run()
			{
				sum++;
				sum = sum * 2;
				sum = sum / 2;
			}
		};

	public void testWithHashLockManager() throws InterruptedException
	{
		final HashLockManager<String> lockManager = new HashLockManager<String>(100, TimeUnit.MILLISECONDS);

		final StopWatch sw = new StopWatch();
		sw.start();
		sum = 0;
		for (int i = 0; i < THREADS; i++)
			es.execute(new Runnable()
				{
					// intentionally generated new object to proof synchronization is not based on lock identity but hashcode identity
					final String NAMED_LOCK = new String("MY_LOCK");

					public void run()
					{
						for (int i = 0; i < ITERATIONS_PER_THREAD; i++)
							lockManager.doWriteLocked(NAMED_LOCK, calculation);
					}
				});
		Thread.sleep(1000);
		assertEquals(1, lockManager.getLockCount());
		es.shutdown();
		es.awaitTermination(60, TimeUnit.SECONDS);
		sw.stop();
		LOG.info(THREADS * ITERATIONS_PER_THREAD + " thread-safe iterations took " + sw + " sum=" + sum);
		assertEquals(THREADS * ITERATIONS_PER_THREAD, sum);
		Threads.sleep(200); // wait for cleanup thread
		assertEquals(0, lockManager.getLockCount());
	}

	public void testWithoutHashLockManager() throws InterruptedException
	{
		final StopWatch sw = new StopWatch();
		sw.start();
		sum = 0;
		for (int i = 0; i < THREADS; i++)
			es.execute(new Runnable()
				{
					final String NAMED_LOCK = new String("MY_LOCK");

					public void run()
					{
						for (int i = 0; i < ITERATIONS_PER_THREAD; i++)
							// this synchronization of course has no effect since the lock object is a different string instance for each thread
							synchronized (NAMED_LOCK)
							{
								calculation.run();
							}
					}
				});
		es.shutdown();
		es.awaitTermination(60, TimeUnit.SECONDS);
		sw.stop();
		LOG.info(THREADS * ITERATIONS_PER_THREAD + " thread-unsafe iterations took " + sw + " sum=" + sum);
		assertFalse(sum == THREADS * ITERATIONS_PER_THREAD);
	}
}
