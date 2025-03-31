/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.ItemChange;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.Operation;
import net.sf.jstuff.core.collection.ext.ListExt;
import net.sf.jstuff.core.event.EventDispatcher;

/**
 * A {@link List} wrapper that emits change events when modified through this {@code ObservableList}.
 * <p>
 * Changes made directly to the underlying {@link List} instance are <b>not</b> observable and will not trigger events.
 * <p>
 * Change events include index information.
 * <p>
 * Listeners can subscribe to receive change events describing the type of change and the affected items.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableList<E> extends ObservableCollection<E, List<E>> implements ListExt<E> {

   public static <E> ObservableList<E> of(final List<E> list) {
      return new ObservableList<>(list);
   }

   public ObservableList(final List<E> list) {
      super(list);
   }

   public ObservableList(final List<E> list, final EventDispatcher<ChangeEvent<E>> dispatcher) {
      super(list, dispatcher);
   }

   @Override
   public void add(final int index, final E item) {
      wrapped.add(index, item);
      fire(Operation.ADD, ItemChange.added(item, index));
   }

   @Override
   public boolean addAll(int index, final Collection<? extends E> itemsToAdd) {
      if (itemsToAdd.isEmpty())
         return false;

      final var added = new ArrayList<ItemChange<E>>(itemsToAdd.size());
      for (final E item : itemsToAdd) {
         wrapped.add(index, item);
         added.add(ItemChange.added(item, index));
         index++;
      }
      fire(Operation.ADD_ALL, added);
      return true;
   }

   @Override
   public E get(final int index) {
      return wrapped.get(index);
   }

   @Override
   public int indexOf(final @Nullable Object item) {
      return wrapped.indexOf(item);
   }

   @Override
   public void replaceAll(final UnaryOperator<E> operator) {
      final var replaced = new ArrayList<ItemChange<E>>();

      final ListIterator<E> it = wrapped.listIterator();
      while (it.hasNext()) {
         final E oldItem = it.next();
         final E newItem = operator.apply(oldItem);
         if (newItem != oldItem) {
            final int index = it.previousIndex();
            it.set(newItem);
            replaced.add(ItemChange.replaced(oldItem, newItem, index));
         }
      }

      if (!replaced.isEmpty()) {
         fire(Operation.REPLACE_ALL, replaced);
      }
   }

   @Override
   public int lastIndexOf(final @Nullable Object item) {
      return wrapped.lastIndexOf(item);
   }

   @Override
   public ListIterator<E> listIterator() {
      return listIterator(0);
   }

   @Override
   public ListIterator<E> listIterator(final int index) {
      final ListIterator<E> it = wrapped.listIterator(index);
      return new ListIterator<>() {
         @SuppressWarnings("null")
         E lastReturned;

         @Override
         public void add(final E item) {
            final int index = it.nextIndex();
            it.add(item);
            fire(Operation.ADD, ItemChange.added(item, index));
         }

         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         @Override
         public boolean hasPrevious() {
            return it.hasPrevious();
         }

         @Override
         public E next() {
            lastReturned = it.next();
            return lastReturned;
         }

         @Override
         public int nextIndex() {
            return it.nextIndex();
         }

         @Override
         public E previous() {
            lastReturned = it.previous();
            return lastReturned;
         }

         @Override
         public int previousIndex() {
            return it.previousIndex();
         }

         @Override
         public void remove() {
            final int index = it.previousIndex();
            it.remove();
            fire(Operation.REMOVE, ItemChange.removed(lastReturned, index));
         }

         @Override
         public void set(final E item) {
            if (item != lastReturned) {
               final int index = it.previousIndex();
               it.set(item);
               fire(Operation.SET, ItemChange.replaced(lastReturned, item, index));
               lastReturned = item;
            }
         }
      };
   }

   @Override
   protected boolean removeMatching(final Predicate<? super E> shouldRemove, final Operation action) {
      if (wrapped.isEmpty())
         return false;

      final var removed = new ArrayList<ItemChange<E>>();
      final Iterator<E> it = wrapped.iterator();
      int index = 0;

      while (it.hasNext()) {
         final E item = it.next();
         if (shouldRemove.test(item)) {
            removed.add(ItemChange.removed(item, index));
            it.remove();
         }
         index++;
      }

      if (removed.isEmpty())
         return false;

      fire(action, removed);
      return true;
   }

   @Override
   public E remove(final int index) {
      final E item = wrapped.remove(index);
      fire(Operation.REMOVE, ItemChange.removed(item, index));
      return item;
   }

   @Override
   public boolean remove(final @Nullable Object item) {
      final int index = indexOf(item);
      if (index == -1)
         return false;
      remove(index);
      return true;
   }

   @Override
   public E set(final int index, final E item) {
      final E old = wrapped.set(index, item);
      fire(Operation.SET, ItemChange.replaced(old, item, index));
      return old;
   }

   @Override
   public ObservableList<E> subList(final int fromIndex, final int toIndex) {
      final List<E> subList = wrapped.subList(fromIndex, toIndex);
      final var subObservable = new ObservableList<>(subList);
      subObservable.subscribe(event -> {
         final var indexAdjustedChanges = new ArrayList<ItemChange<E>>(event.changes().size());
         for (final ItemChange<E> change : event.changes()) {
            indexAdjustedChanges.add(new ItemChange<>( //
               change.type(), //
               change.item(), //
               change.index() == UNKNOWN_INDEX ? UNKNOWN_INDEX : change.index() + fromIndex, //
               change.replacedItem()));
         }

         fire(event.operation(), indexAdjustedChanges);
      });

      return subObservable;
   }
}
