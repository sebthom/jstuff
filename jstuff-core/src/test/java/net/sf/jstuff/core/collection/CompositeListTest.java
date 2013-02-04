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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeListTest extends TestCase
{

	public void testCompositeList()
	{
		final List<String> l1 = Arrays.asList("a", "b");
		final List<String> l2 = Arrays.asList("c", "d");
		@SuppressWarnings("unchecked")
		final List<String> cl = new CompositeList<String>(l1, l2);
		assertEquals(4, cl.size());
		assertEquals("a", cl.get(0));
		assertEquals("b", cl.get(1));
		assertEquals("c", cl.get(2));
		assertEquals("d", cl.get(3));
		try
		{
			cl.get(10);
		}
		catch (final IndexOutOfBoundsException ex)
		{
			assertEquals("Index: 10, Size: 4", ex.getMessage());
		}

		try
		{
			cl.add("foo");
		}
		catch (final UnsupportedOperationException ex)
		{

		}

		try
		{
			cl.remove(2);
		}
		catch (final UnsupportedOperationException ex)
		{

		}
	}
}
