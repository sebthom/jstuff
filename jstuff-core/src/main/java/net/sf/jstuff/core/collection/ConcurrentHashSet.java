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

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ConcurrentHashSet<E> extends MapBackedSet<E>
{
	private static final long serialVersionUID = 1L;

	public ConcurrentHashSet()
	{
		super(new ConcurrentHashMap<E, Boolean>());
	}

	public ConcurrentHashSet(final int initialCapacity)
	{
		super(new ConcurrentHashMap<E, Boolean>(initialCapacity));
	}
}
