/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeComparator<T> extends net.sf.jstuff.core.types.Composite.Default<Comparator<T>> implements Comparator<T> {
   private static final long serialVersionUID = 1L;

   public CompositeComparator() {
   }

   public CompositeComparator(final Collection<? extends Comparator<T>> comparators) {
      super(comparators);
   }

   @SuppressWarnings("null")
   @SafeVarargs
   public CompositeComparator(final @NonNullByDefault({}) Comparator<T>... comparators) {
      super(comparators);
   }

   @Override
   public int compare(final T o1, final T o2) {
      for (final Comparator<T> comparator : components) {
         final int rc = comparator.compare(o1, o2);
         if (rc != 0)
            return rc;
      }
      return 0;
   }
}
