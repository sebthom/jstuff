/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithSets<K, V> extends MapWithCollections<K, V, Set<V>>
{
	private static final long serialVersionUID = 1L;

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

	public MapWithSets(final int initialCapacity, final int initialCapacityOfSet)
	{
		super(initialCapacity, initialCapacityOfSet);
	}

	@Override
	protected Set<V> create(final K key)
	{
		return new HashSet<V>(initialCapacityOfCollection, growthFactorOfCollection);
	}

	@Override
	protected Set<V> createNullSafe(final K key)
	{
		return Collections.emptySet();
	}
}
