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
public class ByteArrayList {
   private byte[] elems;
   private int size;

   public ByteArrayList() {
      elems = new byte[10];
      size = 0;
   }

   public void add(final byte element) {
      add(size, element);
   }

   public void add(final int index, final byte element) {
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

   public boolean contains(final byte element) {
      return indexOf(element) != -1;
   }

   public byte get(final int index) {
      return elems[index];
   }

   public int indexOf(final byte element) {
      for (int i = 0; i < size; i++) {
         if (elems[i] == element)
            return i;
      }
      return -1;
   }

   public boolean isEmpty() {
      return size == 0;
   }

   public boolean remove(final byte element) {
      final int idx = indexOf(element);
      if (idx == -1)
         return false;
      removeAt(idx);
      return true;
   }

   public byte removeAt(final int index) {
      final byte old = elems[index];
      if (index != size) {
         System.arraycopy(elems, index, elems, index, size - index - 1);
      }
      size--;
      return old;
   }

   public byte removeLast() {
      final byte old = elems[size - 1];
      size--;
      return old;
   }

   private void resize() {
      final byte[] newArray = new byte[(int) (size * 1.6)];
      System.arraycopy(elems, size, newArray, 0, size);
      elems = newArray;
   }

   public byte set(final int index, final byte element) {
      final byte old = elems[index];
      elems[index] = element;
      return old;
   }

   public int size() {
      return size;
   }

   public byte[] toArray() {
      final byte[] result = new byte[size];
      System.arraycopy(elems, size, result, 0, size);
      return result;
   }

   public List<Byte> toList() {
      final List<Byte> list = new ArrayList<>(size);
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
