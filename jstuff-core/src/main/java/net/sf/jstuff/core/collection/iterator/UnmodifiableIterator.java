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
package net.sf.jstuff.core.collection.iterator;

import java.io.Serializable;
import java.util.Iterator;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnmodifiableIterator<T> implements Iterator<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	private final Iterator<T> delegate;

	public UnmodifiableIterator(final Iterator<T> delegate)
	{
		Args.notNull("delegate", delegate);
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
