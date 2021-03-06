/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortByPropertyComparator<T> extends SortByComparator<T, String> {
   private static final long serialVersionUID = 1L;

   private final Map<String, Comparator<T>> comparators = new HashMap<>();

   @SafeVarargs
   public SortByPropertyComparator(final SortBy<String>... sortBy) {
      super(sortBy);
   }

   @Override
   protected Comparator<T> getComparator(final String sortKey) {
      return comparators.computeIfAbsent(sortKey, PropertyComparator::new);
   }
}
