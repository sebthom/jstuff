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
package net.sf.jstuff.integration.persistence.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.comparator.SortBy;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Sebastian Thomschke
 */
public class PagedResultList<ItemType, SortKeyType> extends ArrayList<ItemType>
{
	private final static long serialVersionUID = 1L;

	private Class<ItemType> itemType;
	private int start;
	private SortBy<SortKeyType>[] sortedBy;
	private int totalCount;

	public PagedResultList()
	{
		super();
	}

	public PagedResultList(final Class<ItemType> itemType, final List<ItemType> items, final int start, final int totalCount)
	{
		super(items);
		setItemType(itemType);
		this.start = start;
		this.totalCount = totalCount;
	}

	public PagedResultList(final Class<ItemType> itemType, final List<ItemType> items, final int start, final int totalCount,
			final SortBy<SortKeyType>... sortedBy)
	{
		this(itemType, items, start, totalCount);
		this.sortedBy = sortedBy;
	}

	public Class<ItemType> getItemType()
	{
		return itemType;
	}

	public int getStart()
	{
		return start;
	}

	public SortBy< ? >[] getSortedBy()
	{
		return sortedBy;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	/**
	 * @return true if more items are available for retrieval
	 */
	public boolean isMoreItemsAvailable()
	{
		return totalCount > start + size();
	}

	public void setItemType(final Class<ItemType> itemType)
	{
		this.itemType = itemType;
	}

	public void setStart(final int start)
	{
		this.start = start;
	}

	public void setSortedBy(final SortBy<SortKeyType>... sortedBy)
	{
		this.sortedBy = sortedBy;
	}

	public void setTotalCount(final int total)
	{
		this.totalCount = total;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
