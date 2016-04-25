/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StackTraceTest extends TestCase {
    private static class Outer {
        private static class Inner {
            private void ensureEquals(final Object a, final Object b) {
                assertEquals(a, b);
            }

            public void innerMethod() {
                ensureEquals("net.sf.jstuff.core.reflection.StackTraceTest$Outer$Inner", StackTrace.getThisStackTraceElement().getClassName());
                ensureEquals("innerMethod", StackTrace.getThisMethodName());
                ensureEquals("innerMethod", StackTrace.getThisStackTraceElement().getMethodName());
                ensureEquals(31, StackTrace.getThisLineNumber());
                ensureEquals("StackTraceTest.java", StackTrace.getThisFileName());

                ensureEquals("outerMethod", StackTrace.getCallerMethodName());
                ensureEquals(45, StackTrace.getCallerLineNumber());
                ensureEquals("StackTraceTest.java", StackTrace.getCallerFileName());
                ensureEquals(Outer.class.getName(), StackTrace.getCallerClassName());
                ensureEquals(Outer.class, StackTrace.getCallerClass());
            }
        }

        private final Inner caller2 = new Inner();

        public void outerMethod() {
            caller2.innerMethod();
        }
    }

    public void testStackTrace() {
        new Outer().outerMethod();
    }
}
