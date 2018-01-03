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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithQueues<K, V> extends MapWithCollections<K, V, Queue<V>> {
    private static final long serialVersionUID = 1L;

    public static <K, V> MapWithQueues<K, V> create() {
        return new MapWithQueues<K, V>();
    }

    public MapWithQueues() {
        super();
    }

    @Override
    protected Queue<V> create(final K key) {
        return new ConcurrentLinkedQueue<V>();
    }

    @Override
    protected Queue<V> createNullSafe(final K key) {
        return EmptyQueue.get();
    }
}
