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
