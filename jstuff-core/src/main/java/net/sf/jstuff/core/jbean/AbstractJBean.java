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
package net.sf.jstuff.core.jbean;

import java.io.Serializable;

import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.jbean.changelog.AddItemEvent;
import net.sf.jstuff.core.jbean.changelog.PropertyChangeEvent;
import net.sf.jstuff.core.jbean.changelog.RemoveItemEvent;
import net.sf.jstuff.core.jbean.changelog.SetValueEvent;
import net.sf.jstuff.core.jbean.meta.ClassDescriptor;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractJBean implements JBean<AbstractJBean>, Serializable {
    private static final long serialVersionUID = 1L;

    /* ******************************************************************************
     * Property Change Event Support
     * ******************************************************************************/
    private volatile SyncEventDispatcher<PropertyChangeEvent> events;

    public <T> T _get(final PropertyDescriptor<T> property) {
        throw new UnsupportedOperationException("Unknown property [" + property + "]");
    }

    public abstract ClassDescriptor<?> _getMeta();

    public <T> AbstractJBean _set(final PropertyDescriptor<T> property, final T value) {
        throw new UnsupportedOperationException("Unknown property [" + property + "]");
    }

    public void _subscribe(final EventListener<PropertyChangeEvent> listener) {
        getEvents().subscribe(listener);
    }

    public void _unsubscribe(final EventListener<PropertyChangeEvent> listener) {
        getEvents().unsubscribe(listener);
    }

    private SyncEventDispatcher<PropertyChangeEvent> getEvents() {
        //http://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
        SyncEventDispatcher<PropertyChangeEvent> result = events;
        if (result == null)
            synchronized (this) {
            result = events;
            if (result == null)
                events = result = new SyncEventDispatcher<PropertyChangeEvent>();
        }
        return result;
    }

    /* ******************************************************************************
     * Meta API Support
     * ******************************************************************************/
    protected void onItemAdded(final PropertyDescriptor<?> property, final Object item, final int index) {
        if (events != null)
            events.fire(new AddItemEvent(this, property, item, index));
    }

    protected void onItemRemoved(final PropertyDescriptor<?> property, final Object item, final int index) {
        if (events != null)
            events.fire(new RemoveItemEvent(this, property, item, index));
    }

    protected void onValueSet(final PropertyDescriptor<?> property, final Object oldValue, final Object newValue) {
        if (events != null)
            events.fire(new SetValueEvent(this, property, oldValue, newValue));
    }
}
