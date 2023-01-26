/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableList<E> extends ObservableCollection<E, List<E>> implements List<E> {
   public static <E> ObservableList<E> of(final List<E> list) {
      return new ObservableList<>(list);
   }

   public ObservableList(final List<E> list) {
      super(list);
   }

   @Override
   public boolean add(final E item) {
      wrapped.add(item);
      onAdded(item, size() - 1);
      return true;
   }

   @Override
   public void add(final int index, final E item) {
      wrapped.add(index, item);
      onAdded(item, index);
   }

   @Override
   public boolean addAll(int index, final @Nullable Collection<? extends E> itemsToAdd) {
      if (itemsToAdd == null || itemsToAdd.size() == 0)
         return false;

      currentBulkAction = BulkAction.ADD_ALL;
      try {
         for (final E item : itemsToAdd) {
            add(index++, item);
         }
         return true;
      } finally {
         currentBulkAction = null;
      }
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
         private E current;

         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         @Override
         public E next() {
            current = it.next();
            return current;
         }

         @Override
         public boolean hasPrevious() {
            return it.hasPrevious();
         }

         @Override
         public E previous() {
            current = it.previous();
            return current;
         }

         @Override
         public int nextIndex() {
            return it.nextIndex();
         }

         @Override
         public int previousIndex() {
            return it.previousIndex();
         }

         @Override
         public void remove() {
            final int index = nextIndex() - 1;
            it.remove();
            onRemoved(current, index);
         }

         @Override
         public void set(final E item) {
            if (item != current) {
               final int index = nextIndex() - 1;
               it.set(item);
               onRemoved(item, index);
               onAdded(current, index);
               current = item;
            }
         }

         @Override
         public void add(final E item) {
            final int index = nextIndex();
            it.add(item);
            onAdded(current, index);
         }
      };
   }

   @Override
   public E remove(final int index) {
      final E item = wrapped.remove(index);
      onRemoved(item, index);
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
      if (old != item) {
         if (old != null) {
            onRemoved(old, index);
         }
         onAdded(old, index);
      }
      return old;
   }

   @Override
   public List<E> subList(final int fromIndex, final int toIndex) {
      throw new UnsupportedOperationException("Not implemented");
   }
}
