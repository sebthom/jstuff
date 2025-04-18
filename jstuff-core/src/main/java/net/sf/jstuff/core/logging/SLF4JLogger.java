/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import static net.sf.jstuff.core.logging.LoggerUtils.*;
import static org.slf4j.spi.LocationAwareLogger.*;

import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
final class SLF4JLogger implements LoggerInternal {

   private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SLF4JLogger.class);

   private final org.slf4j.Logger logger;

   private final @Nullable LocationAwareLogger loggerEx;
   private final String loggerName;

   SLF4JLogger(final String name) {
      logger = LoggerFactory.getLogger(name);
      loggerName = logger.getName();
      if (logger instanceof final LocationAwareLogger l) {
         loggerEx = l;
      } else {
         loggerEx = null;
      }
   }

   private void _log(final int level, final @Nullable String message) {
      final String effectiveMessage;
      if (LoggerConfig.isAddLocationToDebugMessages && logger.isDebugEnabled()) {
         final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
         if (caller == null) { // should never happen
            LOG.error("Unexpected stacktrace " + Strings.join(Thread.currentThread().getStackTrace(), "\n"));
            effectiveMessage = message;
         } else {
            final String methodName = caller.getMethodName();
            effectiveMessage = methodName + "():" + caller.getLineNumber() + " " + message;
         }
      } else {
         effectiveMessage = message;
      }

      if (loggerEx != null) {
         loggerEx.log(null, DelegatingLogger.FQCN, level, effectiveMessage, null, null);
      } else {
         switch (level) {
            case TRACE_INT:
               logger.trace(effectiveMessage);
               break;
            case DEBUG_INT:
               logger.debug(effectiveMessage);
               break;
            case INFO_INT:
               logger.info(effectiveMessage);
               break;
            case WARN_INT:
               logger.info(effectiveMessage);
               break;
            case ERROR_INT:
               logger.info(effectiveMessage);
               break;
         }
      }
   }

   private void _log(final int level, final @Nullable String message, final @Nullable Throwable ex) {
      if (ex == null) {
         _log(level, message);
         return;
      }

      if (LoggerConfig.isSanitizeStrackTracesEnabled) {
         sanitizeStackTraces(ex);
      }

      String effectiveMessage;
      final Throwable effectiveException;
      if (logger.isDebugEnabled()) {
         effectiveMessage = message == null || message.isEmpty() ? "Catched " : message;
         effectiveException = ex;
         if (LoggerConfig.isAddLocationToDebugMessages) {
            final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
            if (caller == null) { // should never happen
               LOG.error("Unexpected stacktrace " + Strings.join(Thread.currentThread().getStackTrace(), "\n"));
            } else {
               effectiveMessage = caller.getMethodName() + "():" + caller.getLineNumber() + " " + effectiveMessage;
            }
         }
      } else {
         if (LoggerConfig.isCompactExceptionLoggingEnabled) {
            final var sb = new StringBuilder();
            if (message == null || message.isEmpty()) {
               sb.append("Catched ");
            } else {
               sb.append(message).append(" reason: ");
            }
            sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
            final StackTraceElement[] stack = ex.getStackTrace();
            if (stack.length > 0) {
               sb.append("\tat ").append(stack[0]).append("\n");
               if (stack.length > 1) {
                  sb.append("\tat ").append(stack[1]).append("\n");
               }
               if (stack.length > 2) {
                  sb.append("\t[StackTrace truncated - set log level of ").append(loggerName).append(" to FINE for full details]");
               }
            }
            effectiveMessage = sb.toString();
            effectiveException = null;
         } else {
            effectiveMessage = message == null || message.isEmpty() ? "Catched " : message;
            effectiveException = ex;
         }
      }

      if (loggerEx != null) {
         loggerEx.log(null, DelegatingLogger.FQCN, level, effectiveMessage, null, effectiveException);
      } else {
         switch (level) {
            case TRACE_INT:
               logger.trace(effectiveMessage, effectiveException);
               break;
            case DEBUG_INT:
               logger.debug(effectiveMessage, effectiveException);
               break;
            case INFO_INT:
               logger.info(effectiveMessage, effectiveException);
               break;
            case WARN_INT:
               logger.info(effectiveMessage, effectiveException);
               break;
            case ERROR_INT:
               logger.info(effectiveMessage, effectiveException);
               break;
         }
      }
   }

   @Override
   public void debug(final @Nullable String msg) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, msg);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void debug(final Throwable ex) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, "Unexpected exception occured: " + ex.getMessage(), ex);
   }

   @Override
   public void debug(final Throwable ex, final @Nullable String msg) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, msg, ex);
   }

   @Override
   public void debug(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, args), ex);
   }

   @Override
   public void debugNew(final Object newInstance) {
      if (!logger.isDebugEnabled())
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.isEmpty()) {
         _log(DEBUG_INT, newInstance.toString() + " instantiated.");
      } else {
         _log(DEBUG_INT, newInstance + " v" + version + " instantiated.");
      }
   }

   @Override
   public void entry() {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry());
   }

   @Override
   public void entry(final @Nullable Object arg1) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1));
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2));
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3));
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3, final @Nullable Object arg4) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3, arg4));
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3, final @Nullable Object arg4,
         final @Nullable Object arg5) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void error(final @Nullable String msg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, msg);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void error(final Throwable ex) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, null, ex);
   }

   @Override
   public void error(final Throwable ex, final @Nullable String msg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, msg, ex);
   }

   @Override
   public void error(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, args), ex);
   }

   @Override
   public void exit() {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceExit());
   }

   @Override
   public <T> T exit(final T returnValue) {
      if (!logger.isTraceEnabled())
         return returnValue;

      _log(TRACE_INT, formatTraceExit(returnValue));
      return returnValue;
   }

   @Override
   public void fatal(final Throwable ex) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, null, ex);
   }

   @Override
   public void fatal(final Throwable ex, final @Nullable String msg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, msg, ex);
   }

   @Override
   public void fatal(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, args), ex);
   }

   /**
    * @return the underlying org.slf4j.Logger
    */
   org.slf4j.Logger getLogger() {
      return logger;
   }

   @Override
   public String getName() {
      return loggerName;
   }

   @Override
   public void info(final @Nullable String msg) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, msg);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void info(final Throwable ex) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, null, ex);
   }

   @Override
   public void info(final Throwable ex, final @Nullable String msg) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, msg, ex);
   }

   @Override
   public void info(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, args), ex);
   }

   @Override
   public void infoNew(final Object newInstance) {
      if (!logger.isInfoEnabled())
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.isEmpty()) {
         _log(INFO_INT, newInstance.toString() + " instantiated.");
      } else {
         _log(INFO_INT, newInstance + " v" + version + " instantiated.");
      }
   }

   @Override
   public boolean isDebugEnabled() {
      return logger.isDebugEnabled();
   }

   @Override
   public boolean isErrorEnabled() {
      return logger.isErrorEnabled();
   }

   @Override
   public boolean isInfoEnabled() {
      return logger.isInfoEnabled();
   }

   @Override
   public boolean isTraceEnabled() {
      return logger.isTraceEnabled();
   }

   @Override
   public boolean isWarnEnabled() {
      return logger.isWarnEnabled();
   }

   @Override
   public void trace(final Method location, String msg) {
      if (LoggerConfig.isAddLocationToDebugMessages) {
         msg = location.getName() + "():" + msg;
      }
      logger.trace(msg);
   }

   @Override
   public void trace(final @Nullable String msg) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, msg);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void trace(final Throwable ex) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, "Unexpected exception occured: " + ex.getMessage(), ex);
   }

   @Override
   public void trace(final Throwable ex, final @Nullable String msg) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, msg, ex);
   }

   @Override
   public void trace(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, args), ex);
   }

   @Override
   public void warn(final @Nullable String msg) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, msg);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void warn(final Throwable ex) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, null, ex);
   }

   @Override
   public void warn(final Throwable ex, final @Nullable String msg) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, msg, ex);
   }

   @Override
   public void warn(final Throwable ex, final String messageTemplate, final Object... args) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, args), ex);
   }
}
