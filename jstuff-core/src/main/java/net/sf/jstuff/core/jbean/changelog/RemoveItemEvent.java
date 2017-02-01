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
package net.sf.jstuff.core.jbean.changelog;

import java.util.Collection;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RemoveItemEvent extends PropertyChangeEvent {
    private static final long serialVersionUID = 1L;

    public final Object item;
    public final int index;

    public RemoveItemEvent(final JBean<?> bean, final PropertyDescriptor<?> property, final Object item, final int index) {
        super(bean, property);
        this.item = item;
        this.index = index;
    }

    @SuppressWarnings("unchecked")
    @Override
    void undo() {
        ((Collection<Object>) bean._get(property)).add(item);
    }
}
