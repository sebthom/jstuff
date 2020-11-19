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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.WeakHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
      final WeakHashSet<E> copy = new WeakHashSet<>(size());
      copy.addAll(this);
      return copy;
   }

   private void writeObject(@SuppressWarnings("unused") final ObjectOutputStream oos) throws IOException {
      throw new NotSerializableException();
   }
}
