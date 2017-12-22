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
package net.sf.jstuff.core.reflection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesMixinTest extends TestCase {
    protected interface TestEntity {
        String createGreeting(String name);

        String createClosing(String name);
    }

    protected static class TestEntityImpl implements TestEntity {
        public String createClosing(final String name) {
            return "Goodbye " + name + ".";
        }

        public String createGreeting(final String name) {
            return "Hello " + name + "!";
        }
    }

    public void testMixin() {
        final TestEntityImpl delegate = new TestEntityImpl();
        final TestEntity proxy = Types.createMixin(TestEntity.class, new Object() {
            @SuppressWarnings("unused")
            public String createGreeting(final String name) {
                return delegate.createGreeting(name) + " How are you?";
            }
        }, delegate);
        assertEquals("Hello John! How are you?", proxy.createGreeting("John"));
        assertEquals("Goodbye John.", proxy.createClosing("John"));
    }
}
