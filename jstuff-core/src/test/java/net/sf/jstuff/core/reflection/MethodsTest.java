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

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MethodsTest extends TestCase {

    static class EntityA {
        private EntityA property1;
        private int property2;

        protected EntityA getProperty1() {
            return property1;
        }

        protected int getProperty2() {
            return property2;
        }

        protected void setProperty1(final EntityA property1) {
            this.property1 = property1;
        }

        protected void setProperty2(final int property2) {
            this.property2 = property2;
        }
    }

    static class EntityB extends EntityA {

        boolean property3;

        @Override
        public EntityA getProperty1() {
            return super.getProperty1();
        }

        public boolean isProperty3() {
            return property3;
        }

        @Override
        public void setProperty1(final EntityA property1) {
            super.setProperty1(property1);
        }

        public void setProperty3(final boolean property3) {
            this.property3 = property3;
        }
    }

    public void test_findNonPublicGetterInSuperclass() {
        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property2");
            assertNotNull(m);
            assertEquals("getProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property2", Integer.class);
            assertNotNull(m);
            assertEquals("getProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property2", int.class);
            assertNotNull(m);
            assertEquals("getProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property2", String.class);
            assertNull(m);
        }
    }

    public void test_findNonPublicSetterInSuperclass() {
        {
            final Method m = Methods.findAnySetter(EntityB.class, "property2");
            assertNotNull(m);
            assertEquals("setProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property2", Integer.class);
            assertNotNull(m);
            assertEquals("setProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property2", int.class);
            assertNotNull(m);
            assertEquals("setProperty2", m.getName());
            assertEquals(EntityA.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property2", String.class);
            assertNull(m);
        }
    }

    public void test_findPublicNonOverloadedGetter() {

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property3");
            assertNotNull(m);
            assertEquals("isProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property3", Boolean.class);
            assertNotNull(m);
            assertEquals("isProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property3", boolean.class);
            assertNotNull(m);
            assertEquals("isProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnyGetter(EntityB.class, "property3", String.class);
            assertNull(m);
        }
    }

    public void test_findPublicNonOverloadedSetter() {

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property3");
            assertNotNull(m);
            assertEquals("setProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property3", Boolean.class);
            assertNotNull(m);
            assertEquals("setProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property3", boolean.class);
            assertNotNull(m);
            assertEquals("setProperty3", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findAnySetter(EntityB.class, "property3", String.class);
            assertNull(m);
        }
    }

    public void test_findPublicOverloadedGetter() {
        {
            final Method m = Methods.findPublicGetter(EntityB.class, "property1");
            assertNotNull(m);
            assertEquals("getProperty1", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findPublicGetter(EntityB.class, "property1", EntityA.class);
            assertNotNull(m);
            assertEquals("getProperty1", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findPublicGetter(EntityB.class, "property1", Integer.class);
            assertNull(m);
        }
    }

    public void test_findPublicOverloadedSetter() {
        {
            final Method m = Methods.findPublicSetter(EntityB.class, "property1");
            assertNotNull(m);
            assertEquals("setProperty1", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityA.class);
            assertNotNull(m);
            assertEquals("setProperty1", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findPublicSetter(EntityB.class, "property1", EntityB.class); // assignable check
            assertNotNull(m);
            assertEquals("setProperty1", m.getName());
            assertEquals(EntityB.class, m.getDeclaringClass());
        }

        {
            final Method m = Methods.findPublicSetter(EntityB.class, "property1", Integer.class);
            assertNull(m);
        }
    }

    public void test_getAllGetters() {
        assertEquals(2 + 1 /*Object#getClass()*/, Methods.getAllGetters(EntityA.class).size());
        assertEquals(3 + 1 /*Object#getClass()*/, Methods.getAllGetters(EntityB.class).size());
    }

    public void test_getAllSetters() {
        assertEquals(2, Methods.getAllSetters(EntityA.class).size());
        assertEquals(3, Methods.getAllSetters(EntityB.class).size());
    }

    public void test_getPublicGetters() {
        assertEquals(0 + 1 /*Object#getClass()*/, Methods.getPublicGetters(EntityA.class).size());
        assertEquals(2 + 1 /*Object#getClass()*/, Methods.getPublicGetters(EntityB.class).size());
        assertEquals(1, Methods.getPublicGetters(EntityB.class, Boolean.class).size());
        assertEquals(1, Methods.getPublicGetters(EntityB.class, boolean.class).size());
        assertEquals(1, Methods.getPublicGetters(EntityB.class, EntityA.class).size());
        assertEquals(1, Methods.getPublicGetters(EntityB.class, EntityB.class).size());
    }

    public void test_getPublicSetters() {
        assertEquals(0, Methods.getPublicSetters(EntityA.class).size());
        assertEquals(2, Methods.getPublicSetters(EntityB.class).size());
        assertEquals(1, Methods.getPublicSetters(EntityB.class, Boolean.class).size());
        assertEquals(1, Methods.getPublicSetters(EntityB.class, boolean.class).size());
        assertEquals(1, Methods.getPublicSetters(EntityB.class, EntityA.class).size());
        assertEquals(1, Methods.getPublicSetters(EntityB.class, EntityB.class).size());
    }
}
