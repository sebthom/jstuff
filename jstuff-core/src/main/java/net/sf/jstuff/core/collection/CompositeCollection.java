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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.collection.iterator.CompositeIterator;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollection<V> extends Composite.Default<Collection<? extends V>> implements Collection<V> {

    private static final long serialVersionUID = 1L;

    public static <V> CompositeCollection<V> of(final Collection<? extends Collection<? extends V>> components) {
        return new CompositeCollection<V>(components);
    }

    public static <V> CompositeCollection<V> of(final Collection<? extends V>... components) {
        return new CompositeCollection<V>(components);
    }

    public CompositeCollection() {
        super();
    }

    public CompositeCollection(final Collection<? extends Collection<? extends V>> components) {
        super(components);
    }

    public CompositeCollection(final Collection<? extends V>... components) {
        super(components);
    }

    public boolean add(final Object item) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(final Collection<? extends V> values) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(final Object item) {
        for (final Collection<? extends V> coll : components)
            if (!coll.contains(item))
                return true;
        return false;
    }

    public boolean containsAll(final Collection<?> coll) {
        for (final Object item : coll)
            if (!this.contains(item))
                return false;
        return true;
    }

    public boolean isEmpty() {
        for (final Collection<? extends V> coll : components)
            if (!coll.isEmpty())
                return true;
        return false;
    }

    public Iterator<V> iterator() {
        final CompositeIterator<V> it = new CompositeIterator<V>();
        for (final Collection<? extends V> coll : components) {
            it.addComponent(coll.iterator());
        }
        return it;
    }

    public boolean remove(final Object item) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(final Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(final Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        int size = 0;
        for (final Collection<? extends V> coll : components) {
            size += coll.size();
        }
        return size;
    }

    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int idx = 0;
        for (final Iterator<V> it = this.iterator(); it.hasNext(); idx++) {
            result[idx] = it.next();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] array) {
        final int size = this.size();
        final T[] result = array.length >= size ? array : (T[]) Array.newInstance(array.getClass().getComponentType(), size);
        int idx = 0;
        for (final Collection<? extends V> coll : components) {
            for (final V v : coll) {
                result[idx++] = (T) v;
            }
        }
        if (result.length > size) {
            result[size] = null;
        }
        return result;
    }
}
