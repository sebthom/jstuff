/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.core.date;

import java.sql.Date;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ImmutableDate extends Date
{
	private static final long serialVersionUID = 1L;

	public ImmutableDate()
	{
		super(System.currentTimeMillis());
	}

	public ImmutableDate(final Date date)
	{
		super(date.getTime());
	}

	@Override
	public ImmutableDate clone()
	{
		return new ImmutableDate(this);
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setDate(final int date)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setHours(final int i)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setMinutes(final int i)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setMonth(final int month)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setSeconds(final int i)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setTime(final long date)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported
	 */
	@Override
	public void setYear(final int year)
	{
		throw new UnsupportedOperationException();
	}
}
