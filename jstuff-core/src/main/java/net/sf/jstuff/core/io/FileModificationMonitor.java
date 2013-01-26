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
package net.sf.jstuff.core.io;

import java.io.File;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.jstuff.core.validation.Args;

/**
 * Monitors the modification date of the given file
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FileModificationMonitor extends Observable
{
	private static long getModificationDate(final File file)
	{
		return file == null || !file.exists() ? -1 : file.lastModified();
	}

	private final File file;
	private long interval = 1000;
	private boolean isMonitoring = false;

	private long lastModified;
	private final Timer timer = new Timer();

	private final TimerTask timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if (isMonitoring)
				{
					final long currentLastModified = getModificationDate(file);
					if (lastModified != currentLastModified)
					{
						lastModified = currentLastModified;
						setChanged();
						notifyObservers();
					}
				}
			}
		};

	public FileModificationMonitor(final File file)
	{
		Args.notNull("file", file);

		this.file = file;
	}

	public FileModificationMonitor(final File file, final long interval)
	{
		Args.notNull("file", file);

		this.file = file;
		this.interval = interval;
	}

	/**
	 * @return Returns the file being monitored
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @return Returns the monitoring interval.
	 */
	public long getInterval()
	{
		return interval;
	}

	/**
	 * @return Determines if the monitor is currently running.
	 */
	public boolean isMonitoring()
	{
		return isMonitoring;
	}

	public synchronized void startMonitoring()
	{
		if (!isMonitoring)
		{
			lastModified = getModificationDate(file);
			timer.schedule(timerTask, 0, interval);
			isMonitoring = true;
		}
	}

	public synchronized void stopMonitoring()
	{
		timer.cancel();
		isMonitoring = false;
	}
}