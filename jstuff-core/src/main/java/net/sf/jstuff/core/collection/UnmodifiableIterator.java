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
public class UnmodifiableIterator<T> implements Iterator<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	public static <T> UnmodifiableIterator<T> of(final Iterator<T> delegate)
	{
		return new UnmodifiableIterator<T>(delegate);
	}

	private final Iterator<T> delegate;

	public UnmodifiableIterator(final Iterator<T> delegate)
	{
		this.delegate = delegate;
	}

	public boolean hasNext()
	{
		return delegate.hasNext();
	}

	public T next()
	{
		return delegate.next();
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
