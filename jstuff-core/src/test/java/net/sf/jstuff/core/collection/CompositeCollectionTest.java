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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeCollectionTest extends TestCase
{

	public void testCompositeCollection()
	{
		final List<String> l1 = Arrays.asList("a", "b");
		final List<String> l2 = Arrays.asList("c", "d");
		@SuppressWarnings("unchecked")
		final Collection<String> cc = CompositeCollection.of(l1, l2);
		assertEquals(4, cc.size());

		final List<String> l3 = new ArrayList<String>();
		for (final String s : cc)
			l3.add(s);
		assertEquals("a", l3.get(0));
		assertEquals("b", l3.get(1));
		assertEquals("c", l3.get(2));
		assertEquals("d", l3.get(3));
		assertEquals(4, l3.size());

		try
		{
			cc.add("foo");
		}
		catch (final UnsupportedOperationException ex)
		{

		}

		try
		{
			cc.remove("a");
		}
		catch (final UnsupportedOperationException ex)
		{

		}
	}
}
