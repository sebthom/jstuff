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

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashMapTest extends TestCase
{
	private static class Entity
	{
		private String name;

		public Entity setName(final String name)
		{
			this.name = name;
			return this;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + (name == null ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final Entity other = (Entity) obj;
			if (name == null)
			{
				if (other.name != null) return false;
			}
			else if (!name.equals(other.name)) return false;
			return true;
		}
	}

	public void testWeakIdentityHashMap() throws InterruptedException
	{
		final Map<Entity, Object> ihm = WeakIdentityHashMap.create();
		final Map<Entity, Object> hm = CollectionUtils.newHashMap();

		final Entity e1 = new Entity().setName("aa");
		final Entity e2 = new Entity().setName("aa");

		assertEquals(e1, e2);
		assertNotSame(e1, e2);

		ihm.put(e1, Boolean.TRUE);
		ihm.put(e2, Boolean.TRUE);
		assertTrue(ihm.containsKey(e1));
		assertTrue(ihm.containsKey(e2));

		hm.put(e1, Boolean.TRUE);
		hm.put(e2, Boolean.TRUE);

		assertEquals(2, ihm.size());
		assertEquals(1, hm.size());

		final Map<Entity, Object> ihm2 = WeakIdentityHashMap.create();
		ihm2.put(e1, Boolean.TRUE);
		ihm2.put(e2, Boolean.TRUE);
		assertEquals(ihm, ihm2);
		ihm2.remove(e2);
		assertFalse(ihm.equals(ihm2));

		System.gc();
		Thread.sleep(1000);
		assertEquals(1, hm.size());
		assertEquals(0, ihm.size());
	}
}
