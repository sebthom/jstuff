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
public class BooleanComparator implements Comparator<Boolean>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final BooleanComparator INSTANCE = new BooleanComparator();

   protected BooleanComparator() {
      super();
   }

   @Override
   public int compare(final Boolean o1, final Boolean o2) {
      final boolean b1 = o1.booleanValue();
      final boolean b2 = o1.booleanValue();

      return b1 == b2 ? 0 : b1 ? 1 : -1;
   }
}
