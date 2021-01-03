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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LongArrayList {
   private long[] elems;
   private int size;

   public LongArrayList() {
      elems = new long[2];
      size = 0;
   }

   public void add(final int index, final long element) {
      if (size == elems.length) {
         resize();
      }
      if (index != size) {
         System.arraycopy(elems, index, elems, index + 1, size - index);
      }
      elems[index] = element;
      size++;
   }

   public void add(final long element) {
      add(size, element);
   }

   public void clear() {
      size = 0;
   }

   public boolean contains(final long element) {
      return indexOf(element) != -1;
   }

   public long get(final int index) {
      return elems[index];
   }

   public int indexOf(final long element) {
      for (int i = 0; i < size; i++) {
         if (elems[i] == element)
            return i;
      }
      return -1;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   public boolean remove(final long element) {
      final int idx = indexOf(element);
      if (idx == -1)
         return false;
      removeAt(idx);
      return true;
   }

   public long removeAt(final int index) {
      final long old = elems[index];
      if (index != size) {
         System.arraycopy(elems, index, elems, index, size - index - 1);
      }
      size--;
      return old;
   }

   public long removeLast() {
      final long old = elems[size - 1];
      size--;
      return old;
   }

   private void resize() {
      final long[] newArray = new long[(int) (size * 1.6)];
      System.arraycopy(elems, size, newArray, 0, size);
      elems = newArray;
   }

   public long set(final int index, final long element) {
      final long old = elems[index];
      elems[index] = element;
      return old;
   }

   public int size() {
      return size;
   }

   public long[] toArray() {
      final long[] result = new long[size];
      System.arraycopy(elems, size, result, 0, size);
      return result;
   }

   public List<Long> toList() {
      final List<Long> list = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
         list.add(elems[i]);
      }
      return list;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < size; i++) {
         sb.append(elems[i]);
         if (i < size - 1) {
            sb.append(", ");
         }
      }
      return sb.append(']').toString();
   }
}
