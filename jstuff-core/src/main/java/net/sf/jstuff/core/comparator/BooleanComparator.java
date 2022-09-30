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
public class BooleanComparator implements Comparator<Boolean>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final BooleanComparator INSTANCE = new BooleanComparator();

   protected BooleanComparator() {
   }

   @Override
   public int compare(final Boolean o1, final Boolean o2) {
      if (o1 == null)
         return -1;
      if (o2 == null)
         return 1;

      final boolean b1 = o1;
      final boolean b2 = o2;

      return b1 == b2 ? 0 : b1 ? 1 : -1;
   }
}
