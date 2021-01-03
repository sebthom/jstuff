/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
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

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SortByComparator<T, SortKeyType> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private SortBy<SortKeyType>[] sortBy;

   @SafeVarargs
   protected SortByComparator(final SortBy<SortKeyType>... sortBy) {
      setSortBy(sortBy);
   }

   @Override
   public int compare(final T o1, final T o2) {
      for (final SortBy<SortKeyType> sb : sortBy) {
         final int i = compareByKey(o1, o2, sb.getKey());
         if (i == 0) {
            continue;
         }
         return sb.getDirection() == SortDirection.ASC ? i : -i;
      }
      return 0;
   }

   protected int compareByKey(final T o1, final T o2, final SortKeyType sortKey) {
      return getComparator(sortKey).compare(o1, o2);
   }

   protected abstract Comparator<T> getComparator(SortKeyType sortKey);

   @SafeVarargs
   public final void setSortBy(final SortBy<SortKeyType>... sortBy) {
      Args.notNull("sortBy", sortBy);
      this.sortBy = sortBy;
   }
}
