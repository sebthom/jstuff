/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.iterator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayIterator<T> implements Iterator<T>, Serializable {

   private static final long serialVersionUID = 1L;

   @SafeVarargs
   public static <T> ArrayIterator<T> of(final T... array) {
      return new ArrayIterator<>(array);
   }

   private final T[] array;
   private int currentIndex;

   @SafeVarargs
   public ArrayIterator(final T... array) {
      this.array = array;
   }

   @Override
   public boolean hasNext() {
      return currentIndex < array.length;
   }

   @Override
   public T next() {
      if (currentIndex < array.length)
         return array[currentIndex++];
      throw new NoSuchElementException();
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
