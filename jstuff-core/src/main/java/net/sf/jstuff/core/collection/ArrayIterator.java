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

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayIterator<T> implements Iterator<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	public static <T> ArrayIterator<T> of(final T... array)
	{
		return new ArrayIterator<T>(array);
	}

	private final T[] array;
	private int currentIndex = 0;

	public ArrayIterator(final T... array)
	{
		this.array = array;
	}

	public boolean hasNext()
	{
		return currentIndex < array.length;
	}

	public T next()
	{
		return array[currentIndex++];
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
