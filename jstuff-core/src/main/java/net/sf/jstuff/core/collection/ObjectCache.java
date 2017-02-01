/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory object cache.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class ObjectCache<K, V> {
    private static final class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V> {
        private final K key;

        private SoftValueReference(final K key, final V value, final ReferenceQueue<V> queue) {
            super(value, queue);
            this.key = key;
        }

        public K getKey() {
            return key;
        }
    }

    private static interface ValueReference<K, V> {
        K getKey();

        V get();
    }

    private static final class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V> {
        private final K key;

        private WeakValueReference(final K key, final V value, final ReferenceQueue<V> queue) {
            super(value, queue);
            this.key = key;
        }

        public K getKey() {
            return key;
        }
    }

    private final ConcurrentMap<K, ValueReference<K, V>> cache = new ConcurrentHashMap<K, ValueReference<K, V>>();
    private final ReferenceQueue<V> garbageCollectedRefs = new ReferenceQueue<V>();
    private final int maxObjectsToKeep;

    /**
     * most recently used list.
     * hard referencing the last n-th items to avoid their garbage collection.
     * the first item is the latest accessed item.
     */
    private final LinkedList<V> mru;
    private final boolean useWeakReferences;

    /**
     * Creates a new cache where all cached objects are soft-referenced (i.e. only garbage collected if JVM needs to free memory)
     */
    public ObjectCache() {
        this(-1, false);
    }

    public ObjectCache(final boolean useWeakValueReferences) {
        this(-1, useWeakValueReferences);
    }

    /**
     * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are
     *            subject to garbage collection
     */
    public ObjectCache(final int maxObjectsToKeep) {
        this(maxObjectsToKeep, false);
    }

    /**
     * @param maxObjectsToKeep the maximum number of cached objects that is guaranteed not to be garbage collected, a value lower 1 means all objects are
     *            subject to garbage collection
     */
    public ObjectCache(final int maxObjectsToKeep, final boolean useWeakValueReferences) {
        this.maxObjectsToKeep = maxObjectsToKeep;
        mru = maxObjectsToKeep > -1 ? new LinkedList<V>() : null;
        this.useWeakReferences = useWeakValueReferences;
    }

    public boolean contains(final K key) {
        expungeStaleEntries();
        final ValueReference<K, V> ref = cache.get(key);
        if (ref == null)
            return false;
        return ref.get() != null;
    }

    public void clear() {
        cache.clear();
    }

    @SuppressWarnings("unchecked")
    private void expungeStaleEntries() {
        ValueReference<K, V> ref;
        while ((ref = (ValueReference<K, V>) garbageCollectedRefs.poll()) != null) {
            cache.remove(ref.getKey(), ref);
        }
    }

    /**
     * @return a new instance map with all objects currently in the cache
     */
    public Map<K, V> getAll() {
        expungeStaleEntries();
        final Map<K, V> result = new HashMap<K, V>();
        for (final ValueReference<K, V> ref : cache.values()) {
            final V value = ref.get();
            if (value == null) {
                cache.remove(ref.getKey(), ref);
            } else {
                result.put(ref.getKey(), value);
            }
        }
        return result;
    }

    public V get(final K key) {
        expungeStaleEntries();
        final ValueReference<K, V> ref = cache.get(key);
        if (ref == null)
            return null;

        final V value = ref.get();

        if (value == null) {
            cache.remove(key, ref);
            return null;
        }

        // update MRU list
        if (maxObjectsToKeep > 0 && (mru.size() == 0 || value != mru.getFirst())) {
            mru.remove(value);
            mru.addFirst(value);
            if (mru.size() > maxObjectsToKeep) {
                mru.removeLast();
            }
        }
        return value;
    }

    public int getMaxObjectsToKeep() {
        return maxObjectsToKeep;
    }

    public void put(final K key, final V value) {
        cache.put(key, useWeakReferences ? //
                new WeakValueReference<K, V>(key, value, garbageCollectedRefs) : //
                new SoftValueReference<K, V>(key, value, garbageCollectedRefs) //
        );
    }

    public void remove(final K key) {
        cache.remove(key);
    }
}