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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.validation.Args;

/**
 * Tracks garbage collection of registered objects and executes callbacks on the event of garbage collection.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GCTracker
{
	private static final class GCReference extends WeakReference<Object>
	{
		private final Runnable runOnGc;
		private final GCTracker tracker;

		protected GCReference(final Object trackedObject, final Runnable runOnGc, final GCTracker tracker)
		{
			super(trackedObject, garbageCollectedRefs);
			this.runOnGc = runOnGc;
			this.tracker = tracker;
		}
	}

	private static Thread CLEANUP_THREAD = new Thread()
		{
			final Logger LOG = Logger.create();
			{
				setPriority(Thread.MAX_PRIORITY);
				setName("GarbageCollectingConcurrentMap-cleanupthread");
				setDaemon(true);
			}

			@Override
			public void run()
			{
				LOG.info("Cleanup Thread running...");
				while (true)
					try
					{
						GCReference ref;
						while ((ref = (GCReference) garbageCollectedRefs.remove()) != null)
						{
							synchronized (ref.tracker.monitoredReferences)
							{
								ref.tracker.monitoredReferences.remove(ref);
							}
							try
							{
								ref.runOnGc.run();
							}
							catch (final Exception ex)
							{
								LOG.error("Failed to execute callback.", ex);
							}
						}
						LOG.info("Cleanup Thread stopping...");
						break;
					}
					catch (final InterruptedException ex)
					{}
			}
		};

	private static final ReferenceQueue<Object> garbageCollectedRefs = new ReferenceQueue<Object>();

	/**
	 * list that holds the GCReference objects to prevent them from being garbage collected before their reference is garbage collected
	 */
	private final List<GCReference> monitoredReferences = new ArrayList<GCReference>(128);

	public GCTracker()
	{
		synchronized (CLEANUP_THREAD)
		{
			if (!CLEANUP_THREAD.isAlive()) CLEANUP_THREAD.start();
		}
	}

	/**
	 * <b>Important:</b> <code>runWhenGCed</code> must not have a direct or indirect hard reference to <code>target</code>, otherwise you are producing a memory leak by preventing garbage collection of <code>target</code>.
	 * @param target the object whose garbage collection should be tracked
	 * @param runWhenGCed a runnable that is invoked on garbage collection of <code>target</code>
	 */
	public void track(final Object target, final Runnable runWhenGCed)
	{
		Args.notNull("target", target);
		Args.notNull("runWhenGCed", runWhenGCed);
		if (target == runWhenGCed)
			throw new IllegalArgumentException(
					"runOnGC callback cannot be the same as the trackedObject, this avoids garbage collection of target.");
		final GCReference ref = new GCReference(target, runWhenGCed, this);
		synchronized (monitoredReferences)
		{
			monitoredReferences.add(ref);
		}
	}
}