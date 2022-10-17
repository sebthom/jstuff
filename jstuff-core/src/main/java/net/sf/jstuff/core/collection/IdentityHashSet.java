/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.IdentityHashMap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
      final var copy = new IdentityHashSet<E>(size());
      copy.addAll(this);
      return copy;
   }
}
