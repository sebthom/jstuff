/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging;

import static net.sf.jstuff.core.logging.LoggerUtils.*;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.logging.jul.Levels;
import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
final class JULLogger implements LoggerInternal {

   private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(JULLogger.class.getName());

   private static final int L_TRACE = Levels.FINER_INT;
   private static final int L_DEBUG = Levels.FINE_INT;
   private static final int L_INFO = Levels.INFO_INT;
   private static final int L_WARN = Levels.WARNING_INT;
   private static final int L_ERROR = Levels.SEVERE_INT;

   private final java.util.logging.Logger logger;
   private final String loggerName;

   JULLogger(final String name) {
      logger = java.util.logging.Logger.getLogger(name);
      loggerName = name;
   }

   /**
    * @param isDebugEnabled if true the source location of the current log message will be determined and logged (should be only done
    *           if logger is at least in DEBUG because it is a more expensive operation)
    */
   private void _log(final Level level, final @Nullable String message, final boolean isDebugEnabled) {
      if (isDebugEnabled) {
         final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
         if (caller == null) { // should never happen
            logger.log(level, message);
            LOG.severe("Unexpected stacktrace " + Strings.join(Thread.currentThread().getStackTrace(), "\n"));
            return;
         }
         final String methodName = caller.getMethodName();
         String effectiveMessage = message;
         if (LoggerConfig.isAddLocationToDebugMessages) {
            effectiveMessage = methodName + "():" + caller.getLineNumber() + " " + effectiveMessage;
         }
         logger.logp(level, caller.getClassName(), methodName, effectiveMessage);
      } else {
         logger.logp(level, loggerName, null, message);
      }
   }

   /**
    * @param isDebugLevelEnabled if true the source location of the current log message will be determined and logged (should be only done
    *           if logger is at least in DEBUG because it is a more expensive operation)
    */
   private void _log(final Level level, final @Nullable String message, final @Nullable Throwable ex, final boolean isDebugLevelEnabled) {
      if (ex == null) {
         _log(level, message, isDebugLevelEnabled);
         return;
      }

      if (LoggerConfig.isSanitizeStrackTracesEnabled) {
         sanitizeStackTraces(ex);
      }

      String effectiveMessage;
      if (isDebugLevelEnabled) {
         effectiveMessage = message == null || message.isEmpty() ? "Catched " : message;

         final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);
         if (caller == null) { // should never happen
            logger.log(level, message);
            LOG.severe("Unexpected stacktrace " + Strings.join(Thread.currentThread().getStackTrace(), "\n"));
            return;
         }
         final String sourceClassName = caller.getClassName();
         final String sourceMethodName = caller.getMethodName();

         if (LoggerConfig.isAddLocationToDebugMessages) {
            effectiveMessage = sourceMethodName + "():" + caller.getLineNumber() + " " + effectiveMessage;
         }
         logger.logp(level, sourceClassName, sourceMethodName, effectiveMessage, ex);
      } else {
         final Throwable effectiveException;
         if (LoggerConfig.isCompactExceptionLoggingEnabled) {
            final var sb = new StringBuilder();
            if (message == null || message.isEmpty()) {
               sb.append("Catched ");
            } else {
               sb.append(message).append(" reason: ");
            }
            final var stack = ex.getStackTrace();
            sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
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

         logger.log(level, effectiveMessage, effectiveException);
      }
   }

