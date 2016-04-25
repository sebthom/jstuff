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

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithMaps<K, K2, V> extends MapWith<K, Map<K2, V>> {
    private static final long serialVersionUID = 1L;

    public static <K, K2, V> MapWithMaps<K, K2, V> create() {
        return new MapWithMaps<K, K2, V>();
    }

    public MapWithMaps() {
        super();
    }

    public MapWithMaps(final int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected Map<K2, V> create(final K key) {
        return newHashMap();
    }

    @Override
    protected Map<K2, V> createNullSafe(final K key) {
        return Collections.emptyMap();
    }
}
