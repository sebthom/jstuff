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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyChangelog implements EventListener<PropertyChangeEvent>, Serializable {
    private static final long serialVersionUID = 1L;

    private final List<PropertyChangeEvent> propertyChanges = new ArrayList<PropertyChangeEvent>();

    private boolean undoing = false;

    public void clear() {
        propertyChanges.clear();
    }

    public boolean isDirty(final JBean<?> bean) {
        for (final PropertyChangeEvent p : propertyChanges)
            if (p.bean == bean)
                return true;
        return false;
    }

    public boolean isDirty(final JBean<?> bean, final PropertyDescriptor<?> prop) {
        for (final PropertyChangeEvent p : propertyChanges)
            if (p.bean == bean && p.property == prop)
                return true;
        return false;
    }

    public void onEvent(final PropertyChangeEvent event) {
        if (undoing)
            return;
        propertyChanges.add(event);
    }

    public void undo() {
        undoing = true;
        try {
            for (final ListIterator<PropertyChangeEvent> it = propertyChanges.listIterator(propertyChanges.size()); it.hasPrevious();) {
                final PropertyChangeEvent change = it.previous();
                change.undo();
                it.remove();
            }
        } finally {
            undoing = false;
        }
    }

    /**
     * @return true if the marker was found and the modifications where undone
     */
    public boolean undo(final UndoMarker until) {
        if (!propertyChanges.contains(until))
            return false;

        undoing = true;
        try {
            for (final ListIterator<PropertyChangeEvent> it = propertyChanges.listIterator(propertyChanges.size()); it.hasPrevious();) {
                final PropertyChangeEvent change = it.previous();
                change.undo();
                it.remove();

                if (change.equals(until))
                    break;
            }
        } finally {
            undoing = false;
        }
        return true;
    }

    public UndoMarker undoMarker() {
        final UndoMarker um = new UndoMarker();
        propertyChanges.add(um);
        return um;
    }
}
