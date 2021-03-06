/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.concurrent.RuntimeInterruptedException;
import net.sf.jstuff.core.io.RuntimeIOException;
import net.sf.jstuff.core.io.StringPrintWriter;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.security.RuntimeSecurityException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Exceptions extends ExceptionUtils {

   public static <T extends Throwable> T getCauseOfType(final Throwable ex, final Class<T> type) {
      if (ex == null || type == null)
         return null;

      Throwable current = ex;
      while (current != null) {
         if (type.isInstance(current))
            return type.cast(current);
         current = current.getCause();
      }
      return null;
   }

   /**
    * Faster alternative to {@link ExceptionUtils#getStackTrace(Throwable)}
    * as it uses StringBuilder instead of StringBuffer.
    */
   public static String getStackTrace(final Throwable ex) {
      if (ex == null)
         return null;

      try (StringPrintWriter spw = new StringPrintWriter()) {
         ex.printStackTrace(spw);
         return spw.toString();
      }
   }

   /**
    * IMPORTANT: Checked exceptions thrown with this method are not catched by <code>try { } catch(RuntimeException ex) { }</code>
    * <p>
    * Throws the given exception bypassing the compiler check for checked exceptions.
    * <p>
    * This is considered a hack. You should prefer using {@link #wrapAsRuntimeException(Throwable)}.
    */
   @SuppressWarnings("unchecked")
   public static <T extends Throwable> RuntimeException throwSneakily(final Throwable ex) throws T {
      Args.notNull("ex", ex);

      throw (T) ex;
   }

   public static String toString(final StackTraceElement[] stacktrace) {
      return toString(stacktrace, "");
   }

   public static String toString(final StackTraceElement[] stacktrace, final String indention) {
      if (stacktrace == null)
         return null;

      final StringBuilder sb = new StringBuilder();
      for (final StackTraceElement element : stacktrace) {
         sb.append(indention);
         sb.append(element);
         sb.append(Strings.NEW_LINE);
      }
      return sb.toString();
   }

   /**
    * Wraps the given exception if not of the given type already.
    */
   @SuppressWarnings("unchecked")
   public static <T extends Throwable> T wrapAs(final Throwable ex, final Class<T> type) {
      if (ex == null)
         return null;

      if (Types.isInstanceOf(ex, type))
         return (T) ex;

      if (type == RuntimeException.class) {
         if (ex instanceof InterruptedException)
            return (T) new RuntimeInterruptedException((InterruptedException) ex);
         if (ex instanceof GeneralSecurityException)
            return (T) new RuntimeSecurityException((GeneralSecurityException) ex);
         if (ex instanceof IOException)
            return (T) new RuntimeIOException((IOException) ex);
         return (T) new DelegatingRuntimeException(ex);
      }

      final T exception = ex.getMessage() == null ? Types.newInstance(type) : Types.newInstance(type, ex.getMessage());
      exception.initCause(ex);

      final StackTraceElement[] stack = exception.getStackTrace();
      if (stack != null) {
         final StackTraceElement caller = StackTrace.getCallerStackTraceElement();
         for (int i = 0; i < stack.length; i++) {
            if (stack[i].equals(caller)) {
               final StackTraceElement[] newStack = new StackTraceElement[stack.length - i];
               System.arraycopy(stack, i, newStack, 0, stack.length - i);
               exception.setStackTrace(newStack);
               break;
            }
         }
      }

      return exception;
   }

   /**
    * Wraps the given exception if not already a runtime exception.
    */
   public static RuntimeException wrapAsRuntimeException(final Throwable t) {
      return wrapAs(t, RuntimeException.class);
   }
}
