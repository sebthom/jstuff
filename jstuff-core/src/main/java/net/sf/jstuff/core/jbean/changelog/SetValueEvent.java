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
package net.sf.jstuff.core.jbean.changelog;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SetValueEvent extends PropertyChangeEvent {
    private static final long serialVersionUID = 1L;

    public final Object oldValue;
    public final Object newValue;

    public SetValueEvent(final JBean<?> bean, final PropertyDescriptor<?> property, final Object oldValue, final Object newValue) {
        super(bean, property);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    void undo() {
        bean._set((PropertyDescriptor<Object>) property, oldValue);
    }
}
