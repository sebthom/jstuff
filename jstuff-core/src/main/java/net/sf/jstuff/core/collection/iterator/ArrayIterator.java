/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.iterator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
