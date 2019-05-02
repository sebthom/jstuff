/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.List;

import net.sf.jstuff.core.comparator.SortBy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
