/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.util.Collection;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AddItemEvent extends PropertyChangeEvent {
   private static final long serialVersionUID = 1L;

   public final Object item;
   public final int index;

   public AddItemEvent(final JBean<?> bean, final PropertyDescriptor<?> property, final Object item, final int index) {
      super(bean, property);
      this.item = item;
      this.index = index;
   }

   @Override
   void undo() {
      ((Collection<?>) bean._get(property)).remove(item);
   }
}
