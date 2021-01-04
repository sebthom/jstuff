/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import net.sf.jstuff.core.concurrent.ThreadSafe;
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
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public interface Logger {

   static Logger create() {
      final String name = StackTrace.getCallerStackTraceElement(Logger.class).getClassName();
      return LoggerConfig.create(name);
   }

   static Logger create(final Class<?> clazz) {
      Args.notNull("clazz", clazz);
      return LoggerConfig.create(clazz.getName());
   }

   static Logger create(final String name) {
      Args.notNull("name", name);
      return LoggerConfig.create(name);
   }

   static <I> I createLogged(final I object, final Class<I> primaryInterface, final Class<?>... secondaryInterfaces) {
      return LoggerUtils.createLogged(object, primaryInterface, secondaryInterfaces);
   }

   void debug(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   void debug(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   void debug(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   void debug(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   void debug(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   void debug(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void debug(Throwable ex);

   void debug(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void debug(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs the instantiation of the given object at DEBUG level including the corresponding class's implementation version.
    */
   void debugNew(Object newInstance);

   /**
    * Logs a method entry at TRACE level.
    */
   void entry();

   /**
    * Logs a method entry at TRACE level.
    */
   void entry(Object arg1);

   /**
    * Logs a method entry at TRACE level.
    */
   void entry(Object arg1, Object arg2);

   /**
    * Logs a method entry at TRACE level.
    */
   void entry(Object arg1, Object arg2, Object arg3);

   /**
    * Logs a method entry at TRACE level.
    */
   void entry(Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * Logs a method entry at TRACE level.
    */
   void entry(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void error(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   void error(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   void error(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   void error(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   void error(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   void error(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void error(Throwable ex);

   void error(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void error(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs a method exit at TRACE level.
    */
   void exit();

   /**
    * Logs a method exit at TRACE level.
    *
    * @param returnValue the returnValue of the given method
    */
   <T> T exit(T returnValue);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    */
   void fatal(Throwable ex);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    */
   void fatal(Throwable ex, String msg);

   /**
    * Creates a log entry at ERROR level and always logs the full stack trace.
    *
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void fatal(Throwable ex, String messageTemplate, Object... args);

   /**
    * @return the loggers name
    */
   String getName();

   void info(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   void info(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   void info(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   void info(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   void info(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   void info(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void info(Throwable ex);

   void info(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void info(Throwable ex, String messageTemplate, Object... args);

   /**
    * Logs the instantiation of the given object at INFO level including the corresponding class's implementation version.
    */
   void infoNew(Object newInstance);

   boolean isDebugEnabled();

   boolean isErrorEnabled();

   boolean isInfoEnabled();

   boolean isTraceEnabled();

   boolean isWarnEnabled();

   void trace(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   void trace(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   void trace(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   void trace(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   void trace(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   void trace(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void trace(Throwable ex);

   void trace(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void trace(Throwable ex, String messageTemplate, Object... args);

   void warn(String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg Argument referenced by the format specifiers in the message template.
    */
   void warn(String messageTemplate, Object arg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    */
   void warn(String messageTemplate, Object arg1, Object arg2);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    */
   void warn(String messageTemplate, Object arg1, Object arg2, Object arg3);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    */
   void warn(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param arg1 Argument referenced by the format specifiers in the message template.
    * @param arg2 Argument referenced by the format specifiers in the message template.
    * @param arg3 Argument referenced by the format specifiers in the message template.
    * @param arg4 Argument referenced by the format specifiers in the message template.
    * @param arg5 Argument referenced by the format specifiers in the message template.
    */
   void warn(String messageTemplate, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);

   void warn(Throwable ex);

   void warn(Throwable ex, String msg);

   /**
    * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
    * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra
    *           arguments are ignored.
    */
   void warn(Throwable ex, String messageTemplate, Object... args);

}
