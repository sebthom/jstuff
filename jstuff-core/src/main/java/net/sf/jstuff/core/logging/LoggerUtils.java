/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LoggerUtils {
   private static class ParanamerParamNamesResolver implements Invocable<String[], Method, RuntimeException> {
      final com.thoughtworks.paranamer.Paranamer paranamer = new com.thoughtworks.paranamer.CachingParanamer(
         new com.thoughtworks.paranamer.BytecodeReadingParanamer());

      @Override
      public String[] invoke(final Method method) {
         if (method == null)
            return ArrayUtils.EMPTY_STRING_ARRAY;
         return paranamer.lookupParameterNames(method, false);
      }
   }

   private static final String METHOD_ENTRY_MARKER = "ENTRY >> (";
   private static final String METHOD_ENTRY_MARKER_NOARGS = METHOD_ENTRY_MARKER + ")";
   private static final String METHOD_EXIT_MARKER = "EXIT  << ";
   private static final String METHOD_EXIT_MARKER_VOID = METHOD_EXIT_MARKER + "*void*";

   private static final Invocable<String[], Method, RuntimeException> PARAM_NAMES_RESOLVER;

   static {
      Invocable<String[], Method, RuntimeException> paramNamesResolver;
      try {
         // test if paranamer is available on classpath
         paramNamesResolver = new ParanamerParamNamesResolver();
      } catch (final Exception ex) {
         paramNamesResolver = new Invocable<String[], Method, RuntimeException>() {
            @Override
            public String[] invoke(final Method arg) throws RuntimeException {
               return ArrayUtils.EMPTY_STRING_ARRAY;
            }
         };
      } catch (final LinkageError err) {
         paramNamesResolver = new Invocable<String[], Method, RuntimeException>() {
            @Override
            public String[] invoke(final Method arg) throws RuntimeException {
               return ArrayUtils.EMPTY_STRING_ARRAY;
            }
         };
      }
      PARAM_NAMES_RESOLVER = paramNamesResolver;
   }

   private static String argToString(final Object object) {
      if (object == null)
         return "null";
      if (object.getClass().isArray())
         return Arrays.deepToString((Object[]) object);
      if (object instanceof String)
         return "\"" + object + "\"";
      return object.toString();
   }

   @SuppressWarnings("unchecked")
   static <I> I createLogged(final I object, final Class<I> primaryInterface, final Class<?>... secondaryInterfaces) {
      Args.notNull("object", object);
      Args.notNull("primaryInterface", primaryInterface);

      final Class<?>[] interfaces;
      if (secondaryInterfaces == null || secondaryInterfaces.length == 0) {
         interfaces = new Class<?>[] {primaryInterface};
      } else {
         interfaces = new Class<?>[secondaryInterfaces.length + 1];
         interfaces[0] = primaryInterface;
         System.arraycopy(secondaryInterfaces, 0, interfaces, 1, secondaryInterfaces.length);
      }

      return (I) Proxy.newProxyInstance(object.getClass().getClassLoader(), interfaces, new InvocationHandler() {
         LoggerInternal log = (LoggerInternal) Logger.create(object.getClass());

         @Override
         public Object invoke(final Object proxy, final Method interfaceMethod, final Object[] args) throws Throwable {
            if (log.isTraceEnabled()) {
               final long start = System.currentTimeMillis();
               final Method methodWithParameterNames = Methods.findPublic(object.getClass(), interfaceMethod.getName(), interfaceMethod.getParameterTypes());
               log.trace(methodWithParameterNames, formatTraceEntry(methodWithParameterNames, args));
               final Object returnValue = interfaceMethod.invoke(object, args);
               final String elapsed = String.format("%,d", System.currentTimeMillis() - start);
               if (Methods.isReturningVoid(interfaceMethod)) {
                  log.trace(methodWithParameterNames, formatTraceExit() + " " + elapsed + "ms");
               } else {
                  log.trace(methodWithParameterNames, formatTraceExit(returnValue) + " " + elapsed + "ms");
               }
               return returnValue;
            }
            return interfaceMethod.invoke(object, args);
         }
      });
   }

   static String formatTraceEntry(final Method method, final Object... args) {
      if (args == null || args.length == 0)
         return METHOD_ENTRY_MARKER_NOARGS;

      final StringBuilder sb = new StringBuilder(METHOD_ENTRY_MARKER);

      final String[] paramNames = PARAM_NAMES_RESOLVER.invoke(method);
      final int paramNamesLen = paramNames.length;
      if (paramNamesLen == 0) {
         sb.append(argToString(args[0]));
         for (int i = 1; i < args.length; i++) {
            sb.append(", ").append(argToString(args[i]));
         }
      } else {
         sb.append(paramNames[0]).append(": ").append(argToString(args[0]));
         for (int i = 1; i < paramNamesLen; i++) {
            sb.append(", ").append(paramNames[i]).append(": ").append(argToString(args[i]));
         }
      }
      return sb.append(")").toString();
   }

   static String formatTraceEntry(final Object... args) {
      if (args == null || args.length == 0)
         return METHOD_ENTRY_MARKER_NOARGS;

      final Class<?> loggedClass = StackTrace.getCallerClass(DelegatingLogger.FQCN);
      final StackTraceElement loggedSTE = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
      final Method method = Methods.findAnyCompatible(loggedClass, loggedSTE.getMethodName(), args);
      return formatTraceEntry(method, args);
   }

   static String formatTraceExit() {
      return METHOD_EXIT_MARKER_VOID;
   }

   static String formatTraceExit(final Object returnValue) {
      return METHOD_EXIT_MARKER + argToString(returnValue);
   }

   /**
    * Recursively sanitizes removes irrelevant elements from the stacktraces of the given exception and it's causes.
    */
   static void sanitizeStackTraces(final Throwable ex) {
      if (ex == null)
         return;

      final StackTraceElement[] stacktrace = ex.getStackTrace();
      if (stacktrace == null || stacktrace.length < 3)
         return;

      final List<StackTraceElement> sanitized = new ArrayList<>(stacktrace.length - 2);
      // we leave the first two elements untouched to keep the context
      sanitized.add(stacktrace[0]);
      sanitized.add(stacktrace[1]);
      for (int i = 2, l = stacktrace.length; i < l; i++) {
         final StackTraceElement ste = stacktrace[i];
         final String className = ste.getClassName();
         if (className.equals("java.lang.reflect.Method")) {
            continue;
         } else if (className.startsWith("org.springframework.aop.")) {
            continue;
         } else if (className.startsWith("sun.reflect.")) {
            continue;
         } else if (className.startsWith("com.")) {
            if (className.startsWith("sun.proxy.$Proxy", 4)) { // com.sun.proxy.$Proxy
               continue;
            } else if (className.startsWith("ibm.", 4)) { // com.ibm.
               if (className.startsWith("io.async.", 8)) { // com.ibm.io.async.
                  continue;
               } else if (className.startsWith("wps.", 8)) { // com.ibm.wps.
                  continue;
               } else if (className.startsWith("ws.", 8)) { // com.ibm.ws.
                  continue;
               } else if (className.startsWith("_jsp.", 8)) { // com.ibm._jsp.
                  continue;
               } else if (className.startsWith("jsse2.", 8)) { // com.ibm.jsse2.
                  if (className.startsWith(".", 15)) { // com.ibm.jsse2.b. || com.ibm.jsse2.f. || ...
                     continue;
                  }
               }
            }
         } else if (className.startsWith("org.codehaus.groovy.")) {
            if (className.startsWith("runtime.", 20)) { // org.codehaus.groovy.runtime.
               continue;
            } else if (className.startsWith("reflection.", 20)) { // org.codehaus.groovy.runtime.reflection.
               continue;
            }
         } else if (className.startsWith("groovy.lang.")) {
            if (className.startsWith("Meta", 12)) { // groovy.lang.Meta
               continue;
            } else if (className.startsWith("Closure", 12)) { // groovy.lang.Closure
               continue;
            }
         }

         sanitized.add(ste);
      }

      ex.setStackTrace(sanitized.toArray(new StackTraceElement[sanitized.size()]));

      if (ex.getCause() != null) {
         sanitizeStackTraces(ex.getCause());
      }
   }

}