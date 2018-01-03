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

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeMapTest extends TestCase {

    public void testCompositeList() {
        final Map<String, String> m1 = Maps.toMap("a=1;b=2;c=3", ";", "=");
        final Map<String, String> m2 = Maps.toMap("c=4;d=5;e=6", ";", "=");
        @SuppressWarnings("unchecked")
        final CompositeMap<String, String> cm = CompositeMap.of(m1, m2);
        assertEquals(5, cm.size());
        assertEquals(5, cm.keySet().size());
        assertEquals(5, cm.values().size());
        assertEquals("1", cm.get("a"));
        assertEquals("3", cm.get("c"));

        m1.put("a", "X");
        m1.put("d", "Y");
        m2.put("f", "Z");
        assertEquals(6, cm.size());
        assertEquals(6, cm.keySet().size());
        assertEquals(6, cm.values().size());
        try {
            cm.put("f", "f");
        } catch (final UnsupportedOperationException ex) {

        }

        try {
            cm.remove("a");
        } catch (final UnsupportedOperationException ex) {

        }
    }
}
