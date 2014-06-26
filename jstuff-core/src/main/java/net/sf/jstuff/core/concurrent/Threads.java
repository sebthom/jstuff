/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Threads
{
	private static final Logger LOG = Logger.create();

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void join(final Thread thread) throws RuntimeInterruptedException
	{
		Args.notNull("thread", thread);

		try
		{
			LOG.trace("Waiting for thread %s...", thread);
			thread.join();
		}
		catch (final InterruptedException ex)
		{
			LOG.error(ex, "InterruptedException caught");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void sleep(final long millis) throws RuntimeInterruptedException
	{
		try
		{
			LOG.trace("Sending current thread to sleep for %s ms...", millis);
			Thread.sleep(millis);
		}
		catch (final InterruptedException ex)
		{
			LOG.error(ex, "InterruptedException caught");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void sleep(final long millis, final int nanos) throws RuntimeInterruptedException
	{
		try
		{
			LOG.trace("Sending current thread to sleep for %s ms %s nanos...", millis, nanos);
			Thread.sleep(millis, nanos);
		}
		catch (final InterruptedException ex)
		{
			LOG.error(ex, "InterruptedException caught");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Handles InterruptedException correctly.
	 */
	public static void wait(final Object obj, final long millis) throws RuntimeInterruptedException
	{
		try
		{
			LOG.trace("Waiting for %s ms...", millis);
			synchronized (obj)
			{
				obj.wait(millis);
			}
		}
		catch (final InterruptedException ex)
		{
			LOG.error(ex, "InterruptedException caught");
			Thread.currentThread().interrupt();
		}
	}
}
