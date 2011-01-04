/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = 1L;

	public static <K, V> LRUMap<K, V> create(final int maxSize)
	{
		return new LRUMap<K, V>(maxSize);
	}

	private final int maxSize;

	/**
	 * 
	 * @param initialCapacity the initial capacity.
	 * @param maxSize the maximum size of the cache. When maxSize is exceeded the oldest entry in the cache is removed.
	 */
	public LRUMap(final int maxSize)
	{
		super(maxSize, 0.75f, true);
		this.maxSize = maxSize;
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest)
	{
		return size() > maxSize;
	}
}