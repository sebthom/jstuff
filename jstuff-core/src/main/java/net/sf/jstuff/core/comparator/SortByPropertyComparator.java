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

import java.util.Comparator;
import java.util.Map;

import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortByPropertyComparator<T> extends SortByComparator<T, String> {
   private final Map<String, Comparator<T>> comparators = Maps.newHashMap();

   public SortByPropertyComparator(final SortBy<String>... sortBy) {
      super(sortBy);
   }

   @Override
   protected Comparator<T> getComparator(final String sortKey) {
      Comparator<T> comp = comparators.get(sortKey);
      if (comp == null) {
         comp = new PropertyComparator<T>(sortKey);
         comparators.put(sortKey, comp);
      }
      return comparators.get(sortKey);
   }
}
