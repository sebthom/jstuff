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
package net.sf.jstuff.core;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.EventManager;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ref.FinalRef;
import net.sf.jstuff.core.ref.LazyInitializedRef;
import net.sf.jstuff.core.ref.Ref;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Tracks garbage collection of registered objects and executes callbacks in the event of garbage collection.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GCTracker<Event> implements EventListenable<Event>
{
	/**
	 * http://mindprod.com/jgloss/phantom.html
	 * http://blog.yohanliyanage.com/2010/10/ktjs-3-soft-weak-phantom-references/
	 */
	private final class GCReference extends PhantomReference<Object>
	{
		private final Event eventToFireOnGC;
		private final GCTracker<Event> tracker;

		protected GCReference(final Object trackedObject, final Event eventToFireOnGC, final GCTracker<Event> tracker)
		{
			super(trackedObject, garbageCollectedRefs);
			this.eventToFireOnGC = eventToFireOnGC;
			this.tracker = tracker;
		}
	}

	private static final Logger LOG = Logger.create();

	private final EventManager<Event> events = new EventManager<Event>();

	/**
	 * synchronized list that holds the GCReference objects to prevent them from being garbage collected before their reference is garbage collected
	 */
	private final Queue<GCReference> monitoredReferences = new ConcurrentLinkedQueue<GCReference>();

	private final ReferenceQueue<Object> garbageCollectedRefs = new ReferenceQueue<Object>();

	private volatile Ref<ExecutorService> executorService;

	public GCTracker(final boolean useDeamonThread)
	{
		executorService = new LazyInitializedRef<ExecutorService>()
			{
				@Override
				protected ExecutorService create()
				{
					final String threadName = getClass().getSimpleName() + System.identityHashCode(GCTracker.this);
					LOG.info("Creating fixed thread pool with 1 threads");
					return Executors.newFixedThreadPool(
							1,
							new BasicThreadFactory.Builder().daemon(useDeamonThread).priority(Thread.NORM_PRIORITY)
									.namingPattern(threadName).build());
				}
			};

		init();
	}

	public GCTracker(final ExecutorService executorService)
	{
		Args.notNull("executorService", executorService);
		this.executorService = FinalRef.of(executorService);
		init();
	}

	private void init()
	{
		executorService.get().execute(new Runnable()
			{
				@SuppressWarnings("unchecked")
				public void run()
				{
					try
					{
						LOG.info("GC event dispatching started...");
						while (true)
						{
							GCReference ref;
							while ((ref = (GCReference) garbageCollectedRefs.remove()) != null)
							{
								ref.tracker.monitoredReferences.remove(ref);
								try
								{
									ref.tracker.onGCEvent(ref.eventToFireOnGC);
								}
								catch (final Exception ex)
								{
									LOG.error(ex, "Failed to execute callback.");
								}
							}
						}
					}
					catch (final InterruptedException ex)
					{
						LOG.warn("GC event dispatching stopped.");
						Threads.handleInterruptedException(ex);
					}
				}
			});
	}

	protected void onGCEvent(final Event event)
	{
		events.fire(event);
	}

	public <EventType extends Event> boolean subscribe(final EventListener<EventType> listener)
	{
		return events.subscribe(listener);
	}

	/**
	 * <b>Important:</b> <code>eventToFireOnGC</code> must not have a direct or indirect hard reference to <code>target</code>, otherwise you are producing a memory leak by preventing garbage collection of <code>target</code>.
	 * @param subject the object whose garbage collection should be tracked
	 * @param eventToFireOnGC an event that is fired on garbage collection of <code>target</code>
	 */
	public void track(final Object subject, final Event eventToFireOnGC)
	{

		Args.notNull("target", subject);
		if (subject == eventToFireOnGC)
			throw new IllegalArgumentException(
					"eventToFireOnGC callback cannot be the same as the target, this avoids garbage collection of target.");
		monitoredReferences.add(new GCReference(subject, eventToFireOnGC, this));
	}

	public <EventType extends Event> boolean unsubscribe(final EventListener<EventType> listener)
	{
		return events.unsubscribe(listener);
	}
}