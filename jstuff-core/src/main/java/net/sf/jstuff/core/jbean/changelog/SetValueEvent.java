/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SetValueEvent extends PropertyChangeEvent {
   private static final long serialVersionUID = 1L;

   @Nullable
   public final Object oldValue;
   @Nullable
   public final Object newValue;

   public SetValueEvent(final JBean<?> bean, final PropertyDescriptor<?> property, final @Nullable Object oldValue,
      final @Nullable Object newValue) {
      super(bean, property);
      this.oldValue = oldValue;
      this.newValue = newValue;
   }

   @SuppressWarnings("unchecked")
   @Override
   void undo() {
      bean._set((PropertyDescriptor<@Nullable Object>) property, oldValue);
   }
}
