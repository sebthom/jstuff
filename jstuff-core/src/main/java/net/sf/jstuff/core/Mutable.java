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
package net.sf.jstuff.core;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Mutable<T> implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static <T> Mutable<T> create(final T initialValue)
	{
		return new Mutable<T>(initialValue);
	}

	private T value;

	public Mutable(final T initialValue)
	{
		value = initialValue;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(final T newValue)
	{
		value = newValue;
	}

	@Override
	public String toString()
	{
		return StringUtils.toString(this);
	}
}
