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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BooleanComparator implements Comparator<Boolean>, Serializable
{
	private static final long serialVersionUID = 1L;

	public static final BooleanComparator INSTANCE = new BooleanComparator();

	protected BooleanComparator()
	{
		super();
	}

	public int compare(final Boolean o1, final Boolean o2)
	{
		final boolean b1 = o1.booleanValue();
		final boolean b2 = o1.booleanValue();

		return b1 == b2 ? 0 : b1 ? 1 : -1;
	}
}