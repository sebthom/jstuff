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
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Accepts.*;
import junit.framework.TestCase;
import net.sf.jstuff.core.functional.Accepts.ChainableAccept;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AcceptsTest extends TestCase
{
	public void testAccepts()
	{
		final ChainableAccept<String> a2 = startingWith("#");
		assertTrue(a2.accept("#foo"));
		assertFalse(a2.accept(null));
		assertFalse(a2.accept("foo"));

		final Accept<String> a3 = a2.and(endingWith("#"));
		assertTrue(a3.accept("#foo#"));
		assertFalse(a3.accept("foo#"));
		assertFalse(a3.accept("#foo"));
		assertFalse(a3.accept(null));
		assertFalse(a3.accept("foo"));

		final ChainableAccept<String> a4 = notStartingWith("#");
		assertFalse(a4.accept("#foo"));
		assertTrue(a4.accept(null));
		assertTrue(a4.accept("foo"));

		final Accept<String> a5 = a4.and(notEndingWith("#"));
		assertTrue(a5.accept("foo"));
		assertFalse(a5.accept("foo#"));
		assertFalse(a5.accept("#foo"));
		assertTrue(a5.accept(null));
		assertTrue(a5.accept("foo"));

		final Accept<Integer> a6 = nonNull().and(largerThan(10)).and(smallerThan(20));
		assertFalse(a6.accept(null));
		assertFalse(a6.accept(2));
		assertTrue(a6.accept(19));
		assertFalse(a6.accept(21));

		final Accept<Long> a7 = nonNull().//
				and(//
				largerThan(20L).or(smallerThan(10L)).or(equalTo(12L)) //
				);
		assertFalse(a7.accept(null));
		assertTrue(a7.accept(21L));
		assertFalse(a7.accept(19L));
		assertFalse(a7.accept(11L));
		assertTrue(a7.accept(12L));
		assertTrue(a7.accept(9L));
	}
}
