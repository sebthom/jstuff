/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.jstuff.core.concurrent.ConstantFuture;
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class SyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {

    private final Set<EventListener<EVENT>> eventListeners = new CopyOnWriteArraySet<EventListener<EVENT>>();

    /**
     * @return the number of listeners notified successfully
     */
    public ConstantFuture<Integer> fire(final EVENT event) {
        return ConstantFuture.of(Events.fire(event, eventListeners));
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
