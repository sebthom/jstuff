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
package net.sf.jstuff.core.reflection;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DuckTypesTest extends TestCase {

    private static interface Duck {
        void walk();
    }

    public static class DuckLike {
        public int count = 0;

        public void walk() {
            count++;
        }
    }

    public void testDuckTyping() {
        assertFalse(DuckTypes.isDuckType(new Object(), Duck.class));

        final DuckLike duckLike = new DuckLike();
        assertTrue(DuckTypes.isDuckType(duckLike, Duck.class));
        assertEquals(0, duckLike.count);
        final Duck duckTyped = DuckTypes.duckType(duckLike, Duck.class);
        duckTyped.walk();
        assertEquals(1, duckLike.count);
    }

    public void testDuckTypingWithAnonymousInnerClass() {
        final AtomicInteger count = new AtomicInteger();
        final Object duckLike = new Object() {
            @SuppressWarnings("unused")
            public void walk() {
                count.incrementAndGet();
            }
        };
        assertEquals(0, count.get());
        final Duck duckTyped = DuckTypes.duckType(duckLike, Duck.class);
        duckTyped.walk();
        assertEquals(1, count.get());
    }
}
