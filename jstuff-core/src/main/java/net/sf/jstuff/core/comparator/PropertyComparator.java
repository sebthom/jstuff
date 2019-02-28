/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import net.sf.jstuff.core.ogn.ObjectGraphNavigatorDefaultImpl;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private final String propertyPath;

   public PropertyComparator(final String propertyPath) {
      Args.notNull("propertyPath", propertyPath);
      this.propertyPath = propertyPath;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public int compare(final T o1, final T o2) {
      if (o1 == o2)
         return 0;
      if (o1 == null)
         return 1;
      if (o2 == null)
         return -1;

      final Object v1 = getValueAt(o1, propertyPath);
      final Object v2 = getValueAt(o2, propertyPath);

      if (v1 == v2)
         return 0;
      if (v1 == null)
         return 1;
      if (v2 == null)
         return -1;
      return ((Comparable) v1).compareTo(v2);
   }

   protected Object getValueAt(final Object obj, final String propertyPath) {
      return ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(obj, propertyPath);
   }
}
