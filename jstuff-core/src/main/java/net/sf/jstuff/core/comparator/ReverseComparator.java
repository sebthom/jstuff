/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReverseComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Comparator<T> delegate;

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
