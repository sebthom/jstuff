/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortByPropertyComparator<T> extends SortByComparator<T, String> {
   private static final long serialVersionUID = 1L;

   private final Map<String, Comparator<T>> comparators = new HashMap<>();

   @SafeVarargs
   public SortByPropertyComparator(final @NonNull SortBy<String>... sortBy) {
      super(sortBy);
   }

   @Override
   protected Comparator<T> getComparator(final String sortKey) {
      return comparators.computeIfAbsent(sortKey, PropertyComparator::new);
   }
}
