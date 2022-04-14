/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Set;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableSet<E> extends ObservableCollection<E, Set<E>> implements Set<E> {
   public static <E> ObservableSet<E> of(final Set<E> set) {
      return new ObservableSet<>(set);
   }

   public ObservableSet(final Set<E> set) {
      super(set);
   }
}
