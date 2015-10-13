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
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateComparator implements Comparator<Date>, Serializable
{
	private static final long serialVersionUID = 1L;

	public static final DateComparator INSTANCE = new DateComparator();

	protected DateComparator()
	{
		super();
	}

	public int compare(final Date o1, final Date o2)
	{
		final long n1 = o1.getTime();
		final long n2 = o2.getTime();

		return n1 < n2 ? -1 : n1 > n2 ? 1 : 0;
	}
}