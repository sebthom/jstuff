/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateComparator implements Comparator<Date>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final DateComparator INSTANCE = new DateComparator();

   protected DateComparator() {
   }

   @Override
   public int compare(final @Nullable Date o1, final @Nullable Date o2) {
      if (o1 == null)
         return -1;
      if (o2 == null)
         return 1;

      final long n1 = o1.getTime();
      final long n2 = o2.getTime();

      return n1 < n2 //
         ? -1
         : n1 > n2 //
            ? 1
            : 0;
   }
}
