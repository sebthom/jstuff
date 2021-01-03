/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.logging;

import static net.sf.jstuff.core.logging.LoggerUtils.*;
import static org.slf4j.spi.LocationAwareLogger.*;

import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
final class SLF4JLogger implements LoggerInternal {

   private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SLF4JLogger.class);

   private final org.slf4j.Logger logger;
   private final LocationAwareLogger loggerEx;
   private final boolean isLocationAware;
   private final String loggerName;

   SLF4JLogger(final String name) {
      logger = LoggerFactory.getLogger(name);
      loggerName = logger.getName();
      if (logger instanceof LocationAwareLogger) {
         isLocationAware = logger instanceof LocationAwareLogger;
         loggerEx = (LocationAwareLogger) logger;
      } else {
         isLocationAware = false;
         loggerEx = null;
      }
   }

   private void _log(final int level, final String message) {
      final String effectiveMessage;
      if (LoggerConfig.isDebugMessagePrefixEnabled && logger.isDebugEnabled()) {
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

      if (isLocationAware) {
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

   private void _log(final int level, final String message, final Throwable ex) {
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
         effectiveMessage = message == null || message.length() == 0 ? "Catched " : message;
         effectiveException = ex;
         if (LoggerConfig.isDebugMessagePrefixEnabled) {
            final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
            if (caller == null) { // should never happen
               LOG.error("Unexpected stacktrace " + Strings.join(Thread.currentThread().getStackTrace(), "\n"));
            } else {
               effectiveMessage = caller.getMethodName() + "():" + caller.getLineNumber() + " " + effectiveMessage;
            }
         }
      } else {
         if (LoggerConfig.isCompactExceptionLoggingEnabled) {
            final StackTraceElement[] st = ex.getStackTrace();
            final StringBuilder sb = new StringBuilder();
            if (message == null || message.length() == 0) {
               sb.append("Catched ");
            } else {
               sb.append(message).append(" reason: ");
            }
            sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
            if (st != null && st.length > 0) {
               sb.append("\tat ").append(st[0]).append("\n");
               if (st.length > 1) {
                  sb.append("\tat ").append(st[1]).append("\n");
               }
               if (st.length > 2) {
                  sb.append("\t[StackTrace truncated - set log level of ").append(loggerName).append(" to FINE for full details]");
               }
            }
            effectiveMessage = sb.toString();
            effectiveException = null;
         } else {
            effectiveMessage = message == null || message.length() == 0 ? "Catched " : message;
            effectiveException = ex;
         }
      }

      if (isLocationAware) {
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
   public void debug(final String msg) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, msg);
   }

   @Override
   public void debug(final String messageTemplate, final Object arg) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isDebugEnabled())
         return;

      _log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
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
   public void debug(final Throwable ex, final String msg) {
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
      Args.notNull("newInstance", newInstance);

      if (!logger.isDebugEnabled())
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.length() == 0) {
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
   public void entry(final Object arg1) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1));
   }

   @Override
   public void entry(final Object arg1, final Object arg2) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2));
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3));
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3, arg4));
   }

   @Override
   public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, formatTraceEntry(arg1, arg2, arg3, arg4, arg5));
   }

   @Override
   public void error(final String msg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, msg);
   }

   @Override
   public void error(final String messageTemplate, final Object arg) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isErrorEnabled())
         return;

      _log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
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
   public void error(final Throwable ex, final String msg) {
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
   public void fatal(final Throwable ex, final String msg) {
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
   public void info(final String msg) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, msg);
   }

   @Override
   public void info(final String messageTemplate, final Object arg) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isInfoEnabled())
         return;

      _log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
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
   public void info(final Throwable ex, final String msg) {
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
      Args.notNull("newInstance", newInstance);

      if (!logger.isInfoEnabled())
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.length() == 0) {
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
      if (LoggerConfig.isDebugMessagePrefixEnabled) {
         msg = location.getName() + "():" + msg;
      }
      logger.trace(msg);
   }

   @Override
   public void trace(final String msg) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, msg);
   }

   @Override
   public void trace(final String messageTemplate, final Object arg) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isTraceEnabled())
         return;

      _log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
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
   public void trace(final Throwable ex, final String msg) {
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
   public void warn(final String msg) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, msg);
   }

   @Override
   public void warn(final String messageTemplate, final Object arg) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg));
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2));
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3));
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
      if (!logger.isWarnEnabled())
         return;

      _log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4));
   }

   @Override
   public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
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
   public void warn(final Throwable ex, final String msg) {
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
