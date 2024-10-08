/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class CollectionUtils {

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static int addAll(final Collection<Byte> collection, final byte @Nullable... items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final byte item : items)
         if (collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static int addAll(final Collection<Integer> collection, final int @Nullable... items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final int item : items)
         if (collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static int addAll(final Collection<Long> collection, final long @Nullable... items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final long item : items)
         if (collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static <T> int addAll(final Collection<T> collection, final @Nullable Iterable<T> items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final T item : items)
         if (collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static <T> int addAll(final Collection<T> collection, final @Nullable Iterator<T> items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      while (items.hasNext()) {
         if (collection.add(items.next())) {
            count++;
         }
      }
      return count;
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static <T> int addAll(final Collection<T> collection, final Predicate<T> filter, final @Nullable Iterable<T> items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final T item : items)
         if (filter.test(item) && collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   public static <T> int addAll(final Collection<T> collection, final Predicate<T> filter, final @Nullable Iterator<T> items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      while (items.hasNext()) {
         final var item = items.next();
         if (filter.test(item) && collection.add(item)) {
            count++;
         }
      }
      return count;
   }

   /**
    * Adds all items to the collection accepted by the filter
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   @SafeVarargs
   public static <T> int addAll(final Collection<T> collection, final Predicate<T> filter, final T @Nullable... items) {
      Args.notNull("collection", collection);
      Args.notNull("filter", filter);

      if (items == null)
         return 0;

      int count = 0;
      for (final T item : items)
         if (filter.test(item) && collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * Adds all items to the collection
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   @SafeVarargs
   public static <T> int addAll(final Collection<T> collection, final T @Nullable... items) {
      Args.notNull("collection", collection);

      if (items == null)
         return 0;

      int count = 0;
      for (final T item : items)
         if (collection.add(item)) {
            count++;
         }
      return count;
   }

   /**
    * @return true if the given item is contained in the collection based on identity comparison
    */
   public static <T> boolean containsIdentical(final @Nullable Collection<T> collection, final T searchFor) {
      if (collection == null)
         return false;

      for (final T t : collection)
         if (t == searchFor)
            return true;
      return false;
   }

   /**
    * Extends the given list with null elements to ensure the given minimum size.
    */
   public static <@Nullable T> void ensureSize(final List<T> list, final int minimumSize) {
      final int newElements = minimumSize - list.size();
      if (newElements <= 0)
         return;

      if (list instanceof final ArrayList<?> arrayList) {
         arrayList.ensureCapacity(minimumSize);
      }

      for (int i = 0; i < newElements; i++) {
         list.add(null);
      }
   }

   /**
    * Returns a new list or set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> Collection<T> filter(final Iterable<T> iterable, final Predicate<T> filter) {
      return asNonNullUnsafe(filterNullable(iterable, filter));
   }

   /**
    * Returns a new list with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> Collection<T> filter(final List<T> list, final Predicate<T> filter) {
      return asNonNullUnsafe(filterNullable(list, filter));
   }

   /**
    * Returns a new set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> Collection<T> filter(final @Nullable Set<T> set, final Predicate<T> filter) {
      return asNonNullUnsafe(filterNullable(set, filter));
   }

   /**
    * removes all items not accepted by the filter
    *
    * @return number of items removed
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> int filterInPlace(final @Nullable Collection<T> collection, final Predicate<T> filter) {
      if (collection == null)
         return 0;

      Args.notNull("filter", filter);

      int count = 0;
      for (final Iterator<T> it = collection.iterator(); it.hasNext();) {
         final T item = it.next();
         if (!filter.test(item)) {
            it.remove();
            count++;
         }
      }
      return count;
   }

   /**
    * Returns a new list or set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> @Nullable Collection<T> filterNullable(final @Nullable Iterable<T> iterable, final Predicate<T> filter) {
      if (iterable == null)
         return null;

      Args.notNull("filter", filter);

      final Collection<T> result = iterable instanceof Set //
            ? iterable instanceof LinkedHashSet ? new LinkedHashSet<>() : new HashSet<>() //
            : new ArrayList<>();
      for (final T item : iterable)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   /**
    * Returns a new list with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> @Nullable List<T> filterNullable(final @Nullable List<T> list, final Predicate<T> filter) {
      if (list == null)
         return null;
      if (list.isEmpty())
         return Collections.emptyList();

      Args.notNull("filter", filter);

      final var result = new ArrayList<T>();
      for (final T item : list)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   /**
    * Returns a new set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>filter == null</code>
    */
   public static <T> @Nullable Set<T> filterNullable(final @Nullable Set<T> set, final Predicate<T> filter) {
      if (set == null)
         return null;
      if (set.isEmpty())
         return Collections.emptySet();

      Args.notNull("filter", filter);

      final Set<T> result = set instanceof LinkedHashSet ? new LinkedHashSet<>() : new HashSet<>();
      for (final T item : set)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   public static <T> @Nullable T findFirst(final @Nullable Collection<T> coll) {
      if (coll == null || coll.isEmpty())
         return null;

      if (coll instanceof final List<T> list)
         return list.get(0);

      if (coll instanceof final Queue<T> queue)
         return queue.peek();

      final var it = coll.iterator();
      if (it.hasNext())
         return it.next();
      return null;
   }

   public static <T> @Nullable T findFirst(final @Nullable List<T> list) {
      if (list == null || list.isEmpty())
         return null;

      return list.get(0);
   }

   public static <T> @Nullable T findFirstMatching(final @Nullable Collection<T> coll, final Predicate<T> filter) {
      if (coll == null || coll.isEmpty())
         return null;

      for (final T e : coll) {
         if (filter.test(e))
            return e;
      }
      return null;
   }

   /**
    * @return the last element or null if list is empty
    */
   public static <T> @Nullable T findLast(final List<T> list) {
      if (list.isEmpty())
         return null;
      return getLast(list);
   }

   /**
    * Gets the n-th element of the list.
    *
    * @param index a negative index selects an element from the end of the list
    *
    * @throws IllegalArgumentException if list is null
    * @throws IndexOutOfBoundsException if index is out of range
    */
   public static <T> T getAt(final List<T> list, final int index) {
      Args.notNull("list", list);
      if (index < 0)
         return list.get(list.size() + index);
      return list.get(index);
   }

   /**
    * @param list a non-empty list with non-nullable elements
    * @throws IllegalArgumentException if list is null
    * @throws IndexOutOfBoundsException if list is empty
    */
   public static <T> T getLast(final List<T> list) {
      Args.notNull("list", list);
      return list.get(list.size() - 1);
   }

   /**
    * @return the last element or null if list is null or empty
    */
   public static <T> @Nullable T getLastOrNull(final @Nullable List<T> list) {
      if (list == null || list.isEmpty())
         return null;
      return list.get(list.size() - 1);
   }

   /**
    * @param n first n elements to return
    * @return a new list with the first n elements of the input list
    */
   public static <T> List<T> head(final List<T> list, final int n) {
      return asNonNullUnsafe(headNullable(list, n));
   }

   /**
    * @param n first n elements to return
    * @return a new list with the first n elements of the input list
    */
   public static <T> @Nullable List<T> headNullable(final @Nullable List<T> list, final int n) {
      if (list == null)
         return null;

      if (n < 1)
         return Collections.emptyList();

      final var result = new ArrayList<T>(n > list.size() ? list.size() : n);

      int counter = 1;
      for (final T item : list) {
         result.add(item);
         if (counter == n) {
            break;
         }
         counter++;
      }
      return result;
   }

   /**
    * @return all items that are contained in all lists.
    */
   @SafeVarargs
   public static <T> List<T> intersect(final List<T> @Nullable... lists) {
      if (lists == null || lists.length == 0)
         return Collections.emptyList();

      for (final List<T> list : lists) {
         if (list == null || list.isEmpty())
            return Collections.emptyList();
      }

      final var commonItems = new ArrayList<T>();

      for (final T candidate : lists[0]) {
         boolean isCommon = true;
         for (int i = 1; i < lists.length; i++) {

            if (!lists[i].contains(candidate)) {
               isCommon = false;
               break;
            }
         }
         if (isCommon) {
            commonItems.add(candidate);
         }
      }
      return commonItems;
   }

   /**
    * @return all items that are contained in all sets.
    */
   @SafeVarargs
   public static <T> Set<T> intersect(final Set<T> @Nullable... sets) {
      if (sets == null || sets.length == 0)
         return Collections.emptySet();

      for (final Set<T> set : sets) {
         if (set == null || set.isEmpty())
            return Collections.emptySet();
      }

      final var commonItems = new LinkedHashSet<T>();

      for (final T candidate : sets[0]) {
         boolean isCommon = true;
         for (int i = 1; i < sets.length; i++) {

            if (!sets[i].contains(candidate)) {
               isCommon = false;
               break;
            }
         }
         if (isCommon) {
            commonItems.add(candidate);
         }
      }
      return commonItems;
   }

   public static boolean isEmpty(final @Nullable Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static boolean isNotEmpty(final @Nullable Collection<?> collection) {
      return collection != null && !collection.isEmpty();
   }

   public static <T> ArrayList<T> newArrayList() {
      return new ArrayList<>();
   }

   public static <T> ArrayList<T> newArrayList(final @Nullable Collection<T> initialValues) {
      return initialValues == null ? new ArrayList<>() : new ArrayList<>(initialValues);
   }

   public static <T> ArrayList<T> newArrayList(final int initialSize) {
      return new ArrayList<>(initialSize);
   }

   public static <T> ArrayList<T> newArrayList(final T initialValue) {
      final var list = new ArrayList<T>();
      list.add(initialValue);
      return list;
   }

   @SafeVarargs
   public static <T> ArrayList<T> newArrayList(final T @Nullable... initialValues) {
      if (initialValues == null || initialValues.length == 0)
         return new ArrayList<>();

      final var l = new ArrayList<T>(initialValues.length);
      Collections.addAll(l, initialValues);
      return l;
   }

   public static <T> ThreadLocal<ArrayList<T>> newThreadLocalArrayList() {
      return ThreadLocal.withInitial(ArrayList::new);
   }

   public static <T> ThreadLocal<HashSet<T>> newThreadLocalHashSet() {
      return ThreadLocal.withInitial(HashSet::new);
   }

   public static <T> ThreadLocal<IdentityHashSet<T>> newThreadLocalIdentitySet() {
      return ThreadLocal.withInitial(IdentityHashSet::new);
   }

   public static <T> ThreadLocal<LinkedList<T>> newThreadLocalLinkedList() {
      return ThreadLocal.withInitial(LinkedList::new);
   }

   public static <T> ThreadLocal<WeakHashSet<T>> newThreadLocalWeakHashSet() {
      return ThreadLocal.withInitial(WeakHashSet::new);
   }

   /**
    * @throws IndexOutOfBoundsException if index >= collection.size()
    */
   public static <T> T remove(final Collection<T> collection, final int index) {
      Args.notNull("collection", collection);

      if (collection instanceof final List<T> list)
         return list.remove(index);
      if (index >= collection.size())
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + collection.size());

      int i = 0;
      for (final Iterator<T> it = collection.iterator(); it.hasNext();) {
         final T item = it.next(); // CHECKSTYLE:IGNORE MoveVariableInsideIfCheck
         if (i == index) {
            it.remove();
            return item;
         }
         i++;
      }
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + collection.size());
   }

   /**
    * Removes the last element in this list.
    *
    * @return the element previously at the specified position
    *
    * @throws IndexOutOfBoundsException if the list is empty
    * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this list
    */
   public static <T> T removeLast(final List<T> list) {
      return list.remove(list.size() - 1);
   }

   /**
    * Removes the last element in this list.
    *
    * @return the element previously at the specified position or null if the list is empty
    *
    * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this list
    */
   public static <T> @Nullable T removeLastNullable(final @Nullable List<T> list) {
      if (list == null || list.isEmpty())
         return null;
      return list.remove(list.size() - 1);
   }

   public static <T> List<T> reverse(final List<T> list) {
      return asNonNullUnsafe(reverseNullable(list));
   }

   public static <T> @Nullable List<T> reverseNullable(final @Nullable List<T> list) {
      if (list == null)
         return null;
      if (list.isEmpty())
         return Collections.emptyList();

      final var reversed = new ArrayList<>(list);
      Collections.reverse(reversed);
      return reversed;
   }

   /**
    * Sets the value at the given index. If the index is if the index is out of range (<code>index >= list.size()</code>) the list is
    * extended by <code>null</code> elements up to the index position.
    */
   public static <@Nullable T> void setAt(final List<T> list, final int index, final T value) {
      ensureSize(list, index);
      list.set(index, value);
   }

   /**
    * @param n last n elements to return
    * @return a new list with the last n elements of the input list
    */
   public static <T> List<T> tail(final List<T> list, final int n) {
      return asNonNullUnsafe(tailNullable(list, n));
   }

   /**
    * @param n last n elements to return
    * @return a new list with the last n elements of the input list
    */
   public static <T> @Nullable List<T> tailNullable(final @Nullable List<T> list, final int n) {
      if (list == null)
         return null;

      if (n < 1)
         return Collections.emptyList();

      final int listSize = list.size();
      final var result = new ArrayList<T>(n > listSize ? listSize : n);
      final int fromIndex = n > listSize ? 0 : listSize - n;
      final int toIndex = listSize - 1;

      for (int i = fromIndex; i <= toIndex; i++) {
         result.add(list.get(i));
      }
      return result;
   }

   public static <T> Iterable<T> toIterable(final Iterator<T> it) {
      Args.notNull("it", it);

      return () -> it;
   }

   public static <T> List<T> toList(final Iterable<T> it) {
      Args.notNull("it", it);

      final var result = new ArrayList<T>();
      addAll(result, it);
      return result;
   }

   public static <T> List<T> toList(final Iterator<T> it) {
      Args.notNull("it", it);

      final var result = new ArrayList<T>();
      addAll(result, it);
      return result;
   }

   public static String toStringWithIndex(final List<?> list) {
      if (list.isEmpty())
         return "[]";

      final var sb = new StringBuilder("[");
      for (int i = 0, l = list.size(); i < l; i++) {
         final var e = list.get(i);
         sb.append(i).append(':').append(e == list ? "(this List)" : e);
         if (i == l - 1) {
            break;
         }
         sb.append(", ");
      }
      return sb.append(']').toString();
   }

   public static <S, T> List<T> transform(final List<S> source, final Function<? super S, ? extends T> op) {
      return asNonNullUnsafe(transformNullable(source, op));
   }

   public static <S, T> @Nullable List<T> transformNullable(final @Nullable List<S> source, final Function<? super S, ? extends T> op) {
      if (source == null)
         return null;
      Args.notNull("op", op);

      final List<T> target = newArrayList(source.size());
      for (final S sourceItem : source) {
         target.add(op.apply(sourceItem));
      }
      return target;
   }
}
