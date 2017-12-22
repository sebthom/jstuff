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

import java.io.Serializable;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class PropertyChangeEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    public final JBean<?> bean;
    public final PropertyDescriptor<?> property;

    public PropertyChangeEvent(final JBean<?> bean, final PropertyDescriptor<?> property) {
        this.bean = bean;
        this.property = property;
    }

    abstract void undo();
}
