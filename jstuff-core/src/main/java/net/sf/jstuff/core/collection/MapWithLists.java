/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithLists<K, V> extends MapWithCollections<K, V, List<V>>
{
	private static final long serialVersionUID = 1L;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<V> createCollection(final int initialCapacity, final float growthFactor)
	{
		return new ArrayList<V>(initialCapacity);
	}

	/**
	 * Returns the value to which this map maps the specified key.
	 * Returns an unmodifiable empty list if the map contains no mapping for this key.
	 */
	@SuppressWarnings("unchecked")
	public List<V> getSafe(final Object key)
	{
		final List<V> coll = super.get(key);
		return coll == null ? Collections.EMPTY_LIST : coll;
	}
}
