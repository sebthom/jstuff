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
public class UnmodifiableComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Comparator<T> delegate;

   public static <T> UnmodifiableComparator<T> of(final Comparator<T> delegate) {
      return new UnmodifiableComparator<>(delegate);
   }

   public UnmodifiableComparator(final Comparator<T> delegate) {
      Args.notNull("delegate", delegate);

      this.delegate = delegate;
   }

   @Override
   public int compare(final T o1, final T o2) {
      return delegate.compare(o1, o2);
   }
}
