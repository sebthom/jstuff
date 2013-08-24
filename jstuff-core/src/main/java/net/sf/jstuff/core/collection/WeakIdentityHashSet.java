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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashSet<E> extends MapBackedSet<E> implements Cloneable
{
	private static final long serialVersionUID = 1L;

	public static <E> WeakIdentityHashSet<E> create()
	{
		return new WeakIdentityHashSet<E>();
	}

	public static <E> WeakIdentityHashSet<E> create(final int initialCapacity)
	{
		return new WeakIdentityHashSet<E>(initialCapacity);
	}

	public WeakIdentityHashSet()
	{
		this(16);
	}

	public WeakIdentityHashSet(final int initialCapacity)
	{
		super(new WeakIdentityHashMap<E, Boolean>(initialCapacity));
	}

	@Override
	public WeakIdentityHashSet<E> clone() throws CloneNotSupportedException
	{
		final WeakIdentityHashSet<E> copy = new WeakIdentityHashSet<E>(size());
		copy.addAll(this);
		return copy;
	}

	@SuppressWarnings("static-method")
	private void writeObject(final ObjectOutputStream oos) throws IOException
	{
		throw new NotSerializableException();
	}
}