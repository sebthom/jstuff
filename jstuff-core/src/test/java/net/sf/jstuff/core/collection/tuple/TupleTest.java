/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.core.collection.tuple;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TupleTest extends TestCase {

    public void testTuple1() {
        final Tuple1<String> t1 = Tuple1.create("a");
        final Tuple1<String> t2 = Tuple1.create("b");
        final Tuple1<String> t3 = Tuple1.create("a");
        assertFalse(t1.equals(t2));
        assertEquals(t1, t3);
    }

    public void testTuple2() {
        final Tuple2<String, String> t1 = Tuple2.create("a", "a");
        final Tuple2<String, String> t2 = Tuple2.create("b", "a");
        final Tuple2<String, String> t3 = Tuple2.create("a", "a");
        assertFalse(t1.equals(t2));
        assertEquals(t1, t3);
    }
}
