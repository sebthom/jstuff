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
 * A simple System.currentTimeMillis() based profiler.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Profiler
{
	private long endTime;
	private boolean isRunning = false;
	private long startTime;

	/**
	 * @return the elapsed time in milliseconds
	 */
	public long getElapsedTime()
	{
		return (isRunning ? System.currentTimeMillis() : endTime) - startTime;
	}

	/**
	 * determines if the profiler is currently running
	 * return true if running
	 */
	public boolean isRunning()
	{
		return isRunning;
	}

	/**
	 * starts the profiler
	 * if it is already running, then the watch will be  stopped and restarted
	 */
	public synchronized void restart()
	{
		stop();
		startTime = System.currentTimeMillis();
		isRunning = true;
	}

	/**
	 * starts the profiler, if it is not running already
	 */
	public synchronized void start()
	{
		if (!isRunning)
		{
			startTime = System.currentTimeMillis();
			isRunning = true;
		}
	}

	/**
	 * stops the profiler from measuring
	 */
	public synchronized void stop()
	{
		if (isRunning)
		{
			isRunning = false;
			endTime = System.currentTimeMillis();
		}
	}
}