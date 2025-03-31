/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.ItemChange;
import net.sf.jstuff.core.collection.ObservableCollection.ChangeEvent.Operation;
import net.sf.jstuff.core.collection.ext.CollectionExt;
import net.sf.jstuff.core.event.EventDispatcher;
import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.validation.Args;

/**
 * A {@link Collection} wrapper that emits {@link ChangeEvent}s when modified through this {@code ObservableCollection}.
 * <p>
 * Changes made directly to the underlying {@link Collection} instance are <b>not</b> observable and will not trigger events.
 * <p>
 * {@link ChangeEvent}s may include index information if the underlying collection has a defined order:
 * <ul>
 * <li>For collections with insertion order (e.g. {@link List}, {@link LinkedHashSet}), added and removed items include their index.</li>
 * <li>For collections with stable iteration order (e.g. {@link java.util.SortedSet}), only removed items include their index.</li>
 * <li>For unordered collections (e.g. {@link java.util.HashSet}), indices are {@code -1}.</li>
 * </ul>
 * <p>
 * Listeners can subscribe to receive {@link ChangeEvent}s describing the type of change and the affected items.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableCollection<E, C extends Collection<E>> implements CollectionExt<E>, EventListenable<ChangeEvent<E>> {

   /**
    * Describes a change that occurred in an {@link ObservableCollection}.
    * <p>
    * Contains the collection operation (e.g. add, remove, clear) and a list of item-level changes
    * detailing which items were affected and their indices (if known).
    *
    * @param source the {@link ObservableCollection} that triggered the event
    * @param operation the collection operation that caused the change
    * @param changes the list of {@link ItemChange}s describing the affected items
    */
   public record ChangeEvent<E>(ObservableCollection<E, ?> source, Operation operation, List<ItemChange<E>> changes) {

      /**
       * Describes the type of modification performed on an {@link ObservableCollection}.
       * Represents high-level collection operations that can trigger a {@link ChangeEvent}.
       * <p>
       * Each action corresponds to a standard {@link Collection} operation.
       */
      public enum Operation {
         /** @see Collection#add(Object) */
         ADD,
         /** @see Collection#addAll(Collection) */
         ADD_ALL,
         /** @see Collection#clear() */
         CLEAR,
         /** @see Collection#remove(Object) */
         REMOVE,
         /** @see Collection#removeAll(Collection) */
         REMOVE_ALL,
         /** @see Collection#removeIf(Predicate) */
         REMOVE_IF,
         /** @see List#replaceAll(UnaryOperator) */
         REPLACE_ALL,
         /** @see List#retainAll(Collection) */
         RETAIN_ALL,
         /** @see List#set(int, Object) */
         SET;

         public boolean isAdd() {
            return this == ADD || this == ADD_ALL;
         }

         public boolean isRemove() {
            return switch (this) {
               case CLEAR, REMOVE, REMOVE_ALL, REMOVE_IF, RETAIN_ALL -> true;
               default -> false;
            };
         }

         public boolean isReplace() {
            return this == REPLACE_ALL || this == SET;
         }
      }

      /**
       * Describes a single item-level change in an {@link ObservableCollection}.
       *
       * @param type the type of change applied to the item
       * @param item the affected item
       * @param index the index of the item in the collection, or {@code -1} if the index is unknown (e.g. for unordered collections)
       * @param replacedItem for {@link ItemChange.Type#REPLACE} actions, the item that was replaced; {@code null} for {@code ADD}
       *           and {@code REMOVE} actions
       */
      public record ItemChange<E>(ItemChange.Type type, E item, int index, @Nullable E replacedItem) {
         public enum Type {
            ADD,
            REMOVE,
            REPLACE
         }

         static <E> ItemChange<E> added(final E item, final int index) {
            return new ItemChange<>(ItemChange.Type.ADD, item, index, null);
         }

         static <E> ItemChange<E> removed(final E item, final int index) {
            return new ItemChange<>(ItemChange.Type.REMOVE, item, index, null);
         }

         static <E> ItemChange<E> replaced(final E replacedItem, final E item, final int index) {
            return new ItemChange<>(ItemChange.Type.REPLACE, item, index, replacedItem);
         }
      }

      @Override
      public String toString() {
         return Strings.toString(this, //
            "operation", operation, //
            "changes", changes);
      }
   }

   protected static final int UNKNOWN_INDEX = -1;

   protected final EventDispatcher<ChangeEvent<E>> events;
   protected final C wrapped;

   protected final boolean supportsInsertionIndex;
   protected final boolean supportsRemovalIndex;

   public ObservableCollection(final C coll) {
      this(coll, new SyncEventDispatcher<>());
   }

   public ObservableCollection(final C coll, final EventDispatcher<ChangeEvent<E>> dispatcher) {
      Args.notNull("coll", coll);
      wrapped = coll;
      supportsInsertionIndex = coll instanceof List || coll instanceof LinkedHashSet;
      supportsRemovalIndex = supportsInsertionIndex || coll instanceof java.util.SortedSet;
      events = dispatcher;
   }

   @Override
   public boolean add(final E item) {
      if (wrapped.add(item)) {
         final int index = supportsInsertionIndex ? wrapped.size() - 1 : UNKNOWN_INDEX;
         fire(Operation.ADD, ItemChange.added(item, index));
         return true;
      }
      return false;
   }

   @Override
   public boolean addAll(final Collection<? extends E> itemsToAdd) {
      return addAllInternal(itemsToAdd.iterator(), null) > 0;
   }

   @Override
   public int addAll(@SuppressWarnings("unchecked") final E... itemsToAdd) {
      return addAllInternal(Arrays.asList(itemsToAdd).iterator(), null);
   }

   @Override
   public int addAll(final E[] itemsToAdd, final Predicate<E> filter) {
      return addAllInternal(Arrays.asList(itemsToAdd).iterator(), filter);
   }

   @Override
   public int addAll(final Iterable<? extends E> itemsToAdd) {
      return addAllInternal(itemsToAdd.iterator(), null);
   }

   @Override
   public int addAll(final Iterable<? extends E> itemsToAdd, final Predicate<E> filter) {
      return addAllInternal(itemsToAdd.iterator(), filter);
   }

   @Override
   public int addAll(final Iterator<? extends E> itemsToAdd) {
      return addAllInternal(itemsToAdd, null);
   }

   @Override
   public int addAll(final Iterator<? extends E> itemsToAdd, final Predicate<E> filter) {
      return addAllInternal(itemsToAdd, filter);
   }

   private int addAllInternal(final Iterator<? extends E> itemsToAdd, final @Nullable Predicate<E> filter) {
      if (!itemsToAdd.hasNext())
         return 0;

      final var added = new ArrayList<ItemChange<E>>();
      int index = supportsInsertionIndex ? wrapped.size() : UNKNOWN_INDEX;

      while (itemsToAdd.hasNext()) {
         final E item = itemsToAdd.next();
         if ((filter == null || filter.test(item)) && wrapped.add(item)) {
            added.add(ItemChange.added(item, index));
            if (supportsInsertionIndex) {
               index++;
            }
         }
      }

      if (added.isEmpty())
         return 0;

      fire(Operation.ADD_ALL, added);
      return added.size();
   }

   @Override
   public void clear() {
      fire(Operation.CLEAR, clearInternal());
   }

   protected List<ItemChange<E>> clearInternal() {
      if (wrapped.isEmpty())
         return Collections.emptyList();

      final var removed = new ArrayList<ItemChange<E>>();
      int index = 0;
      for (final E item : wrapped) {
         removed.add(ItemChange.removed(item, index++));
      }

      wrapped.clear();
      return removed;
   }

   @Override
   public boolean contains(final @Nullable Object o) {
      return wrapped.contains(o);
   }

   @Override
   public boolean containsAll(final Collection<?> c) {
      return wrapped.containsAll(c);
   }

   /**
    * Compares this {@code ObservableCollection} to another object.
    * <p>
    * Returns {@code true} if:
    * <ul>
    * <li>The other object is the same instance.</li>
    * <li>The other object is an {@code ObservableCollection} with an equal underlying collection.</li>
    * <li>The other object is a {@link Collection} equal to the underlying collection.</li>
    * </ul>
    */
   @Override
   public boolean equals(final @Nullable Object o) {
      return this == o //
            || o instanceof final ObservableCollection<?, ?> other && wrapped.equals(other.wrapped) //
            || o instanceof final Collection<?> c && wrapped.equals(c);
   }

   protected void fire(final Operation action, final ItemChange<E> change) {
      events.fire(new ChangeEvent<>(this, action, Collections.singletonList(change)));
   }

   protected void fire(final Operation action, final List<ItemChange<E>> changes) {
      if (changes.isEmpty())
         return;
      events.fire(new ChangeEvent<>(this, action, changes));
   }

   /**
    * @return the hash code based on the wrapped collection
    */
   @Override
   public int hashCode() {
      return wrapped.hashCode();
   }

   @Override
   public boolean isEmpty() {
      return wrapped.isEmpty();
   }

   @Override
   public Iterator<E> iterator() {
      final Iterator<E> it = wrapped.iterator();
      return new Iterator<>() {
         int index = UNKNOWN_INDEX;

         @SuppressWarnings("null")
         E lastReturned;

         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         @Override
         public E next() {
            lastReturned = it.next();
            index++;
            return lastReturned;
         }

         @Override
         public void remove() {
            it.remove();
            fire(Operation.REMOVE, ItemChange.removed(lastReturned, index));
         }
      };
   }

   @Override
   public boolean remove(final @Nullable Object itemToRemove) {
      if (wrapped.isEmpty())
         return false;

      final boolean removed = wrapped.remove(itemToRemove);
      if (removed) {
         @SuppressWarnings("unchecked")
         final var removedItem = (E) itemToRemove;
         fire(Operation.REMOVE, ItemChange.removed(removedItem, UNKNOWN_INDEX));
      }
      return removed;
   }

   @Override
   public boolean removeAll(final Collection<?> itemsToRemove) {
      if (wrapped.isEmpty() || itemsToRemove.isEmpty())
         return false;

      final Set<?> toRemove = itemsToRemove instanceof Set //
            ? (Set<?>) itemsToRemove
            : new HashSet<>(itemsToRemove);

      return removeMatching(toRemove::contains, Operation.REMOVE_ALL);
   }

   public boolean removeAll(@SuppressWarnings("unchecked") final @NonNull E... itemsToRemove) {
      if (itemsToRemove.length == 0 || wrapped.isEmpty())
         return false;

      final Set<E> toRemove = itemsToRemove.length == 1 //
            ? Collections.singleton(itemsToRemove[0])
            : Set.of(itemsToRemove);

      return removeMatching(toRemove::contains, Operation.REMOVE_ALL);
   }

   @Override
   public boolean removeIf(final Predicate<? super E> filter) {
      return removeMatching(filter, Operation.REMOVE_IF);
   }

   protected boolean removeMatching(final Predicate<? super E> shouldRemove, final Operation action) {
      if (wrapped.isEmpty())
         return false;

      final var removed = new ArrayList<ItemChange<E>>();

      if (supportsRemovalIndex) {

         // 1) collect indices of items to remove
         int index = 0;
         for (final E item : wrapped) {
            if (shouldRemove.test(item)) {
               removed.add(ItemChange.removed(item, index));
            }
            index++;
         }

         if (removed.isEmpty())
            return false;

         // 2) remove items by matching indices
         final Iterator<E> it = wrapped.iterator();
         index = 0;
         int removeIndex = 0;
         while (it.hasNext() && removeIndex < removed.size()) {
            it.next();
            if (index == removed.get(removeIndex).index()) {
               it.remove();
               removeIndex++;
            }
            index++;
         }
      } else {
         final Iterator<E> it = wrapped.iterator();
         while (it.hasNext()) {
            final E item = it.next();
            if (shouldRemove.test(item)) {
               removed.add(ItemChange.removed(item, UNKNOWN_INDEX));
               it.remove();
            }
         }

         if (removed.isEmpty())
            return false;
      }

      fire(action, removed);
      return true;
   }

   @Override
   public boolean retainAll(final Collection<?> itemsToKeep) {
      if (wrapped.isEmpty())
         return false;

      if (itemsToKeep.isEmpty()) {
         fire(Operation.RETAIN_ALL, clearInternal());
         return true;
      }

      final Set<?> toKeep = itemsToKeep instanceof Set //
            ? (Set<?>) itemsToKeep
            : new HashSet<>(itemsToKeep);

      return removeMatching(e -> !toKeep.contains(e), Operation.RETAIN_ALL);
   }

   public boolean retainAll(@SuppressWarnings("unchecked") final @NonNull E... itemsToKeep) {
      if (wrapped.isEmpty())
         return false;

      if (itemsToKeep.length == 0) {
         fire(Operation.RETAIN_ALL, clearInternal());
         return true;
      }

      final Set<E> toKeep = itemsToKeep.length == 1 //
            ? Collections.singleton(itemsToKeep[0])
            : Set.of(itemsToKeep);

      return removeMatching(e -> !toKeep.contains(e), Operation.RETAIN_ALL);
   }

   @Override
   public int size() {
      return wrapped.size();
   }

   @Override
   public boolean subscribe(final EventListener<ChangeEvent<E>> listener) {
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
   public String toString() {
      return wrapped.toString();
   }

   @Override
   public boolean unsubscribe(final EventListener<ChangeEvent<E>> listener) {
      return events.unsubscribe(listener);
   }
}
