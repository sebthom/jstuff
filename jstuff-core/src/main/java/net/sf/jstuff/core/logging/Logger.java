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
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.validation.Args;

/**
 * Features:
 * <li>Logger implementation using the {@link java.util.Formatter} syntax for message templates.
 * <li>{@link Logger#entry()} logs parameter name and parameter values.
 * <li>Stacktraces are logged truncated if logger is not at DEBUG level.
 * <li>{@link Logger#fatal}() always logs the full stacktrace
 * <li>Defaults to SLF4J as logging infrastructure and falls back to java.util.logging if SLF4J is not available.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public abstract class Logger {
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

   public static Logger create() {
      final String name = StackTrace.getCallerStackTraceElement(Logger.class).getClassName();
      return LoggerConfig.create(name);
   }

   public static Logger create(final Class<?> clazz) {
      Args.notNull("clazz", clazz);
      return LoggerConfig.create(clazz.getName());
   }

   public static Logger create(final String name) {
      Args.notNull("name", name);
      return LoggerConfig.create(name);
   }

   @SuppressWarnings("unchecked")
   public static <I> I createLogged(final I object, final Class<I> primaryInterface, final Class<?>... secondaryInterfaces) {
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
         Logger log = Logger.create(object.getClass());

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

   protected static String formatTraceEntry(final Method method, final Object... args) {
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

   protected static String formatTraceEntry(final Object... args) {
      if (args == null || args.length == 0)
         return METHOD_ENTRY_MARKER_NOARGS;

      final Class<?> loggedClass = StackTrace.getCallerClass(DelegatingLogger.FQCN);
      final StackTraceElement loggedSTE = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
      final Method method = Methods.findAnyCompatible(loggedClass, loggedSTE.getMethodName(), args);
      return formatTraceEntry(method, args);
   }

   protected static String formatTraceExit() {
      return METHOD_EXIT_MARKER_VOID;
   }

   protected static String formatTraceExit(final Object returnValue) {
      return METHOD_EXIT_MARKER + argToString(returnValue);
   }

   /**
    * Recursively sanitizes removes irrelevant elements from the stacktraces of the given exception and it's causes.
    */
   public static void sanitizeStackTraces(final Throwable ex) {
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

   public abstract void debug(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   public abstract void debug(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   public abstract void debug(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   public abstract void debug(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   public abstract void debug(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   public abstract void debug(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void debug(Throwable ex);

   public abstract void debug(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void debug(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs the instantiation of the given object at DEBUG level including the corresponding class's implementation version.
    */
   public abstract void debugNew(Object newInstance);

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry();

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry(Object arg1);

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry(Object arg1, Object arg2);

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry(Object arg1, Object arg2, Object arg3);

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry(Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * Logs a method entry at TRACE level.
    */
   public abstract void entry(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void error(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   public abstract void error(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   public abstract void error(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   public abstract void error(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   public abstract void error(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   public abstract void error(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void error(Throwable ex);

   public abstract void error(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void error(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs a method exit at TRACE level.
    */
   public abstract void exit();

   /**
    * Logs a method exit at TRACE level.
    *
    * @param returnValue the returnValue of the given method
    */
   public abstract <T> T exit(T returnValue);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    */
   public abstract void fatal(Throwable ex);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    */
   public abstract void fatal(Throwable ex, String msg);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    *
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void fatal(Throwable ex, String messageTemplate, Object... args);

   /**
    * @return the loggers name
    */
   public abstract String getName();

   public abstract void info(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   public abstract void info(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   public abstract void info(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   public abstract void info(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   public abstract void info(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   public abstract void info(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void info(Throwable ex);

   public abstract void info(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void info(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs the instantiation of the given object at INFO level including the corresponding class's implementation version.
    */
   public abstract void infoNew(Object newInstance);

   public abstract boolean isDebugEnabled();

   public abstract boolean isErrorEnabled();

   public abstract boolean isInfoEnabled();

   public abstract boolean isTraceEnabled();

   public abstract boolean isWarnEnabled();

   protected abstract void trace(Method location, String msg);

   public abstract void trace(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   public abstract void trace(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   public abstract void trace(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   public abstract void trace(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   public abstract void trace(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   public abstract void trace(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void trace(Throwable ex);

   public abstract void trace(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void trace(Throwable ex, String messageTemplate, Object... args);

   public abstract void warn(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   public abstract void warn(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   public abstract void warn(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   public abstract void warn(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   public abstract void warn(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   public abstract void warn(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   public abstract void warn(Throwable ex);

   public abstract void warn(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   public abstract void warn(Throwable ex, String messageTemplate, Object... args);
}
