/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.IdentityHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IdentityHashSet<E> extends MapBackedSet<E> implements Cloneable {
   private static final long serialVersionUID = 1L;

   public static <E> IdentityHashSet<E> create() {
      return new IdentityHashSet<>();
   }

   public static <E> IdentityHashSet<E> create(final int initialCapacity) {
      return new IdentityHashSet<>(initialCapacity);
   }

   public IdentityHashSet() {
      this(16);
   }

   public IdentityHashSet(final int initialCapacity) {
      super(new IdentityHashMap<>(initialCapacity));
   }

   @Override
   public IdentityHashSet<E> clone() throws CloneNotSupportedException {
      final IdentityHashSet<E> copy = new IdentityHashSet<>(size());
      copy.addAll(this);
      return copy;
   }
}
