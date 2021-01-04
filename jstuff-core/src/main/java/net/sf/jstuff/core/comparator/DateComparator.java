/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateComparator implements Comparator<Date>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final DateComparator INSTANCE = new DateComparator();

   protected DateComparator() {
   }

   @Override
   public int compare(final Date o1, final Date o2) {
      final long n1 = o1.getTime();
      final long n2 = o2.getTime();

      return n1 < n2 ? -1 : n1 > n2 ? 1 : 0;
   }
}
