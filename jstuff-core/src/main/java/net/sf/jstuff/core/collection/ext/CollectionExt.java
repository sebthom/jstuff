/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface CollectionExt<E> extends Collection<E> {

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    */
   default int addAll(final Iterator<E> items) {
      return CollectionUtils.addAll(this, items);
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    */
   default int addAll(final Predicate<E> filter, final Iterator<E> items) {
      return CollectionUtils.addAll(this, filter, items);
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    */
   default int addAll(@SuppressWarnings("unchecked") final E... items) {
      return CollectionUtils.addAll(this, items);
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    */
   default int addAll(final Predicate<E> filter, @SuppressWarnings("unchecked") final E... items) {
      return CollectionUtils.addAll(this, filter, items);
   }

   /**
    * @return true if the given item is contained in the collection based on identity comparison
    */
   default boolean containsIdentical(final E searchFor) {
      return CollectionUtils.containsIdentical(this, searchFor);
   }

   default boolean isNotEmpty() {
      return !isEmpty();
   }
}
