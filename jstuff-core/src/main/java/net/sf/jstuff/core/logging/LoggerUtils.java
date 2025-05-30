/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
abstract class LoggerUtils {

   private static final String METHOD_ENTRY_MARKER = "ENTRY >> (";
   private static final String METHOD_ENTRY_MARKER_NOARGS = METHOD_ENTRY_MARKER + ")";
   private static final String METHOD_EXIT_MARKER = "EXIT  << ";
   private static final String METHOD_EXIT_MARKER_VOID = METHOD_EXIT_MARKER + "*void*";

   private static String argToString(final @Nullable Object object) {
      if (object == null)
         return "null";
      if (object.getClass().isArray())
         return Arrays.deepToString((Object[]) object);
      if (object instanceof String)
         return "\"" + object + "\"";
      return object.toString();
   }

   @SuppressWarnings("unchecked")
   static <@NonNull I> I createLogged(final I object, final Class<I> primaryInterface, final Class<?> @Nullable... secondaryInterfaces) {
      Args.notNull("primaryInterface", primaryInterface);

      final Class<?>[] interfaces;
      if (secondaryInterfaces == null || secondaryInterfaces.length == 0) {
         interfaces = new Class<?>[] {primaryInterface};
      } else {
         interfaces = new Class<?>[secondaryInterfaces.length + 1];
         interfaces[0] = primaryInterface;
         System.arraycopy(secondaryInterfaces, 0, interfaces, 1, secondaryInterfaces.length);
      }

      final LoggerInternal log = (LoggerInternal) Logger.create(object.getClass());
      return (I) Proxy.newProxyInstance(object.getClass().getClassLoader(), interfaces, (proxy, interfaceMethod, args) -> {
         if (log.isTraceEnabled()) {
            final long start = System.nanoTime();
            final Method methodWithParameterNames = Methods.getPublic(object.getClass(), interfaceMethod.getName(), interfaceMethod
               .getParameterTypes());
            log.trace(methodWithParameterNames, formatTraceEntry(methodWithParameterNames, args));
            final Object returnValue = interfaceMethod.invoke(object, args);
            final String elapsed = String.format("%,d", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            if (Methods.isReturningVoid(interfaceMethod)) {
               log.trace(methodWithParameterNames, formatTraceExit() + " " + elapsed + "ms");
            } else {
               log.trace(methodWithParameterNames, formatTraceExit(returnValue) + " " + elapsed + "ms");
            }
            return returnValue;
         }
         return interfaceMethod.invoke(object, args);
      });
   }

   static String formatTraceEntry(final Method method, final Object @Nullable... args) {
      if (args == null || args.length == 0)
         return METHOD_ENTRY_MARKER_NOARGS;

      final var sb = new StringBuilder(METHOD_ENTRY_MARKER);

      final Parameter[] params = method.getParameters();
      if (params.length == 0) {
         sb.append(argToString(args[0]));
         for (int i = 1; i < args.length; i++) {
            sb.append(", ").append(argToString(args[i]));
         }
      } else {
         sb.append(params[0].getName()).append(": ").append(argToString(args[0]));
         for (int i = 1; i < params.length; i++) {
            sb.append(", ").append(params[i].getName()).append(": ").append(argToString(args[i]));
         }
      }
      return sb.append(")").toString();
   }

   static String formatTraceEntry(final Object @Nullable... args) {
      if (args == null || args.length == 0)
         return METHOD_ENTRY_MARKER_NOARGS;

      final Class<?> loggedClass = asNonNullUnsafe(StackTrace.getCallerClass(DelegatingLogger.FQCN));
      final StackTraceElement loggedSTE = asNonNullUnsafe(StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN));
      final Method method = Methods.findAnyCompatible(loggedClass, loggedSTE.getMethodName(), args);
      if (method == null) {
         final var sb = new StringBuilder(METHOD_ENTRY_MARKER);
         sb.append(argToString(args[0]));
         for (int i = 1; i < args.length; i++) {
            sb.append(", ").append(argToString(args[i]));
         }
         return sb.append(")").toString();
      }
      return formatTraceEntry(method, args);
   }

   static String formatTraceExit() {
      return METHOD_EXIT_MARKER_VOID;
   }

   static String formatTraceExit(final @Nullable Object returnValue) {
      return METHOD_EXIT_MARKER + argToString(returnValue);
   }

   /**
    * Recursively sanitizes removes irrelevant elements from the stacktraces of the given exception and it's causes.
    */
   static void sanitizeStackTraces(final @Nullable Throwable ex) {
      if (ex == null)
         return;

      final var stack = ex.getStackTrace();
      if (stack.length < 3)
         return;

      final var sanitized = new ArrayList<StackTraceElement>(stack.length - 2);
      // we leave the first two elements untouched to keep the context
      sanitized.add(stack[0]);
      sanitized.add(stack[1]);
      for (int i = 2, l = stack.length; i < l; i++) {
         final StackTraceElement ste = stack[i];
         final String className = ste.getClassName();
         if ("java.lang.reflect.Method".equals(className) //
               || className.startsWith("org.springframework.aop.") //
               || className.startsWith("sun.reflect.") //
         ) {
            continue;
         }
         if (className.startsWith("com.")) {
            if (className.startsWith("sun.proxy.$Proxy", 4)) { // com.sun.proxy.$Proxy
               continue;
            } else if (className.startsWith("ibm.", 4)) { // com.ibm.
               if (className.startsWith("io.async.", 8) // com.ibm.io.async.
                     || className.startsWith("wps.", 8) // com.ibm.wps.
                     || className.startsWith("ws.", 8) // com.ibm.ws.
                     || className.startsWith("_jsp.", 8) // com.ibm._jsp.
               ) {
                  continue;
               } else if (className.startsWith("jsse2.", 8)) { // com.ibm.jsse2.
                  if (className.startsWith(".", 15)) { // com.ibm.jsse2.b. || com.ibm.jsse2.f. || ...
                     continue;
                  }
               }
            }
         } else if (className.startsWith("org.codehaus.groovy.")) {
            if (className.startsWith("runtime.", 20) // org.codehaus.groovy.runtime.
                  || className.startsWith("reflection.", 20) // org.codehaus.groovy.runtime.reflection.
            ) {
               continue;
            }
         } else if (className.startsWith("groovy.lang.")) {
            if (className.startsWith("Meta", 12) // groovy.lang.Meta
                  || className.startsWith("Closure", 12) // groovy.lang.Closure
            ) {
               continue;
            }
         }

         sanitized.add(ste);
      }

      ex.setStackTrace(asNonNullUnsafe(sanitized.toArray(StackTraceElement[]::new)));

      if (ex.getCause() != null) {
         sanitizeStackTraces(ex.getCause());
      }
   }
}
