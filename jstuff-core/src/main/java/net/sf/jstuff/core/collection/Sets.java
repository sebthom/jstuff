/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Sets {

   public static <T> Set<T> newConcurrentHashSet() {
      return ConcurrentHashMap.newKeySet();
   }

   public static <T> Set<T> newConcurrentHashSet(final Collection<T> items) {
      if (items == null)
         return null;

      final Set<T> set = ConcurrentHashMap.newKeySet(items.size());
      set.addAll(items);
      return set;
   }

   public static <T> Set<T> newConcurrentHashSet(final int initialSize) {
      return ConcurrentHashMap.newKeySet(initialSize);
   }

   @SafeVarargs
   public static <T> Set<T> newConcurrentHashSet(final T... items) {
      if (items == null)
         return null;

      final Set<T> set = ConcurrentHashMap.newKeySet(items.length);
      CollectionUtils.addAll(set, items);
      return set;
   }
}
