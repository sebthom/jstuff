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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumberComparator implements Comparator<Number>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final NumberComparator INSTANCE = new NumberComparator();

   protected NumberComparator() {
   }

   @Override
   public int compare(final Number o1, final Number o2) {
      final double d1 = o1.doubleValue();
      final double d2 = o2.doubleValue();

      return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
   }
}
