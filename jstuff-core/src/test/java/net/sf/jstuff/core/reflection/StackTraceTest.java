/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
            ensureEquals(28, StackTrace.getThisLineNumber());
            ensureEquals("StackTraceTest.java", StackTrace.getThisFileName());

            ensureEquals("outerMethod", StackTrace.getCallerMethodName());
            ensureEquals(42, StackTrace.getCallerLineNumber());
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
