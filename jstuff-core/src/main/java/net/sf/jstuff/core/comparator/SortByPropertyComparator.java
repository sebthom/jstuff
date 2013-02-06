/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.core.comparator;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Comparator;
import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortByPropertyComparator<T> extends SortByComparator<T, String>
{
	private final Map<String, Comparator<T>> comparators = newHashMap();

	public SortByPropertyComparator(final SortBy<String>... sortBy)
	{
		super(sortBy);
	}

	@Override
	protected Comparator<T> getComparator(final String sortKey)
	{
		Comparator<T> comp = comparators.get(sortKey);
		if (comp == null)
		{
			comp = new PropertyComparator<T>(sortKey);
			comparators.put(sortKey, comp);
		}
		return comparators.get(sortKey);
	}
}
