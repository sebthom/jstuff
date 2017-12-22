/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapBackedSet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static <E> MapBackedSet<E> create(final Map<E, Boolean> emptyMap) {
        return new MapBackedSet<E>(emptyMap);
    }

    private final Map<E, Boolean> map;

    private transient Set<E> keys;

    public MapBackedSet(final Map<E, Boolean> emptyMap) {
        Args.notNull("map", emptyMap);
        if (!emptyMap.isEmpty())
            throw new IllegalArgumentException("Argument [map] is not an empty map");

        this.map = emptyMap;
        keys = emptyMap.keySet();
    }

    @Override
    public boolean add(final E e) {
        return map.put(e, Boolean.TRUE) == null;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return keys.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Set))
            return false;

        final Set<?> otherSet = (Set<?>) o;
        if (otherSet.size() != size())
            return false;

        for (final E key : keys)
            if (!otherSet.contains(key))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return keys.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return keys.iterator();
    }

    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        keys = map.keySet();
    }

    @Override
    public boolean remove(final Object o) {
        return map.remove(o) != null;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return keys.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return keys.retainAll(c);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Object[] toArray() {
        return keys.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return keys.toArray(a);
    }

    @Override
    public String toString() {
        return keys.toString();
    }
}
