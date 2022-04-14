/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean;

import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.jbean.changelog.PropertyChangeEvent;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface JBean<Type extends JBean<Type>> {
   void _subscribe(EventListener<PropertyChangeEvent> listener);

   void _unsubscribe(EventListener<PropertyChangeEvent> listener);

   <PType> PType _get(PropertyDescriptor<PType> property);

   <PType> Type _set(PropertyDescriptor<PType> property, PType value);
}
