/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.AbstractSet;
import java.util.Iterator;

import net.sf.jstuff.core.collection.iterator.Iterators;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class EmptySet<E> extends AbstractSet<E> {

   @Override
   public boolean add(final E e) {
      return true;
   }

   @Override
   public Iterator<E> iterator() {
      return Iterators.empty();
   }

   @Override
   public int size() {
      return 0;
   }
}
