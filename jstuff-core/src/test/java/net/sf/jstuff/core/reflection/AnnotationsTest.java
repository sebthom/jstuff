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
package net.sf.jstuff.core.reflection;

import java.util.Map;

import org.junit.Ignore;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.Maps;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AnnotationsTest extends TestCase {
    public void testCreate() {
        final Map<String, Object> m1 = Maps.newHashMap("value", "hello!");
        final Map<String, Object> m2 = Maps.newHashMap("value", "hi!");

        final Ignore a1a = Annotations.create(Ignore.class, m1);
        final Ignore a1b = Annotations.create(Ignore.class, m1);
        final Ignore a2 = Annotations.create(Ignore.class, m2);

        assertEquals(a1a, a1b);
        assertFalse(a1a.equals(a2));
        assertEquals("@org.junit.Ignore(value=hello!)", a1a.toString());
        assertEquals("@org.junit.Ignore(value=hi!)", a2.toString());
    }
}
