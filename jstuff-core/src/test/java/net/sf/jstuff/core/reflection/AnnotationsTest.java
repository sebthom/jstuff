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
package net.sf.jstuff.core.reflection;

import java.util.Map;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.CollectionUtils;

import org.junit.Ignore;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AnnotationsTest extends TestCase {
    public void testCreate() {
        final Map<String, Object> m1 = CollectionUtils.newHashMap("value", "hello!");
        final Map<String, Object> m2 = CollectionUtils.newHashMap("value", "hi!");

        final Ignore a1a = Annotations.create(Ignore.class, m1);
        final Ignore a1b = Annotations.create(Ignore.class, m1);
        final Ignore a2 = Annotations.create(Ignore.class, m2);

        assertEquals(a1a, a1b);
        assertFalse(a1a.equals(a2));
        assertEquals("@org.junit.Ignore(value=hello!)", a1a.toString());
        assertEquals("@org.junit.Ignore(value=hi!)", a2.toString());
    }
}
