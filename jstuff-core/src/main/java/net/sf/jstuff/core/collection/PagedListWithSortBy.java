/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.List;

import net.sf.jstuff.core.comparator.SortBy;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PagedListWithSortBy<E, SortKeyType> extends PagedList<E> {
   private static final long serialVersionUID = 1L;

   private SortBy<SortKeyType>[] sortedBy;

   public PagedListWithSortBy() {
   }

   public PagedListWithSortBy(final Class<E> elementType) {
      super(elementType);
   }

   public PagedListWithSortBy(final Class<E> elementType, final List<E> elements, final int start, final int totalCount) {
      super(elementType, elements, start, totalCount);
   }

   public SortBy<SortKeyType>[] getSortedBy() {
      return sortedBy;
   }

   public void setSortedBy(final SortBy<SortKeyType>[] sortedBy) {
      this.sortedBy = sortedBy;
   }
}
