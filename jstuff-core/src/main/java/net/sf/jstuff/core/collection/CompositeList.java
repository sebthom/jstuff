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
package net.sf.jstuff.core.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Unmodifiable composite list
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeList<V> extends AbstractList<V>
{
	private final ArrayList<List< ? extends V>> lists = new ArrayList<List< ? extends V>>();

	public CompositeList()
	{
		super();
	}

	public CompositeList(final List< ? extends V>... lists)
	{
		CollectionUtils.addAll(this.lists, lists);
	}

	public CompositeList<V> addComposite(final List< ? extends V> list)
	{
		this.lists.add(list);
		return this;
	}

	@Override
	public V get(final int index)
	{
		int totalSizeOfCheckedLists = 0;
		for (final List< ? extends V> list : lists)
		{
			final int currentListIndex = index - totalSizeOfCheckedLists;
			final int currentListSize = list.size();
			if (currentListIndex >= currentListSize)
			{
				totalSizeOfCheckedLists += currentListSize;
				continue;
			}
			return list.get(currentListIndex);
		}
		throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSizeOfCheckedLists);
	}

	@Override
	public int size()
	{
		int size = 0;
		for (final List< ? extends V> list : lists)
			size += list.size();
		return size;
	}
}
