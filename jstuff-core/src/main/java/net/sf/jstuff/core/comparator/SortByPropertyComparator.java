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
package net.sf.jstuff.core.comparator;

import java.util.Comparator;
import java.util.Map;

import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortByPropertyComparator<T> extends SortByComparator<T, String> {
    private final Map<String, Comparator<T>> comparators = Maps.newHashMap();

    public SortByPropertyComparator(final SortBy<String>... sortBy) {
        super(sortBy);
    }

    @Override
    protected Comparator<T> getComparator(final String sortKey) {
        Comparator<T> comp = comparators.get(sortKey);
        if (comp == null) {
            comp = new PropertyComparator<T>(sortKey);
            comparators.put(sortKey, comp);
        }
        return comparators.get(sortKey);
    }
}
