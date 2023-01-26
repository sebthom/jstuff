/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.jpa;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.core.fluent.Fluent;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class JpqlOrderByBuilder {
   private final SortBy<String>[] defaultSortBy;
   private final Map<String, String> mappings = new HashMap<>(4);

   @SafeVarargs
   public JpqlOrderByBuilder(final SortBy<String>... defaultSortBy) {
      this.defaultSortBy = defaultSortBy;
   }

   @Fluent
   public JpqlOrderByBuilder addMapping(final String sortField, final String jpqlExpression) {
      mappings.put(sortField, jpqlExpression);
      return this;
   }

   public String buildOrderBy(final SortBy<String>[] sortBy) throws IllegalArgumentException {
      final var orderBy = new StringBuilder();

      for (final SortBy<String> sb : getActiveSortBy(sortBy)) {
         final String jpqlExpression = mappings.get(sb.getKey());
         if (jpqlExpression == null)
            throw new IllegalArgumentException("Invalid sorting field [" + sb.getKey() + "]");

         orderBy.append(' ');
         orderBy.append(jpqlExpression);
         orderBy.append(' ');
         orderBy.append(sb.getDirection());
         orderBy.append(',');
      }
      orderBy.setLength(orderBy.length() - 1); // strip off the last comma
      return orderBy.toString();
   }

   public SortBy<String>[] getActiveSortBy(final SortBy<String> @Nullable [] sortBy) {
      if (sortBy == null || sortBy.length == 0)
         return defaultSortBy;
      return sortBy;
   }
}
