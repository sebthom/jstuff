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

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithSets<K, V> extends MapWithCollections<K, V, Set<V>>
{
	public static <K, V> MapWithSets<K, V> create()
	{
		return new MapWithSets<K, V>();
	}

	public static <K, V> MapWithSets<K, V> create(final int initialCapacity, final int initialSetCapacity)
	{
		return new MapWithSets<K, V>(initialCapacity, initialSetCapacity);
	}

	public MapWithSets()
	{
		super();
	}

	public MapWithSets(final int initialCapacity)
	{
		super(initialCapacity);
	}

	public MapWithSets(final int initialCapacity, final float growthFactor)
	{
		super(initialCapacity, growthFactor);
	}

	public MapWithSets(final int initialCapacity, final float growthFactor, final int initialCapacityOfSet,
			final float growthFactorOfSet)
	{
		super(initialCapacity, growthFactor, initialCapacityOfSet, growthFactorOfSet);
	}

	public MapWithSets(final int initialCapacity, final int initialCapacityOfSet)
	{
		super(initialCapacity, initialCapacityOfSet);
	}

	@Override
	protected Set<V> createCollection(final int initialCapacity, final float growthFactor)
	{
		return new HashSet<V>(initialCapacity, growthFactor);
	}
}
