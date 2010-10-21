/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FIFOMapTest extends TestCase
{
	public void testFIFOMap()
	{
		final FIFOMap<String, String> map = FIFOMap.create(3);
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		map.put("4", "4");
		assertEquals(3, map.size());
		assertTrue(map.containsKey("2"));
		assertTrue(map.containsKey("3"));
		assertTrue(map.containsKey("4"));

		assertEquals("2", map.get("2"));
		map.put("5", "5");

		assertTrue(map.containsKey("3"));
		assertTrue(map.containsKey("4"));
		assertTrue(map.containsKey("5"));
	}
}
