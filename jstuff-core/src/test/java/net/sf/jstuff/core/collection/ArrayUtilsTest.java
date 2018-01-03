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

import junit.framework.TestCase;
import net.sf.jstuff.core.functional.Accept;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayUtilsTest extends TestCase {

    public void testIntersect() {
        assertEquals(0, ArrayUtils.intersect((Object[]) null).length);
        assertEquals(0, ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY).length);
        assertEquals(0, ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY, null).length);

        final String[] arr1 = new String[] { "foo", "bar", "dog" };
        final String[] arr2 = new String[] { "cat", "bar", "foo" };

        assertEquals(2, ArrayUtils.intersect(arr1, arr2).length);
        assertEquals("foo", ArrayUtils.intersect(arr1, arr2)[0]);
        assertEquals("bar", ArrayUtils.intersect(arr1, arr2)[1]);
    }

    public void testFilter() {

        final String[] filtered = ArrayUtils.filter(new Accept<String>() {
            public boolean accept(final String obj) {
                return "foo".equals(obj) || "bar".equals(obj);
            }
        }, new String[] { "dog", "foo", "bar", "cat" });

        assertEquals(2, filtered.length);
        assertEquals("foo", filtered[0]);
        assertEquals("bar", filtered[1]);
    }
}
