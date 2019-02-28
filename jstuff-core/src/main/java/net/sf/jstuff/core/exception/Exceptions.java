/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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

   @SuppressWarnings("unchecked")
   private static <T extends Throwable> void throwsUnchecked(final Throwable toThrow) throws T {
      throw (T) toThrow;
   }

   /**
    * Throws the given exception wrapped in a new {@link FastRuntimeException} instance if it isn't one itself.
    */
   public static RuntimeException throwUnchecked(final Throwable ex) {
      Args.notNull("ex", ex);
      if (ex instanceof RuntimeException)
         throw (RuntimeException) ex;
      throw new FastRuntimeException(ex);
   }

   /**
    * IMPORTANT: Checked exceptions thrown with this method are not catched by <code>try { } catch(RuntimeException ex) { }</code>
    * <p>
    * Throws the given exception bypassing the compiler check for checked exceptions.
    * <p>
    * This is considered a hack. You should prefer using {@link #throwUnchecked(Throwable)}.
    */
   public static RuntimeException throwUncheckedRaw(final Throwable ex) {
      Args.notNull("ex", ex);

      Exceptions.<RuntimeException> throwsUnchecked(ex); // CHECKSTYLE:IGNORE GenericWhitespace

      throw new AssertionError("should never be reached.");
   }

   /**
    * Wraps the given exception if not of the given type already.
    */
   @SuppressWarnings("unchecked")
   public static <T extends Throwable> T wrapAs(final Throwable t, final Class<T> type) {
      if (t == null)
         return null;
      if (Types.isInstanceOf(t, type))
         return (T) t;
      final T exception = t.getMessage() == null ? Types.newInstance(type) : Types.newInstance(type, t.getMessage());
      exception.initCause(t);

      if (exception.getStackTrace() != null) {
         final StackTraceElement caller = StackTrace.getCallerStackTraceElement();
         final StackTraceElement[] stack = exception.getStackTrace();
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
}
