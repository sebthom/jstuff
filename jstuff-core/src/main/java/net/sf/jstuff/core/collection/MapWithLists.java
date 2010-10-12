/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithLists<K, V> extends MapWithCollections<K, V, List<V>>
{
	public static <K, V> MapWithLists<K, V> create()
	{
		return new MapWithLists<K, V>();
	}

	public static <K, V> MapWithLists<K, V> create(final int initialCapacity)
	{
		return new MapWithLists<K, V>(initialCapacity);
	}

	public MapWithLists()
	{
		super();
	}

	public MapWithLists(final int initialCapacity)
	{
		super(initialCapacity);
	}

	public MapWithLists(final int initialCapacity, final float growthFactor)
	{
		super(initialCapacity, growthFactor);
	}

	public MapWithLists(final int initialCapacity, final float growthFactor, final int initialCapacityOfList)
	{
		super(initialCapacity, growthFactor, initialCapacityOfList, 0.75f);
	}

	public MapWithLists(final int initialCapacity, final int initialCapacityOfList)
	{
		super(initialCapacity, initialCapacityOfList);
	}

	@Override
	protected List<V> createCollection(final int initialCapacity, final float growthFactor)
	{
		return new ArrayList<V>(initialCapacity);
	}
}
