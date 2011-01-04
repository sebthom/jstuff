/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
public class NumberComparator implements Comparator<Number>, Serializable
{
	private static final long serialVersionUID = 1L;

	public int compare(final Number o1, final Number o2)
	{
		final double d1 = o1.doubleValue();
		final double d2 = o2.doubleValue();

		return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
	}
}