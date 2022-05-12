/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class CollectionUtils {

   public static int addAll(final Collection<Byte> collection, final byte... items) throws IllegalArgumentException {
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

   public static int addAll(final Collection<Integer> collection, final int... items) throws IllegalArgumentException {
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

   public static int addAll(final Collection<Long> collection, final long... items) throws IllegalArgumentException {
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
    * adds all items to the collection accepted by the filter
    *
    * @return number of items added
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   @SafeVarargs
   public static <T> int addAll(final Collection<T> collection, final Predicate<T> filter, final T... items) {
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
   public static <T> int addAll(final Collection<T> collection, final T... items) throws IllegalArgumentException {
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
    * Returns true if the given item is contained in the collection based on identity comparison
    */
   public static <T> boolean containsIdentical(final Collection<T> collection, final T theItem) {
      if (collection == null)
         return false;

      for (final T t : collection)
         if (t == theItem)
            return true;
      return false;
   }

   /**
    * Returns a new list or set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>accept == null</code>
    */
   public static <T> Collection<T> filter(final Iterable<T> iterable, final Predicate<T> filter) throws IllegalArgumentException {
      if (iterable == null)
         return null;

      Args.notNull("filter", filter);

      final Collection<T> result = iterable instanceof Set ? new HashSet<>() : new ArrayList<>();
      for (final T item : iterable)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   /**
    * Returns a new list with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>accept == null</code>
    */
   public static <T> List<T> filter(final List<T> list, final Predicate<T> filter) throws IllegalArgumentException {
      if (list == null)
         return null;
      if (list.isEmpty())
         return Collections.emptyList();

      Args.notNull("filter", filter);

      final List<T> result = new ArrayList<>();
      for (final T item : list)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   /**
    * Returns a new set with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>accept == null</code>
    */
   public static <T> Set<T> filter(final Set<T> set, final Predicate<T> filter) throws IllegalArgumentException {
      if (set == null)
         return null;
      if (set.isEmpty())
         return Collections.emptySet();

      Args.notNull("filter", filter);

      final Set<T> result = new HashSet<>();
      for (final T item : set)
         if (filter.test(item)) {
            result.add(item);
         }
      return result;
   }

   /**
    * removes all items not accepted by the filter
    *
    * @return number of items removed
    * @throws IllegalArgumentException if <code>accept == null</code>
    */
   public static <T> int filterInPlace(final Collection<T> collection, final Predicate<T> filter) throws IllegalArgumentException {
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
   public static <T> T getLastOrNull(final List<T> list) {
      if (list == null || list.isEmpty())
         return null;
      return list.get(list.size() - 1);
   }

   /**
    * @param n first n elements to return
    * @return a new list with the first n elements of the input list
    */
   public static <T> List<T> head(final List<T> list, final int n) {
      if (list == null)
         return null;

      if (n < 1)
         return Collections.emptyList();

      final List<T> result = new ArrayList<>(n > list.size() ? list.size() : n);

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
   public static <T> List<T> intersect(final List<T>... lists) {
      if (lists == null || lists.length == 0)
         return Collections.emptyList();

      for (final List<T> list : lists) {
         if (list == null || list.isEmpty())
            return Collections.emptyList();
      }

      final List<T> commonItems = new ArrayList<>();

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
   public static <T> Set<T> intersect(final Set<T>... sets) {
      if (sets == null || sets.length == 0)
         return Collections.emptySet();

      for (final Set<T> set : sets) {
         if (set == null || set.isEmpty())
            return Collections.emptySet();
      }

      final Set<T> commonItems = new LinkedHashSet<>();

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

   public static boolean isEmpty(final Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static boolean isNotEmpty(final Collection<?> collection) {
      return collection != null && !collection.isEmpty();
   }

   public static <K> ArrayList<K> newArrayList() {
      return new ArrayList<>();
   }

   public static <K> ArrayList<K> newArrayList(final Collection<K> collection) {
      return collection == null ? new ArrayList<>() : new ArrayList<>(collection);
   }

   public static <K> ArrayList<K> newArrayList(final int initialSize) {
      return new ArrayList<>(initialSize);
   }

   @SafeVarargs
   public static <K> ArrayList<K> newArrayList(final K... values) {
      if (values == null || values.length == 0)
         return new ArrayList<>();

      final ArrayList<K> l = new ArrayList<>(values.length);
      Collections.addAll(l, values);
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

   public static <T> T remove(final Collection<T> collection, final int index) {
      Args.notNull("collection", collection);

      if (collection instanceof List)
         return ((List<T>) collection).remove(index);
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
    * @return the element previously at the specified position or null if the list is empty
    *
    * @throws UnsupportedOperationException if the {@code remove} operation is not supported by this list
    */
   public static <T> T removeLast(final List<T> list) {
      Args.notNull("list", list);

      if (list.isEmpty())
         return null;
      return list.remove(list.size() - 1);
   }

   public static <T> List<T> reverse(final List<T> list) {
      if (list == null)
         return null;
      if (list.isEmpty())
         return Collections.emptyList();

      final List<T> reversed = new ArrayList<>(list);
      Collections.reverse(reversed);
      return reversed;
   }

   /**
    * @param n last n elements to return
    * @return a new list with the last n elements of the input list
    */
   public static <T> List<T> tail(final List<T> list, final int n) {
      Args.notNull("list", list);

      if (n < 1)
         return Collections.emptyList();

      final int listSize = list.size();
      final List<T> result = new ArrayList<>(n > listSize ? listSize : n);
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

   public static <T> List<T> toList(final Iterator<T> it) {
      Args.notNull("it", it);

      final List<T> result = newArrayList();
      while (it.hasNext()) {
         result.add(it.next());
      }
      return result;
   }

   public static <S, T> List<T> transform(final List<S> source, final Function<? super S, ? extends T> op) {
      if (source == null)
         return null;

      final List<T> target = newArrayList(source.size());
      for (final S sourceItem : source) {
         target.add(op.apply(sourceItem));
      }
      return target;
   }
}
