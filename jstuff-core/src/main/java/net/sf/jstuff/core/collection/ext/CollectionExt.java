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
    * Adds all items to the collection accepted by the exclude
    *
    * @return number of items added
    */
   default int addAll(@SuppressWarnings("unchecked") final E... itemsToAdd) {
      return CollectionUtils.addAll(this, itemsToAdd);
   }

   /**
    * Adds all items to the collection accepted by the exclude
    *
    * @return number of items added
    */
   default int addAll(final E[] itemsToAdd, final Predicate<E> exclude) {
      return CollectionUtils.addAll(this, itemsToAdd, exclude);
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    */
   default int addAll(final Iterable<? extends E> itemsToAdd) {
      return CollectionUtils.addAll(this, itemsToAdd);
   }

   /**
    * Adds all items to the collection accepted by the exclude
    *
    * @return number of items added
    */
   default int addAll(final Iterable<? extends E> itemsToAdd, final Predicate<E> exclude) {
      return CollectionUtils.addAll(this, itemsToAdd, exclude);
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    */
   default int addAll(final Iterator<? extends E> itemsToAdd) {
      return CollectionUtils.addAll(this, itemsToAdd);
   }

   /**
    * Adds all items to the collection accepted by the exclude
    *
    * @return number of items added
    */
   default int addAll(final Iterator<? extends E> itemsToAdd, final Predicate<E> exclude) {
      return CollectionUtils.addAll(this, itemsToAdd, exclude);
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

   /**
    * Removes all items not accepted by the exclude
    *
    * @return number of items removed
    */
   default int removeIfNot(final Predicate<E> keep) {
      return CollectionUtils.removeIfNot(this, keep);
   }
}
