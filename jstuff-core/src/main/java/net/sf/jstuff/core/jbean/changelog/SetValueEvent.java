/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.jbean.changelog;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
