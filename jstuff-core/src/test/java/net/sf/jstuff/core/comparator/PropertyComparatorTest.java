/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.comparator;

import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyComparatorTest extends TestCase {
    private static class Entity {
        String name;

        @SuppressWarnings("unused")
        Entity child;

        private Entity(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public void testPropertyComparator() {
        final Entity e1 = new Entity("444");
        final Entity e2 = new Entity("222");
        final Entity e3 = new Entity("333");
        final Entity e4 = new Entity(null);
        {
            final TreeSet<Entity> set = new TreeSet<Entity>(new PropertyComparator<Entity>("name"));
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);

            final Entity[] orderedEntities = set.toArray(new Entity[4]);
            assertEquals(e2, orderedEntities[0]);
            assertEquals(e3, orderedEntities[1]);
            assertEquals(e1, orderedEntities[2]);
            assertEquals(e4, orderedEntities[3]);
        }
        e1.child = new Entity("AAA");
        e2.child = new Entity("CCC");
        e3.child = new Entity(null);
        e4.child = new Entity("BBB");
        {
            final TreeSet<Entity> set = new TreeSet<Entity>(new PropertyComparator<Entity>("child.name"));
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);

            final Entity[] orderedEntities = set.toArray(new Entity[4]);
            assertEquals(e1, orderedEntities[0]);
            assertEquals(e4, orderedEntities[1]);
            assertEquals(e2, orderedEntities[2]);
            assertEquals(e3, orderedEntities[3]);
        }
    }
}
