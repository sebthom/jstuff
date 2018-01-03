/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class AsyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {
    private static final class LazyInitialized {
        private static final ScheduledExecutorService DEFAULT_NOTIFICATION_THREAD = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .daemon(true).priority(Thread.NORM_PRIORITY).namingPattern("EventManager-thread").build());
    }

    private final Set<EventListener<EVENT>> eventListeners = new CopyOnWriteArraySet<EventListener<EVENT>>();

    private ExecutorService executor;

    public AsyncEventDispatcher() {
        this(LazyInitialized.DEFAULT_NOTIFICATION_THREAD);
    }

    public AsyncEventDispatcher(final ExecutorService executor) {
        Args.notNull("executor", executor);
        this.executor = executor;
    }

    public Future<Integer> fire(final EVENT type) {
        @SuppressWarnings("unchecked")
        final EventListener<EVENT>[] copy = eventListeners.toArray(new EventListener[eventListeners.size()]);

        return executor.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                return Events.fire(type, copy);
            }
        });
    }

    public boolean subscribe(final EventListener<EVENT> listener) {
        Args.notNull("listener", listener);
        return eventListeners.add(listener);
    }

    public boolean unsubscribe(final EventListener<EVENT> listener) {
        Args.notNull("listener", listener);
        return eventListeners.remove(listener);
    }

    public void unsubscribeAll() {
        eventListeners.clear();
    }
}
