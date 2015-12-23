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
package net.sf.jstuff.core.collection;

import java.util.Set;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashSetTest extends TestCase {
    private static class Entity {
        private String name;

        public Entity setName(final String name) {
            this.name = name;
            return this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Entity other = (Entity) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }
    }

    public void testWeakIdentityHashSet() throws InterruptedException {
        final WeakIdentityHashSet<Entity> identitySet = WeakIdentityHashSet.create();

        Entity e1 = new Entity().setName("aa");
        Entity e2 = new Entity().setName("aa");

        assertEquals(e1, e2);
        assertNotSame(e1, e2);

        identitySet.add(e1);
        identitySet.add(e2);

        assertEquals(2, identitySet.size());
        assertTrue(identitySet.contains(e1));
        assertTrue(identitySet.contains(e2));
        assertEquals(2, identitySet.toArray().length);

        System.gc();
        Thread.sleep(1000);

        assertEquals(2, identitySet.size());
        assertTrue(identitySet.contains(e1));
        assertTrue(identitySet.contains(e2));

        final Set<Entity> identitySet2 = WeakIdentityHashSet.create();
        identitySet2.add(e1);
        identitySet2.add(e2);

        assertEquals(identitySet, identitySet2);

        identitySet2.remove(e2);
        assertTrue(identitySet2.contains(e1));
        assertFalse(identitySet2.contains(e2));
        assertFalse(identitySet.equals(identitySet2));

        e1 = null;
        e2 = null;

        System.gc();
        Thread.sleep(1000);

        assertEquals(0, identitySet.size());
        assertEquals(0, identitySet2.size());
    }
}
