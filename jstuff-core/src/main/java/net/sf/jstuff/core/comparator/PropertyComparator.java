/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.ogn.ObjectGraphNavigatorDefaultImpl;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private final String propertyPath;

   public PropertyComparator(final String propertyPath) {
      Args.notNull("propertyPath", propertyPath);
      this.propertyPath = propertyPath;
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   public int compare(final @Nullable T o1, final @Nullable T o2) {
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

   @Nullable
   protected Object getValueAt(final Object obj, final String propertyPath) {
      return ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(obj, propertyPath);
   }
}
