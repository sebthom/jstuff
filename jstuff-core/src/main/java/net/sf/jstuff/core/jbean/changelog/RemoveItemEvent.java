/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RemoveItemEvent extends PropertyChangeEvent {
   private static final long serialVersionUID = 1L;

   @Nullable
   public final Object item;
   public final int index;

   public RemoveItemEvent(final JBean<?> bean, final PropertyDescriptor<?> property, final @Nullable Object item, final int index) {
      super(bean, property);
      this.item = item;
      this.index = index;
   }

   @SuppressWarnings("unchecked")
   @Override
   void undo() {
      ((Collection<@Nullable Object>) bean._get(property)).add(item);
   }
}
