/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Tracks garbage collection of registered objects and executes callbacks in the event of garbage collection.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class GCTracker<EVENT> implements EventListenable<EVENT> {
    /**
     * http://mindprod.com/jgloss/phantom.html
     * http://blog.yohanliyanage.com/2010/10/ktjs-3-soft-weak-phantom-references/
     */
    private final class GCReference extends PhantomReference<Object> {
        private final EVENT eventToFireOnGC;
        private final GCTracker<EVENT> tracker;

        protected GCReference(final Object trackedObject, final EVENT eventToFireOnGC, final GCTracker<EVENT> tracker) {
            super(trackedObject, garbageCollectedRefs);
            this.eventToFireOnGC = eventToFireOnGC;
            this.tracker = tracker;
        }
    }

    private static final class LazyInitialized {
        private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("GCTracker-thread").build());
    }

    private static final Logger LOG = Logger.create();

    private final SyncEventDispatcher<EVENT> events = new SyncEventDispatcher<EVENT>();

    /**
     * synchronized list that holds the GCReference objects to prevent them from being garbage collected before their reference is garbage collected
     */
    private final Queue<GCReference> monitoredReferences = new ConcurrentLinkedQueue<GCReference>();
    private final ReferenceQueue<Object> garbageCollectedRefs = new ReferenceQueue<Object>();

    private volatile ScheduledExecutorService executor;

    private final int intervalMS;

    public GCTracker(final int intervalMS) {
        this.intervalMS = intervalMS;
        executor = LazyInitialized.DEFAULT_NOTIFICATION_THREAD;
        init();
    }

    public GCTracker(final int intervalMS, final ScheduledExecutorService executor) {
        this.intervalMS = intervalMS;
        Args.notNull("executor", executor);
        this.executor = executor;
        init();
    }

    private void init() {
        executor.scheduleWithFixedDelay(new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                GCReference ref;
                while ((ref = (GCReference) garbageCollectedRefs.poll()) != null) {
                    ref.tracker.monitoredReferences.remove(ref);
                    try {
                        ref.tracker.onGCEvent(ref.eventToFireOnGC);
                    } catch (final Exception ex) {
                        LOG.error(ex, "Failed to execute callback.");
                    }
                }
            }
        }, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
    }

    protected void onGCEvent(final EVENT event) {
        events.fire(event);
    }

    public boolean subscribe(final EventListener<EVENT> listener) {
        return events.subscribe(listener);
    }

    /**
     * <b>Important:</b> <code>eventToFireOnGC</code> must not have a direct or indirect hard reference to <code>target</code>, otherwise you are producing a
     * memory leak by preventing garbage collection of <code>target</code>.
     *
     * @param subject the object whose garbage collection should be tracked
     * @param eventToFireOnGC an event that is fired on garbage collection of <code>target</code>
     */
    public void track(final Object subject, final EVENT eventToFireOnGC) {
        Args.notNull("target", subject);

        if (subject == eventToFireOnGC)
            throw new IllegalArgumentException("eventToFireOnGC callback cannot be the same as the target, this avoids garbage collection of target.");

        monitoredReferences.add(new GCReference(subject, eventToFireOnGC, this));
    }

    public boolean unsubscribe(final EventListener<EVENT> listener) {
        return events.unsubscribe(listener);
    }
}