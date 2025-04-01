/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class StackTraceTest {
   private static final class Outer {
      private static final class Inner {
         private void ensureEquals(@Nullable final Object a, @Nullable final Object b) {
            assertThat(b).isEqualTo(a);
         }

         void innerMethod() {
            ensureEquals("StackTraceTest.java", StackTrace.getThisFileName());
            ensureEquals(Inner.class, StackTrace.getThisStackFrame().getDeclaringClass());
            ensureEquals("net.sf.jstuff.core.reflection.StackTraceTest$Outer$Inner", StackTrace.getThisStackFrame().getClassName());
            ensureEquals("net.sf.jstuff.core.reflection.StackTraceTest$Outer$Inner", StackTrace.getThisStackTraceElement().getClassName());
            ensureEquals("innerMethod", StackTrace.getThisMethodName());
            ensureEquals("innerMethod", StackTrace.getThisStackFrame().getMethodName());
            ensureEquals("innerMethod", StackTrace.getThisStackTraceElement().getMethodName());
            ensureEquals(30, StackTrace.getThisLineNumber());

            ensureEquals("StackTraceTest.java", StackTrace.getCallerFileName());
            ensureEquals(Outer.class.getName(), StackTrace.getCallerClassName());
            ensureEquals(Outer.class.getSimpleName(), StackTrace.getCallerClassSimpleName());
            ensureEquals(Outer.class, StackTrace.getCallerClass());
            ensureEquals("outerMethod", StackTrace.getCallerMethodName());
            ensureEquals("outerMethod", StackTrace.getCallerStackFrame().getMethodName());
            ensureEquals("outerMethod", StackTrace.getCallerStackTraceElement().getMethodName());
            ensureEquals(49, StackTrace.getCallerLineNumber());

            ensureEquals(StackTraceTest.class, StackTrace.getCallerClass(Outer.class));
            ensureEquals(Outer.class, StackTrace.getCallerClass(Inner.class));
         }
      }

      private final Inner caller2 = new Inner();

      void outerMethod() {
         caller2.innerMethod();
      }
   }

   @Test
   void testRemoveFirstStackTraceElement() {
      final var ex = new RuntimeException();
      final var stackLength = ex.getStackTrace().length;
      StackTrace.removeFirstStackTraceElement(ex);
      assertThat(ex.getStackTrace()).hasSize(stackLength - 1);
   }

   @Test
   void testStackTrace() {
      new Outer().outerMethod();
   }
}
