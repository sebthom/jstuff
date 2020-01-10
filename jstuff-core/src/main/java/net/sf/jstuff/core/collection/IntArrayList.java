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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IntArrayList {
   private int[] elems;
   private int size;

   public IntArrayList() {
      elems = new int[10];
      size = 0;
   }

   public void add(final int element) {
      add(size, element);
   }

   public void add(final int index, final int element) {
      if (size == elems.length) {
         resize();
      }
      if (index != size) {
         System.arraycopy(elems, index, elems, index + 1, size - index);
      }
      elems[index] = element;
      size++;
   }

   public void clear() {
      size = 0;
   }

   public boolean contains(final int element) {
      return indexOf(element) != -1;
   }

   public int get(final int index) {
      return elems[index];
   }

   public int indexOf(final int element) {
      for (int i = 0; i < size; i++) {
         if (elems[i] == element)
            return i;
      }
      return -1;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   public boolean remove(final int element) {
      final int idx = indexOf(element);
      if (idx == -1)
         return false;
      removeAt(idx);
      return true;
   }

   public int removeAt(final int index) {
      final int old = elems[index];
      if (index != size) {
         System.arraycopy(elems, index, elems, index, size - index - 1);
      }
      size--;
      return old;
   }

   public int removeLast() {
      final int old = elems[size - 1];
      size--;
      return old;
   }

   private void resize() {
      final int[] newArray = new int[(int) (size * 1.6)];
      System.arraycopy(elems, size, newArray, 0, size);
      elems = newArray;
   }

   public int set(final int index, final int element) {
      final int old = elems[index];
      elems[index] = element;
      return old;
   }

   public int size() {
      return size;
   }

   public int[] toArray() {
      final int[] result = new int[size];
      System.arraycopy(elems, size, result, 0, size);
      return result;
   }

   public List<Integer> toList() {
      final List<Integer> list = new ArrayList<>(size);
      for (int i = 0; i < size; i++) {
         list.add(elems[i]);
      }
      return list;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder('[');
      for (int i = 0; i < size; i++) {
         sb.append(elems[i]);
         if (i < size - 1) {
            sb.append(", ");
         }
      }
      return sb.append(']').toString();
   }
}
