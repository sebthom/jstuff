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

import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;
import net.sf.jstuff.core.validation.Args;

/**
 * Tracks garbage collection of registered objects and executes callbacks on the event of garbage collection.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GCTracker<Event> implements EventListenable<Event>
{
	private static final class GCReference<Event> extends WeakReference<Object>
	{
		private final Object eventToFireOnGC;
		private final GCTracker<Event> tracker;

		protected GCReference(final Object trackedObject, final Event eventToFireOnGC, final GCTracker<Event> tracker)
		{
			super(trackedObject, garbageCollectedRefs);
			this.eventToFireOnGC = eventToFireOnGC;
			this.tracker = tracker;
		}
	}

	private static Thread CLEANUP_THREAD = new Thread()
		{
			private final Logger LOG = Logger.create();
			{
				setPriority(Thread.MAX_PRIORITY);
				setName("GarbageCollectingConcurrentMap-cleanupthread");
				setDaemon(true);
			}

			@SuppressWarnings("unchecked")
			@Override
			public void run()
			{
				LOG.info("Cleanup Thread running...");
				while (true)
					try
					{
						GCReference<Object> ref;
						while ((ref = (GCReference<Object>) garbageCollectedRefs.remove()) != null)
						{
							synchronized (ref.tracker.monitoredReferences)
							{
								ref.tracker.monitoredReferences.remove(ref);
							}
							try
							{
								ref.tracker.onGCEvent(ref.eventToFireOnGC);
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

	private final EventManager<Event> events = new EventManager<Event>();

	/**
	 * list that holds the GCReference objects to prevent them from being garbage collected before their reference is garbage collected
	 */
	private final List<GCReference<Event>> monitoredReferences = new ArrayList<GCReference<Event>>(128);

	public GCTracker()
	{
		synchronized (CLEANUP_THREAD)
		{
			if (!CLEANUP_THREAD.isAlive()) CLEANUP_THREAD.start();
		}
	}

	public <EventType extends Event> boolean subscribe(final EventListener<EventType> listener)
	{
		return events.subscribe(listener);
	}

	protected void onGCEvent(final Event event)
	{
		events.fire(event);
	}

	/**
	 * <b>Important:</b> <code>eventToFireOnGC</code> must not have a direct or indirect hard reference to <code>target</code>, otherwise you are producing a memory leak by preventing garbage collection of <code>target</code>.
	 * @param target the object whose garbage collection should be tracked
	 * @param eventToFireOnGC an event that is fired on garbage collection of <code>target</code>
	 */
	public void track(final Object target, final Event eventToFireOnGC)
	{
		Args.notNull("target", target);
		if (target == eventToFireOnGC)
			throw new IllegalArgumentException(
					"eventToFireOnGC callback cannot be the same as the target, this avoids garbage collection of target.");
		final GCReference<Event> ref = new GCReference<Event>(target, eventToFireOnGC, this);
		synchronized (monitoredReferences)
		{
			monitoredReferences.add(ref);
		}
	}

	public <EventType extends Event> boolean unsubscribe(final EventListener<EventType> listener)
	{
		return events.unsubscribe(listener);
	}
}