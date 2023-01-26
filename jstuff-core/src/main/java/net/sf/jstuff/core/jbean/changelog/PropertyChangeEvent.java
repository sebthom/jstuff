/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jbean.changelog;

import java.io.Serializable;

import net.sf.jstuff.core.jbean.JBean;
import net.sf.jstuff.core.jbean.meta.PropertyDescriptor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class PropertyChangeEvent implements Serializable {
   private static final long serialVersionUID = 1L;

   public final JBean<?> bean;
   public final PropertyDescriptor<?> property;

   protected PropertyChangeEvent(final JBean<?> bean, final PropertyDescriptor<?> property) {
      this.bean = bean;
      this.property = property;
   }

   abstract void undo();
}
