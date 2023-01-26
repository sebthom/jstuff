/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SortByComparator<T, SortKeyType> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private SortBy<@NonNull SortKeyType>[] sortBy;

   @SafeVarargs
   protected SortByComparator(final @NonNull SortBy<SortKeyType>... sortBy) {
      this.sortBy = sortBy;
   }

   @Override
   public int compare(final T o1, final T o2) {
      for (final var sb : sortBy) {
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
   public final void setSortBy(final @NonNull SortBy<SortKeyType>... sortBy) {
      this.sortBy = sortBy;
   }
}
