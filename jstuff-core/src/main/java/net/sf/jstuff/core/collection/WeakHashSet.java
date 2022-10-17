/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.WeakHashMap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakHashSet<E> extends MapBackedSet<E> implements Cloneable {
   private static final long serialVersionUID = 1L;

   public static <E> WeakHashSet<E> create() {
      return new WeakHashSet<>();
   }

   public static <E> WeakHashSet<E> create(final int initialCapacity) {
      return new WeakHashSet<>(initialCapacity);
   }

   public static <E> WeakHashSet<E> create(final int initialCapacity, final float growthFactor) {
      return new WeakHashSet<>(initialCapacity, growthFactor);
   }

   public WeakHashSet() {
      this(16, 0.75f);
   }

   public WeakHashSet(final int initialCapacity) {
      this(initialCapacity, 0.75f);
   }

   public WeakHashSet(final int initialCapacity, final float growthFactor) {
      super(new WeakHashMap<>(initialCapacity, growthFactor));
   }

   @Override
   protected WeakHashSet<E> clone() {
      final var copy = new WeakHashSet<E>(size());
      copy.addAll(this);
      return copy;
   }

   private void writeObject(@SuppressWarnings("unused") final ObjectOutputStream oos) throws IOException {
      throw new NotSerializableException();
   }
}
