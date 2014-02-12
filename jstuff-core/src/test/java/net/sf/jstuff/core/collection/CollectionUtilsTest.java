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

import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CollectionUtilsTest extends TestCase
{

	public void testHead()
	{
		final List<String> testList = CollectionUtils.newArrayList("1", "2", "3");

		assertEquals(CollectionUtils.newArrayList("1"), CollectionUtils.head(testList, 1));
		assertEquals(CollectionUtils.newArrayList("1", "2"), CollectionUtils.head(testList, 2));
		assertEquals(CollectionUtils.newArrayList("1", "2", "3"), CollectionUtils.head(testList, 3));
		assertEquals(CollectionUtils.newArrayList("1", "2", "3"), CollectionUtils.head(testList, 10));
		assertEquals(CollectionUtils.newArrayList(), CollectionUtils.head(testList, -10));
	}

	public void testTail()
	{
		final List<String> testList = CollectionUtils.newArrayList("1", "2", "3");

		assertEquals(CollectionUtils.newArrayList("3"), CollectionUtils.tail(testList, 1));
		assertEquals(CollectionUtils.newArrayList("2", "3"), CollectionUtils.tail(testList, 2));
		assertEquals(CollectionUtils.newArrayList("1", "2", "3"), CollectionUtils.tail(testList, 3));
		assertEquals(CollectionUtils.newArrayList("1", "2", "3"), CollectionUtils.tail(testList, 10));
		assertEquals(CollectionUtils.newArrayList(), CollectionUtils.tail(testList, -10));
	}
}
