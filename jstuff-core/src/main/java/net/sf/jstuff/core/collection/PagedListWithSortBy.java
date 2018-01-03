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

import java.util.List;

import net.sf.jstuff.core.comparator.SortBy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PagedListWithSortBy<E, SortKeyType> extends PagedList<E> {
    private static final long serialVersionUID = 1L;

    private SortBy<SortKeyType>[] sortedBy;

    public PagedListWithSortBy() {
        super();
    }

    public PagedListWithSortBy(final Class<E> elementType) {
        super(elementType);
    }

    public PagedListWithSortBy(final Class<E> elementType, final List<E> elements, final int start, final int totalCount) {
        super(elementType, elements, start, totalCount);
    }

    public SortBy<SortKeyType>[] getSortedBy() {
        return sortedBy;
    }

    public void setSortedBy(final SortBy<SortKeyType>[] sortedBy) {
        this.sortedBy = sortedBy;
    }
}
