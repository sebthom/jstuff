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
package net.sf.jstuff.core.jbean;

import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.jbean.changelog.PropertyChangeEvent;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface JBean<Type extends JBean<Type>> {
    void _subscribe(EventListener<PropertyChangeEvent> listener);

    void _unsubscribe(EventListener<PropertyChangeEvent> listener);

    <PType> PType _get(PropertyDescriptor<PType> property);

    <PType> Type _set(PropertyDescriptor<PType> property, PType value);
}
