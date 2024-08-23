/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.exception;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jdt.annotation.Nullable;

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

   public static boolean equals(final @Nullable Throwable ex1, final @Nullable Throwable ex2) {
      if (ex1 == ex2)
         return true;
      if (ex1 == null || ex2 == null //
            || !ex1.getClass().equals(ex2.getClass()) //
            || !Objects.equals(ex1.getMessage(), ex2.getMessage()))
         return false;

      final StackTraceElement[] stackTrace1 = ex1.getStackTrace();
      final StackTraceElement[] stackTrace2 = ex2.getStackTrace();
      if (stackTrace1.length != stackTrace2.length)
         return false;
      for (int i = 0; i < stackTrace1.length; i++) {
         if (!stackTrace1[i].equals(stackTrace2[i]))
            return false;
      }
      return true;
   }

   @Nullable
   public static <T extends Throwable> T getCauseOfType(final @Nullable Throwable ex, final @Nullable Class<T> type) {
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
    * as it uses StringBuilder internally instead of StringBuffer.
    */
   public static String getStackTrace(final Throwable ex) {
      try (var spw = new StringPrintWriter()) {
         ex.printStackTrace(spw);
         return spw.toString();
      }
   }

   /**
    * Faster alternative to {@link ExceptionUtils#getStackTrace(Throwable)}
    * as it uses StringBuilder internally instead of StringBuffer.
    */
   @Nullable
   public static String getStackTraceNullable(final @Nullable Throwable ex) {
      if (ex == null)
         return null;
      return getStackTrace(ex);
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
      final var sb = new StringBuilder();
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
      Args.notNull("ex", ex);

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
      final StackTraceElement caller = StackTrace.getCallerStackTraceElement();
      for (int i = 0; i < stack.length; i++) {
         if (stack[i].equals(caller)) {
            final var newStack = new StackTraceElement[stack.length - i];
            System.arraycopy(stack, i, newStack, 0, stack.length - i);
            exception.setStackTrace(newStack);
            break;
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

   @SuppressWarnings("deprecation")
   protected Exceptions() {
   }
}
