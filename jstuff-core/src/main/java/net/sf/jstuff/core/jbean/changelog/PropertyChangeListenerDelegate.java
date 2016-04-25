/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.core.jbean.changelog;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sf.jstuff.core.event.EventListener;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyChangeListenerDelegate implements EventListener<PropertyChangeEvent> {
    private final PropertyChangeListener listener;

    public PropertyChangeListenerDelegate(final PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void onEvent(final PropertyChangeEvent event) {
        if (event instanceof SetValueEvent) {
            final SetValueEvent ev = (SetValueEvent) event;
            listener.propertyChange(new java.beans.PropertyChangeEvent(ev.bean, ev.property.getName(), ev.oldValue, ev.newValue));
        } else if (event instanceof AddItemEvent) {
            final AddItemEvent ev = (AddItemEvent) event;
            listener.propertyChange(new IndexedPropertyChangeEvent(ev.bean, ev.property.getName(), null, ev.item, ev.index));
        } else if (event instanceof RemoveItemEvent) {
            final RemoveItemEvent ev = (RemoveItemEvent) event;
            listener.propertyChange(new IndexedPropertyChangeEvent(ev.bean, ev.property.getName(), ev.item, null, ev.index));
        }
    }
}
