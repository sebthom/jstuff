/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MapWith<K, V> implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<K, V> map;

    public MapWith() {
        map = createBackingMap(16);
    }

    public MapWith(final int initialCapacity) {
        map = createBackingMap(initialCapacity);
    }

    protected abstract V create(final K key);

    protected V createNullSafe(final K key) {
        return create(key);
    }

    protected Map<K, V> createBackingMap(final int initialCapacity) {
        return newHashMap(initialCapacity);
    }

    public final V getOrCreate(final K key) {
        if (containsKey(key))
            return get(key);
        final V value = create(key);
        put(key, value);
        return value;
    }

    public final V getNullSafe(final K key) {
        if (containsKey(key))
            return get(key);
        return createNullSafe(key);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    public V get(final Object key) {
        return map.get(key);
    }

    public V put(final K key, final V value) {
        return map.put(key, value);
    }

    public V remove(final Object key) {
        return map.remove(key);
    }

    public void putAll(final Map<? extends K, ? extends V> t) {
        map.putAll(t);
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
