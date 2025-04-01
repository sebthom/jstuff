/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StackTrace {
   private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

   public static Class<?> getCallerClass() {
      return getStackFrame(3).getDeclaringClass();
   }

   /**
    * @return class that called the <code>calledClass</code>
    */
   public static @Nullable Class<?> getCallerClass(final Class<?> calledClass) {
      return getCallerClass(calledClass.getName());
   }

   /**
    * @return class that called the <code>calledClassName</code>
    */
   public static @Nullable Class<?> getCallerClass(final String calledClassName) {
      final var frame = getCallerStackFrame(calledClassName);
      return frame == null ? null : frame.getDeclaringClass();
   }

   public static String getCallerClassName() {
      return getStackFrame(3).getClassName();
   }

   public static String getCallerClassSimpleName() {
      return getStackFrame(3).getDeclaringClass().getSimpleName();
   }

   public static @Nullable String getCallerFileName() {
      return getStackFrame(3).getFileName();
   }

   public static int getCallerLineNumber() {
      return getStackFrame(3).getLineNumber();
   }

   public static String getCallerMethodName() {
      return getStackFrame(3).getMethodName();
   }

   public static StackFrame getCallerStackFrame() {
      return getStackFrame(3);
   }

   public static @Nullable StackFrame getCallerStackFrame(final Class<?> calledClass) {
      return getCallerStackFrame(calledClass.getName());
   }

   public static @Nullable StackFrame getCallerStackFrame(final String calledClassName) {
      final var frames = STACK_WALKER.walk(Stream::toList);
      for (int i = 0, l = frames.size() - 1; i < l; i++) {
         if (calledClassName.equals(frames.get(i).getClassName()))
            return frames.get(i + 1);
      }
      return null;
   }

   public static StackTraceElement getCallerStackTraceElement() {
      return getStackFrame(3).toStackTraceElement();
   }

   public static @Nullable StackTraceElement getCallerStackTraceElement(final Class<?> calledClass) {
      return getCallerStackTraceElement(calledClass.getName());
   }

   public static @Nullable StackTraceElement getCallerStackTraceElement(final String calledClassName) {
      final var frame = getCallerStackFrame(calledClassName);
      return frame == null ? null : frame.toStackTraceElement();
   }

   private static StackFrame getStackFrame(final int skip) {
      return STACK_WALKER.walk(frames -> frames //
         .skip(skip) //
         .findFirst() //
         .orElseThrow(() -> new AssertionError("should never be reached.")));
   }

   public static @Nullable String getThisFileName() {
      return getStackFrame(2).getFileName();
   }

   public static int getThisLineNumber() {
      return getStackFrame(2).getLineNumber();
   }

   public static String getThisMethodName() {
      return getStackFrame(2).getMethodName();
   }

   public static StackFrame getThisStackFrame() {
      return getStackFrame(2);
   }

   public static StackTraceElement getThisStackTraceElement() {
      return getStackFrame(2).toStackTraceElement();
   }

   /**
    * Removes the first element of the given exception's stack trace.
    *
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
