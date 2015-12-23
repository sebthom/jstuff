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
package net.sf.jstuff.core.event;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventManager<Event> implements EventListenable<Event> {
    private static final class LazyInitialized {
        private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
    }

    private final Set<EventListener<Event>> eventListeners = new CopyOnWriteArraySet<EventListener<Event>>();

    private ExecutorService executor;

    public EventManager() {
        this(LazyInitialized.DEFAULT_NOTIFICATION_THREAD);
    }

    public EventManager(final ExecutorService executor) {
        Args.notNull("executor", executor);
        this.executor = executor;
    }

    public int fire(final Event type) {
        return Events.fire(type, eventListeners);
    }

    public Future<Integer> fireAsync(final Event type) {
        @SuppressWarnings("unchecked")
        final EventListener<Event>[] copy = eventListeners.toArray(new EventListener[eventListeners.size()]);

        return executor.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                return Events.fire(type, copy);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <EventType extends Event> boolean subscribe(final EventListener<EventType> listener) {
        Args.notNull("listener", listener);
        return eventListeners.add((EventListener<Event>) listener);
    }

    public <EventType extends Event> boolean unsubscribe(final EventListener<EventType> listener) {
        Args.notNull("listener", listener);
        return eventListeners.remove(listener);
    }

    public void unsubscribeAll() {
        eventListeners.clear();
    }
}
