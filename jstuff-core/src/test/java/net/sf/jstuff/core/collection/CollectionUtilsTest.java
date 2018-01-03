/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CollectionUtilsTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testIntersectListt() {
        assertEquals(0, intersect((List[]) null).size());
        assertEquals(0, intersect(Collections.emptyList()).size());
        assertEquals(0, intersect(Collections.emptyList(), null).size());

        final List<String> list1 = newArrayList("foo", "bar", "dog");
        final List<String> list2 = newArrayList("cat", "bar", "foo");

        assertEquals(2, intersect(list1, list2).size());
        assertEquals("foo", intersect(list1, list2).iterator().next());
        assertEquals(newArrayList("foo", "bar"), intersect(list1, list2));
    }

    @SuppressWarnings("unchecked")
    public void testIntersectSet() {
        assertEquals(0, intersect((Set[]) null).size());
        assertEquals(0, intersect(Collections.emptySet()).size());
        assertEquals(0, intersect(Collections.emptySet(), null).size());

        final Set<String> set1 = newLinkedHashSet("foo", "bar", "dog");
        final Set<String> set2 = newLinkedHashSet("cat", "bar", "foo");

        assertEquals(2, intersect(set1, set2).size());
        assertEquals("foo", intersect(set1, set2).iterator().next());
        assertEquals(newLinkedHashSet("foo", "bar"), intersect(set1, set2));
    }

    public void testHead() {
        final List<String> testList = newArrayList("1", "2", "3");

        assertEquals(newArrayList("1"), head(testList, 1));
        assertEquals(newArrayList("1", "2"), head(testList, 2));
        assertEquals(newArrayList("1", "2", "3"), head(testList, 3));
        assertEquals(newArrayList("1", "2", "3"), head(testList, 10));
        assertEquals(newArrayList(), head(testList, -10));
    }

    public void testTail() {
        final List<String> testList = newArrayList("1", "2", "3");

        assertEquals(newArrayList("3"), tail(testList, 1));
        assertEquals(newArrayList("2", "3"), tail(testList, 2));
        assertEquals(newArrayList("1", "2", "3"), tail(testList, 3));
        assertEquals(newArrayList("1", "2", "3"), tail(testList, 10));
        assertEquals(newArrayList(), tail(testList, -10));
    }
}
