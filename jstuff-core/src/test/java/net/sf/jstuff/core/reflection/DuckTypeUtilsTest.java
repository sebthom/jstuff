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
package net.sf.jstuff.core.reflection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DuckTypeUtilsTest extends TestCase
{
	private static interface Duck
	{
		void walk();
	}

	public static class DuckLike
	{
		public int count = 0;

		public void walk()
		{
			count++;
		}
	}

	public void testDuckType()
	{
		final DuckLike duckLike = new DuckLike();
		assertEquals(0, duckLike.count);
		DuckTypeUtils.duckType(duckLike, Duck.class).walk();
		assertEquals(1, duckLike.count);
	}
}
