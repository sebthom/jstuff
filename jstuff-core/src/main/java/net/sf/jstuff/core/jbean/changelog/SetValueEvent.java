/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
