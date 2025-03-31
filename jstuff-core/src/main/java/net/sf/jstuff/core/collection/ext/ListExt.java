/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ListExt<E> extends List<E>, CollectionExt<E> {
   @SuppressWarnings("unchecked")
   static <E> ListExt<E> empty() {
      return (ListExt<E>) ArrayListExt.EMPTY_LIST;
   }

   /**
    * @return the first item, or {@code null} if the list is {@code null} or empty.
    */
   default @Nullable E findFirst() {
      return CollectionUtils.findFirst(this);
   }

   /**
    * @return the first item matching the given filter, or {@code null} if none.
    */
   default @Nullable E findFirstMatching(final Predicate<E> filter) {
      return CollectionUtils.findFirstMatching(this, filter);
   }

   /**
    * @return the last item or null if empty
    */
   default @Nullable E findLast() {
      return CollectionUtils.findLast(this);
   }

   /**
    * Gets the n-th item of the list.
    *
    * @param index a negative index selects an item from the end of the list
    *
    * @throws IndexOutOfBoundsException if index is out of range
    */
   default E getAt(final int index) {
      return CollectionUtils.getAt(this, index);
   }

   /**
    * Gets the n-th item of the list or the default value.
    *
    * @param index a negative index selects an item from the end of the list
    */
   default E getAtOrDefault(int index, final E defaultValue) {
      if (index < 0) {
         index = size() + index;
      }
      return index > -1 && index < size() ? get(index) : defaultValue;
   }

   /**
    * Gets the last item of the list.
    *
    * @throws IndexOutOfBoundsException if the list is empty
    */
   default E getLast() {
      return CollectionUtils.getLast(this);
   }

   /**
    * Removes the last item of the list and returns it.
    *
    * @throws IndexOutOfBoundsException if the list is empty
    */
   default E removeLast() {
      return CollectionUtils.removeLast(this);
   }

   /**
    * Sets the item at the specified index.
    * <p>
    * Supports negative indices ({@code -1} = last item, {@code -2} = second-last, etc.).
    *
    * @return the previous item at the index
    * @throws IndexOutOfBoundsException if index is out of range
    */
   default E setAt(final int index, final E item) {
      return CollectionUtils.setAt(this, index, item);
   }
}
