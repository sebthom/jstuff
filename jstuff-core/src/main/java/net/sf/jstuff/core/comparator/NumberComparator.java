/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberComparator implements Comparator<Number>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final NumberComparator INSTANCE = new NumberComparator();

   protected NumberComparator() {
   }

   @Override
   public int compare(final Number o1, final Number o2) {
      if (o1 == null)
         return -1;
      if (o2 == null)
         return 1;

      final double d1 = o1.doubleValue();
      final double d2 = o2.doubleValue();

      return d1 < d2 //
         ? -1
         : d1 > d2 //
            ? 1
            : 0;
   }
}