   @Override
   public void debug(final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, msg, true);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, arg), true);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, arg1, arg2), true);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3), true);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4), true);
   }

   @Override
   public void debug(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), true);
   }

   @Override
   public void debug(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, null, ex, true);
   }

   @Override
   public void debug(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, msg, ex, true);
   }

   @Override
   public void debug(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      _log(Level.FINE, String.format(messageTemplate, args), ex, true);
   }

   @Override
   public void debugNew(final Object newInstance) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_DEBUG)
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.isEmpty()) {
         _log(Level.FINE, newInstance.toString() + " instantiated.", effectiveLevel <= L_DEBUG);
      } else {
         _log(Level.FINE, newInstance + " v" + version + " instantiated.", effectiveLevel <= L_DEBUG);
      }
   }

   @Override
   public void entry() {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(), true);
   }

   @Override
   public void entry(final @Nullable Object arg1) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(arg1), true);
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(arg1, arg2), true);
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3), true);
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3, final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3, arg4), true);
   }

   @Override
   public void entry(final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3, final @Nullable Object arg4,
         final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3, arg4, arg5), true);
   }

   @Override
   public void error(final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, msg, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, arg), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, null, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, msg, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void error(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, args), ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void exit() {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, formatTraceExit(), true);
   }

   @Override
   public <T> T exit(final T returnValue) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return returnValue;

      _log(Level.FINEST, formatTraceExit(returnValue), true);
      return returnValue;
   }

   @Override
   public void fatal(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, null, ex, true);
   }

   @Override
   public void fatal(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, msg, ex, true);
   }

   @Override
   public void fatal(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_ERROR)
         return;

      _log(Level.SEVERE, String.format(messageTemplate, args), ex, true);
   }

   /**
    * @return the effective log level of the underlying java.util.Logger
    */
   int getLevelInt() {
      for (java.util.logging.Logger current = logger; current != null;) {
         final java.util.logging.Level level = current.getLevel();
         if (level != null)
            return level.intValue();
         current = current.getParent();
      }
      return Level.INFO.intValue();
   }

   /**
    * @return the underlying java.util.Logger
    */
   java.util.logging.Logger getLogger() {
      return logger;
   }

   @Override
   public String getName() {
      return loggerName;
   }

   @Override
   public void info(final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, msg, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, arg), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, arg1, arg2), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, null, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, msg, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void info(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      _log(Level.INFO, String.format(messageTemplate, args), ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void infoNew(final Object newInstance) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_INFO)
         return;

      final String version = Types.getVersion(newInstance.getClass());
      if (version == null || version.isEmpty()) {
         _log(Level.INFO, newInstance.toString() + " instantiated.", effectiveLevel <= L_DEBUG);
      } else {
         _log(Level.INFO, newInstance + " v" + version + " instantiated.", effectiveLevel <= L_DEBUG);
      }
   }

   @Override
   public boolean isDebugEnabled() {
      final int effectiveLevel = getLevelInt();
      return effectiveLevel <= L_DEBUG;
   }

   @Override
   public boolean isErrorEnabled() {
      final int effectiveLevel = getLevelInt();
      return effectiveLevel <= L_ERROR;
   }

   @Override
   public boolean isInfoEnabled() {
      final int effectiveLevel = getLevelInt();
      return effectiveLevel <= L_INFO;
   }

   @Override
   public boolean isTraceEnabled() {
      final int effectiveLevel = getLevelInt();
      return effectiveLevel <= L_TRACE;
   }

   @Override
   public boolean isWarnEnabled() {
      final int effectiveLevel = getLevelInt();
      return effectiveLevel <= L_WARN;
   }

   @Override
   public void trace(final Method location, String msg) {
      if (LoggerConfig.isAddLocationToDebugMessages) {
         msg = location.getName() + "():" + msg;
      }
      logger.logp(Level.FINEST, location.getDeclaringClass().getName(), location.getName(), msg);
   }

   @Override
   public void trace(final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, msg, true);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, arg), true);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, arg1, arg2), true);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3), true);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4), true);
   }

   @Override
   public void trace(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), true);
   }

   @Override
   public void trace(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, null, ex, true);
   }

   @Override
   public void trace(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, msg, ex, true);
   }

   @Override
   public void trace(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_TRACE)
         return;

      _log(Level.FINEST, String.format(messageTemplate, args), ex, true);
   }

   @Override
   public void warn(final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, msg, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, arg), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, arg1, arg2), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final String messageTemplate, final @Nullable Object arg1, final @Nullable Object arg2, final @Nullable Object arg3,
         final @Nullable Object arg4, final @Nullable Object arg5) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final Throwable ex) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, null, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final Throwable ex, final @Nullable String msg) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, msg, ex, effectiveLevel <= L_DEBUG);
   }

   @Override
   public void warn(final Throwable ex, final String messageTemplate, final Object... args) {
      final int effectiveLevel = getLevelInt();
      if (effectiveLevel > L_WARN)
         return;

      _log(Level.WARNING, String.format(messageTemplate, args), ex, effectiveLevel <= L_DEBUG);
   }
}
