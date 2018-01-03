/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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

import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class CollectionUtils {

    /**
     * adds all items to the collection accepted by the filter
     *
     * @return number of items added
     * @throws IllegalArgumentException if <code>collection == null</code>
     */
    public static <T> int addAll(final Collection<T> collection, final Accept<T> filter, final T... items) {
        Args.notNull("collection", collection);
        Args.notNull("filter", filter);

        if (items == null)
            return 0;

        int count = 0;
        for (final T item : items)
            if (filter.accept(item) && collection.add(item)) {
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
    public static <T> boolean containsIdentical(final Collection<T> coll, final T theItem) {
        Args.notNull("coll", coll);

        for (final T t : coll)
            if (t == theItem)
                return true;
        return false;
    }

    /**
     * Returns a new list or set with all items accepted by the filter
     *
     * @throws IllegalArgumentException if <code>accept == null</code>
     */
    public static <T> Collection<T> filter(final Collection<T> collection, final Accept<T> accept) throws IllegalArgumentException {
        if (collection == null)
            return null;

        Args.notNull("accept", accept);

        final Collection<T> result = collection instanceof Set ? new HashSet<T>() : new ArrayList<T>();
        for (final T item : collection)
            if (accept.accept(item)) {
                result.add(item);
            }
        return result;
    }

    /**
     * Returns a new list with all items accepted by the filter
     *
     * @throws IllegalArgumentException if <code>accept == null</code>
     */
    public static <T> List<T> filter(final List<T> collection, final Accept<T> accept) throws IllegalArgumentException {
        if (collection == null)
            return null;

        Args.notNull("accept", accept);

        final List<T> result = new ArrayList<T>();
        for (final T item : collection)
            if (accept.accept(item)) {
                result.add(item);
            }
        return result;
    }

    /**
     * Returns a new set with all items accepted by the filter
     *
     * @throws IllegalArgumentException if <code>accept == null</code>
     */
    public static <T> Set<T> filter(final Set<T> collection, final Accept<T> accept) throws IllegalArgumentException {
        if (collection == null)
            return null;

        Args.notNull("accept", accept);

        final Set<T> result = new HashSet<T>();
        for (final T item : collection)
            if (accept.accept(item)) {
                result.add(item);
            }
        return result;
    }

    /**
     * removes all items not accepted by the filter
     *
     * @param collection
     * @param accept
     * @return number of items removed
     * @throws IllegalArgumentException if <code>accept == null</code>
     */
    public static <T> int filterInPlace(final Collection<T> collection, final Accept<T> accept) throws IllegalArgumentException {
        if (collection == null)
            return 0;

        Args.notNull("accept", accept);

        int count = 0;
        for (final Iterator<T> it = collection.iterator(); it.hasNext();) {
            final T item = it.next();
            if (!accept.accept(item)) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    /**
     * @param n first n elements to return
     * @return a new list with the first n elements of the input list
     */
    public static <T> List<T> head(final List<T> list, final int n) {
        Args.notNull("list", list);

        if (n < 1)
            return Collections.emptyList();

        final List<T> result = new ArrayList<T>(n > list.size() ? list.size() : n);

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
    public static <T> List<T> intersect(final List<T>... lists) {
        if (lists == null)
            return Collections.emptyList();

        for (final List<T> list : lists) {
            if (list == null || list.size() == 0)
                return Collections.emptyList();
        }

        final List<T> commonItems = new ArrayList<T>();

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
    public static <T> Set<T> intersect(final Set<T>... sets) {
        if (sets == null)
            return Collections.emptySet();

        for (final Set<T> set : sets) {
            if (set == null || set.size() == 0)
                return Collections.emptySet();
        }

        final Set<T> commonItems = new LinkedHashSet<T>();

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

    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static <K> ArrayList<K> newArrayList() {
        return new ArrayList<K>();
    }

    public static <K> ArrayList<K> newArrayList(final Collection<K> coll) {
        return coll == null ? new ArrayList<K>() : new ArrayList<K>(coll);
    }

    public static <K> ArrayList<K> newArrayList(final int initialSize) {
        return new ArrayList<K>(initialSize);
    }

    public static <K> ArrayList<K> newArrayList(final K... values) {
        if (values == null || values.length == 0)
            return new ArrayList<K>();

        final ArrayList<K> l = new ArrayList<K>(values.length);
        // faster than Collections.addAll(result, array);
        for (final K v : values) {
            l.add(v);
        }
        return l;
    }

    public static <K> HashSet<K> newHashSet() {
        return new HashSet<K>();
    }

    public static <K> HashSet<K> newHashSet(final Collection<K> initialValues) {
        return initialValues == null ? new HashSet<K>() : new HashSet<K>(initialValues);
    }

    public static <K> HashSet<K> newHashSet(final int initialSize) {
        return new HashSet<K>(initialSize);
    }

    public static <K> HashSet<K> newHashSet(final K... values) {
        if (values == null || values.length == 0)
            return new HashSet<K>();

        final HashSet<K> s = new HashSet<K>(values.length);
        for (final K v : values) {
            s.add(v);
        }
        return s;
    }

    public static <V> LinkedHashSet<V> newLinkedHashSet() {
        return new LinkedHashSet<V>();
    }

    public static <K> HashSet<K> newLinkedHashSet(final int initialSize) {
        return new LinkedHashSet<K>(initialSize);
    }

    public static <K> HashSet<K> newLinkedHashSet(final K... values) {
        if (values == null || values.length == 0)
            return new LinkedHashSet<K>();

        final HashSet<K> s = new LinkedHashSet<K>(values.length);
        for (final K v : values) {
            s.add(v);
        }
        return s;
    }

    public static <T> ThreadLocal<ArrayList<T>> newThreadLocalArrayList() {
        return new ThreadLocal<ArrayList<T>>() {
            @Override
            public ArrayList<T> initialValue() {
                return new ArrayList<T>();
            }
        };
    }

    public static <T> ThreadLocal<HashSet<T>> newThreadLocalHashSet() {
        return new ThreadLocal<HashSet<T>>() {
            @Override
            public HashSet<T> initialValue() {
                return new HashSet<T>();
            }
        };
    }

    public static <T> ThreadLocal<IdentityHashSet<T>> newThreadLocalIdentitySet() {
        return new ThreadLocal<IdentityHashSet<T>>() {
            @Override
            public IdentityHashSet<T> initialValue() {
                return new IdentityHashSet<T>();
            }
        };
    }

    public static <T> ThreadLocal<LinkedList<T>> newThreadLocalLinkedList() {
        return new ThreadLocal<LinkedList<T>>() {
            @Override
            public LinkedList<T> initialValue() {
                return new LinkedList<T>();
            }
        };
    }

    public static <T> ThreadLocal<WeakHashSet<T>> newThreadLocalWeakHashSet() {
        return new ThreadLocal<WeakHashSet<T>>() {
            @Override
            public WeakHashSet<T> initialValue() {
                return new WeakHashSet<T>();
            }
        };
    }

    public static <T> T remove(final Collection<T> coll, final int index) {
        Args.notNull("coll", coll);
        if (coll instanceof List)
            return ((List<T>) coll).remove(index);
        if (index >= coll.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + coll.size());

        int i = 0;
        for (final Iterator<T> it = coll.iterator(); it.hasNext();) {
            final T item = it.next();
            if (i == index) {
                it.remove();
                return item;
            }
            i++;
        }
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + coll.size());
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
        final List<T> result = new ArrayList<T>(n > listSize ? listSize : n);
        final int fromIndex = n > listSize ? 0 : listSize - n;
        final int toIndex = listSize - 1;

        for (int i = fromIndex; i <= toIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }

    public static <T> Iterable<T> toIterable(final Iterator<T> it) {
        Args.notNull("it", it);

        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return it;
            }
        };
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

    public static <S, T> Set<T> transform(final Set<S> source, final Function<? super S, ? extends T> op) {
        if (source == null)
            return null;

        final Set<T> target = newHashSet(source.size());
        for (final S sourceItem : source) {
            target.add(op.apply(sourceItem));
        }
        return target;
    }
}
