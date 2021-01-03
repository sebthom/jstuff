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

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReverseComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Comparator<T> delegate;

   public static <T> ReverseComparator<T> of(final Comparator<T> delegate) {
      return new ReverseComparator<>(delegate);
   }

   /**
    * @param delegate the comparator to reverse
    */
   public ReverseComparator(final Comparator<T> delegate) {
      this.delegate = delegate;
   }

   @Override
   public int compare(final T o1, final T o2) {
      return -delegate.compare(o1, o2);
   }
}
