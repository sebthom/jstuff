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
package net.sf.jstuff.core;

/** 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadUtils
{
	private final static Logger LOG = Logger.get();

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void join(final Thread thread)
	{
		Assert.argumentNotNull("thread", thread);

		try
		{
			LOG.trace("Waiting for thread %s...", thread);
			thread.join();
		}
		catch (final InterruptedException ex)
		{
			LOG.debug("Thread %s interrupted", ex, thread);
			thread.interrupt();
		}
	}

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void sleep(final long millis)
	{
		try
		{
			LOG.trace("Sending current thread to sleep for %s ms...", millis);
			Thread.sleep(millis);
		}
		catch (final InterruptedException ex)
		{
			final Thread t = Thread.currentThread();
			LOG.debug("Current thread %s interrupted", ex, t);
			// http://stackoverflow.com/questions/1895881/why-would-you-catch-interruptedexception-to-call-thread-currentthread-interrupt
			// http://www.ibm.com/developerworks/java/library/j-jtp05236.html
			t.interrupt();
		}
	}

	protected ThreadUtils()
	{
		super();
	}
}
