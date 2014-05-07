/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import java.util.Set;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableSet<E> extends ObservableCollection<E, Set<E>> implements Set<E>
{
	public static <E> ObservableSet<E> of(final Set<E> set)
	{
		return new ObservableSet<E>(set);
	}

	public ObservableSet(final Set<E> set)
	{
		super(set);
	}
}
