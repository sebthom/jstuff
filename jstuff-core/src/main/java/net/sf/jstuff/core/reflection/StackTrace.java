/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StackTrace {

   private static final class CallerResolver extends SecurityManager {
      private static final CallerResolver INSTANCE = new CallerResolver();

      @Override
      public Class<?>[] getClassContext() {
         return super.getClassContext();
      }
   }

   private static final String CLASSNAME = StackTrace.class.getName();

   public static Class<?> getCallerClass() {
      final String callerClassName = getCallerClassName();

      final Class<?>[] stack = CallerResolver.INSTANCE.getClassContext();
      for (final Class<?> curr : stack)
         if (callerClassName.equals(curr.getName()))
            return curr;
      throw new AssertionError("should never be reached.");
   }

   /**
    * @return class that called the <code>calledClassName</code>
    */
   @Nullable
   public static Class<?> getCallerClass(final Class<?> calledClass) {
      return getCallerClass(calledClass.getName());
   }

   /**
    * @return class that called the <code>calledClassName</code>
    */
   @Nullable
   public static Class<?> getCallerClass(final String calledClassName) {
      final Class<?>[] stack = CallerResolver.INSTANCE.getClassContext();
      boolean foundInStack = false;
      for (final Class<?> curr : stack)
         if (calledClassName.equals(curr.getName())) {
            foundInStack = true;
         } else if (foundInStack)
            return curr;
      return null;
   }

   public static String getCallerClassName() {
      final StackTraceElement ste = getCallerStackTraceElement();
      return ste.getClassName();
   }

   public static String getCallerClassSimpleName() {
      final StackTraceElement ste = getCallerStackTraceElement();
      return Strings.substringAfterLast(ste.getClassName(), ".");
   }

   @Nullable
   public static String getCallerFileName() {
      final StackTraceElement ste = getCallerStackTraceElement();
      return ste.getFileName();
   }

   public static int getCallerLineNumber() {
      final StackTraceElement ste = getCallerStackTraceElement();
      return ste.getLineNumber();
   }

   public static String getCallerMethodName() {
      final StackTraceElement ste = getCallerStackTraceElement();
      return ste.getMethodName();
   }

   public static StackTraceElement getCallerStackTraceElement() {
      final var stack = Thread.currentThread().getStackTrace();
      boolean stackTraceClassFound = false;
      for (int i = 0, l = stack.length; i < l; i++) {
         final StackTraceElement elem = stack[i];
         if (CLASSNAME.equals(elem.getClassName())) {
            stackTraceClassFound = true;
         } else {
            if (stackTraceClassFound)
               return stack[i + 1];
         }
      }
      throw new AssertionError("should never be reached.");
   }

   @Nullable
   public static StackTraceElement getCallerStackTraceElement(final Class<?> calledClass) {
      return getCallerStackTraceElement(calledClass.getName());
   }

   @Nullable
   public static StackTraceElement getCallerStackTraceElement(final String calledClassName) {
      final var stack = Thread.currentThread().getStackTrace();
      boolean foundInStack = false;
      for (final StackTraceElement curr : stack)
         if (calledClassName.equals(curr.getClassName())) {
            foundInStack = true;
         } else if (foundInStack)
            return curr;
      return null;
   }

   @Nullable
   public static String getThisFileName() {
      final StackTraceElement ste = getThisStackTraceElement();
      return ste.getFileName();
   }

   public static int getThisLineNumber() {
      final StackTraceElement ste = getThisStackTraceElement();
      return ste.getLineNumber();
   }

   public static String getThisMethodName() {
      final StackTraceElement ste = getThisStackTraceElement();
      return ste.getMethodName();
   }

   public static StackTraceElement getThisStackTraceElement() {
      final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
      boolean stackTraceClassFound = false;
      for (final StackTraceElement elem : stack) {
         if (CLASSNAME.equals(elem.getClassName())) {
            stackTraceClassFound = true;
         } else {
            if (stackTraceClassFound)
               return elem;
         }
      }
      throw new AssertionError("should never be reached.");
   }

   /**
    * @return the given exception
    */
   public static <T extends Throwable> T removeFirstStackTraceElement(final T ex) {
      final var stack = ex.getStackTrace();
      if (stack.length > 0) {
         final @NonNull StackTraceElement @NonNull [] newStack = Arrays.copyOfRange(stack, 1, stack.length);
         ex.setStackTrace(newStack);
      }
      return ex;
   }

}
