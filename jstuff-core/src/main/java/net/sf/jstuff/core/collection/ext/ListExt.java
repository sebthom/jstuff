/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ListExt<E> extends List<E>, CollectionExt<E> {
   @SuppressWarnings("unchecked")
   static <E> ListExt<E> empty() {
      return (ListExt<E>) ArrayListExt.EMPTY_LIST;
   }

   /**
    * Gets the n-th element of the list.
    *
    * @param index a negative index selects an element from the end of the list
    *
    * @throws IndexOutOfBoundsException if index is out of range
    */
   default E getAt(final int index) {
      if (index < 0)
         return get(size() + index);
      return get(index);
   }

   /**
    * Gets the last element of the list.
    *
    * @throws IndexOutOfBoundsException if the list is empty
    */
   default E getLast() {
      return get(size() - 1);
   }

   /**
    * Gets the last element of the list or null if empty.
    */
   @Nullable
   default E getLastOrNull() {
      if (isEmpty())
         return null;
      return getLast();
   }

   /**
    * Removes the last element of the list and returns it.
    *
    * @throws IndexOutOfBoundsException if the list is empty
    */
   default E removeLast() {
      return remove(size() - 1);
   }
}
