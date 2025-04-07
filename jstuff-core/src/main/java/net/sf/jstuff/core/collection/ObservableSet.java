/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.ItemChange;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.Operation;
import net.sf.jstuff.core.event.EventDispatcher;

/**
 * A {@link Set} wrapper that emits change events when modified through this {@code ObservableSet}.
 * <p>
 * Changes made directly to the underlying {@link Set} instance are <b>not</b> observable and will not trigger events.
 * <p>
 * Change events include item-level details, and may include index information if the underlying set has a
 * defined or stable iteration
 * order:
 * <ul>
 * <li>For sets with insertion order (e.g. {@link LinkedHashSet}), added and removed items include their index.</li>
 * <li>For sets with stable iteration order (e.g. {@link java.util.SortedSet}), only removed items include their index.</li>
 * <li>For unordered sets (e.g. {@link java.util.HashSet}), indices are set to {@link ObservableCollection#UNKNOWN_INDEX}.</li>
 * </ul>
 * <p>
 * Listeners can subscribe to receive change events describing the type of change and the affected items.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableSet<E> extends ObservableCollection<E, Set<E>> implements Set<E> {
   public static <E> ObservableSet<E> of(final Set<E> set) {
      return new ObservableSet<>(set);
   }

   public ObservableSet(final Set<E> set) {
      super(set);
   }

   public ObservableSet(final Set<E> set, final EventDispatcher<ChangeEvent<E>> dispatcher) {
      super(set, dispatcher);
   }

   @Override
   public boolean remove(final @Nullable Object itemToRemove) {
      if (supportsRemovalIndex) {
         if (wrapped.isEmpty())
            return false;

         final Iterator<E> it = wrapped.iterator();
         int index = 0;
         while (it.hasNext()) {
            final E item = it.next();
            if (Objects.equals(item, itemToRemove)) {
               it.remove();
               @SuppressWarnings("unchecked")
               final E removedElement = (E) itemToRemove;
               fire(Operation.REMOVE, ItemChange.removed(removedElement, index));
               return true;
            }
            index++;
         }
         return false;
      }
      return super.remove(itemToRemove);
   }
}
