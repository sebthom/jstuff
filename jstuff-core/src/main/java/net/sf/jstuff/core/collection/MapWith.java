/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

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

    public void clear() {
        map.clear();
    }

    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    protected abstract V create(final K key);

    protected Map<K, V> createBackingMap(final int initialCapacity) {
        return CollectionUtils.newHashMap(initialCapacity);
    }

    protected V createNullSafe(final K key) {
        return create(key);
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    public V get(final Object key) {
        return map.get(key);
    }

    public final V getNullSafe(final K key) {
        if (containsKey(key))
            return get(key);
        return createNullSafe(key);
    }

    public final V getOrCreate(final K key) {
        if (containsKey(key))
            return get(key);
        final V value = create(key);
        put(key, value);
        return value;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public V put(final K key, final V value) {
        return map.put(key, value);
    }

    public void putAll(final Map<? extends K, ? extends V> otherMap) {
        map.putAll(otherMap);
    }

    /**
     * @since 1.8
     */
    public V putIfAbsent(final K key, final V value) {
        if (containsKey(value))
            return map.put(key, value);
        return map.get(key);
    }

    public V remove(final Object key) {
        return map.remove(key);
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified value.
     *
     * @since 1.8
     */
    public boolean remove(final Object key, final Object value) {
        if (!containsKey(key))
            return false;

        if (ObjectUtils.equals(get(key), value)) {
            remove(key);
            return true;
        }
        return false;
    }

    public int size() {
        return map.size();
    }

    public Collection<V> values() {
        return map.values();
    }
}
