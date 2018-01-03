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
package net.sf.jstuff.core.comparator;

import java.util.Comparator;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SortByComparator<T, SortKeyType> implements Comparator<T> {
    private SortBy<SortKeyType>[] sortBy;

    public SortByComparator(final SortBy<SortKeyType>... sortBy) {
        setSortBy(sortBy);
    }

    public int compare(final T o1, final T o2) {
        for (final SortBy<SortKeyType> sb : sortBy) {
            final int i = compareByKey(o1, o2, sb.getKey());
            if (i == 0)
                continue;
            return sb.getDirection() == SortDirection.ASC ? i : -i;
        }
        return 0;
    }

    public void setSortBy(final SortBy<SortKeyType>[] sortBy) {
        Args.notNull("sortBy", sortBy);
        this.sortBy = sortBy;
    }

    protected int compareByKey(final T o1, final T o2, final SortKeyType sortKey) {
        return getComparator(sortKey).compare(o1, o2);
    }

    protected abstract Comparator<T> getComparator(final SortKeyType sortKey);
}
