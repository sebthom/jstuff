/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StackTraceTest {
   private static class Outer {
      private static class Inner {
         private void ensureEquals(@Nullable final Object a, @Nullable final Object b) {
            assertThat(b).isEqualTo(a);
         }

         public void innerMethod() {
            ensureEquals("net.sf.jstuff.core.reflection.StackTraceTest$Outer$Inner", StackTrace.getThisStackTraceElement().getClassName());
            ensureEquals("innerMethod", StackTrace.getThisMethodName());
            ensureEquals("innerMethod", StackTrace.getThisStackTraceElement().getMethodName());
            ensureEquals(26, StackTrace.getThisLineNumber());
            ensureEquals("StackTraceTest.java", StackTrace.getThisFileName());

            ensureEquals("outerMethod", StackTrace.getCallerMethodName());
            ensureEquals(40, StackTrace.getCallerLineNumber());
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

   @Test
   public void testRemoveFirstStackTraceElement() {
      final var ex = new RuntimeException();
      final var stackLength = ex.getStackTrace().length;
      StackTrace.removeFirstStackTraceElement(ex);
      assertThat(ex.getStackTrace().length).isEqualTo(stackLength - 1);
   }

   @Test
   public void testStackTrace() {
      new Outer().outerMethod();
   }
}
