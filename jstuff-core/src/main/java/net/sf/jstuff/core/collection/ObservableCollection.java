/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.collection.ObservableCollection.ItemAction;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableCollection<E, C extends Collection<E>> implements Collection<E>, EventListenable<ItemAction<E>> {
   public enum BulkAction {
      ADD_ALL,
      CLEAR,
      REMOVE_ALL,
      RETAIN_ALL
   }

   public static class ItemAction<E> {
      /**
       * -1 means the index is not specified
       */
      public final int index;
      public final E item;
      public final ItemActionType type;

      public ItemAction(final ItemActionType type, final E item, final int index) {
         this.type = type;
         this.item = item;
         this.index = index;
      }
   }

   public enum ItemActionType {
      ADD,
      REMOVE
   }

   public static <E> ObservableCollection<E, Collection<E>> of(final Collection<E> set) {
      return new ObservableCollection<>(set);
   }

   protected BulkAction currentBulkAction;

   private final SyncEventDispatcher<ItemAction<E>> events = new SyncEventDispatcher<>();

   protected final C wrapped;

   public ObservableCollection(final C coll) {
      Args.notNull("coll", coll);
      this.wrapped = coll;
   }

   @Override
   public boolean add(final E item) {
      if (wrapped.add(item)) {
         onAdded(item, -1);
         return true;
      }
      return false;
   }

   @Override
   public boolean addAll(final Collection<? extends E> itemsToAdd) {
      if (itemsToAdd == null || itemsToAdd.size() == 0)
         return false;

      currentBulkAction = BulkAction.ADD_ALL;
      try {
         boolean anyAdded = false;
         for (final E item : itemsToAdd)
            if (add(item)) {
               anyAdded = true;
            }
         return anyAdded;
      } finally {
         currentBulkAction = null;
      }

   }

   @Override
   public void clear() {
      currentBulkAction = BulkAction.CLEAR;
      try {
         for (final Iterator<E> it = iterator(); it.hasNext();) {
            it.remove();
         }
      } finally {
         currentBulkAction = null;
      }
   }

   @Override
   public boolean contains(final Object item) {
      return wrapped.contains(item);
   }

   @Override
   public boolean containsAll(final Collection<?> items) {
      return wrapped.containsAll(items);
   }

   public BulkAction getCurrentBulkAction() {
      return currentBulkAction;
   }

   @Override
   public boolean isEmpty() {
      return wrapped.isEmpty();
   }

   public boolean isObserving(final Collection<E> collection) {
      return wrapped == collection;
   }

   @Override
   public Iterator<E> iterator() {
      final Iterator<E> it = wrapped.iterator();
      return new Iterator<>() {
         int index = -1;
         E item;

         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         @Override
         public E next() {
            index++;
            item = it.next();
            return item;
         }

         @Override
         public void remove() {
            it.remove();
            onRemoved(item, index);
         }
      };
   }

   /**
    * @param index negative value if index unknown
    */
   protected void onAdded(final E item, final int index) {
      events.fire(new ItemAction<>(ItemActionType.ADD, item, index));
   }

   /**
    * @param index negative value if index unknown
    */
   protected void onRemoved(final E item, final int index) {
      events.fire(new ItemAction<>(ItemActionType.REMOVE, item, index));
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean remove(final Object item) {
      final boolean removed = wrapped.remove(item);
      if (removed) {
         onRemoved((E) item, -1);
      }
      return removed;
   }

   @Override
   public boolean removeAll(final Collection<?> itemsToRemove) {
      if (itemsToRemove == null || itemsToRemove.size() == 0)
         return false;

      currentBulkAction = BulkAction.REMOVE_ALL;
      try {
         boolean removedAny = false;
         for (final Object item : itemsToRemove)
            if (remove(item)) {
               removedAny = true;
            }
         return removedAny;
      } finally {
         currentBulkAction = null;
      }
   }

   @Override
   public boolean retainAll(final Collection<?> itemsToKeep) {
      currentBulkAction = BulkAction.RETAIN_ALL;
      try {
         boolean removedAny = false;
         for (final Iterator<E> it = wrapped.iterator(); it.hasNext();) {
            final E item = it.next();
            if (itemsToKeep == null || !itemsToKeep.contains(item)) {
               it.remove();
               removedAny = true;
            }
         }
         return removedAny;
      } finally {
         currentBulkAction = null;
      }
   }

   @Override
   public int size() {
      return wrapped.size();
   }

   @Override
   public boolean subscribe(final EventListener<ItemAction<E>> listener) {
      return events.subscribe(listener);
   }

   @Override
   public Object[] toArray() {
      return wrapped.toArray();
   }

   @Override
   public <T> T[] toArray(final T[] a) {
      return wrapped.toArray(a);
   }

   @Override
   public boolean unsubscribe(final EventListener<ItemAction<E>> listener) {
      return events.unsubscribe(listener);
   }
}
