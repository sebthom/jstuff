/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.logging;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;

import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class JULLogger extends Logger {
    private final java.util.logging.Logger logger;
    private final String loggerName;

    private static final int L_TRACE = java.util.logging.Level.FINEST.intValue();
    private static final int L_DEBUG = java.util.logging.Level.FINE.intValue();
    private static final int L_INFO = java.util.logging.Level.INFO.intValue();
    private static final int L_WARN = java.util.logging.Level.WARNING.intValue();
    private static final int L_ERROR = java.util.logging.Level.SEVERE.intValue();

    JULLogger(final String name) {
        logger = java.util.logging.Logger.getLogger(name);
        loggerName = name;
    }

    /**
     * @param isLogExactSourceLocation if true the source location of the current log message will be determined and logged (should be only done
     *            if logger is at least in DEBUG because it is a more expensive operation)
     */
    private void _log(final Level level, final String message, final Throwable ex, final boolean isLogExactSourceLocation) {
        final String sourceClassName;
        final String methodName;
        String effectiveMessage;
        final Throwable effectiveException;
        if (isLogExactSourceLocation) {
            effectiveMessage = message;
            if (effectiveMessage == null && ex != null) {
                effectiveMessage = "Catched ";
            }

            /*
             * if the current logger's level is DEBUG or TRACE we create full-blown log records for all levels
             */
            final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);

            if (caller == null) // should never happen
                throw new IllegalStateException("Unexpected stacktrace " + Arrays.toString(Thread.currentThread().getStackTrace()));

            methodName = caller.getMethodName();
            if (LoggerConfig.isDebugMessagePrefixEnabled) {
                effectiveMessage = methodName + "():" + caller.getLineNumber() + " " + effectiveMessage;
            }
            sourceClassName = caller.getClassName();
            effectiveException = ex;
        } else {
            if (ex == null) {
                effectiveMessage = message;
            } else {
                final StackTraceElement[] st = ex.getStackTrace();
                effectiveMessage = (message == null ? "" : message + " reason: ") + ex.getClass().getName() + ": " + ex.getMessage() + (st != null
                        && st.length > 0 ? "\n\tat " + st[0] + "\n\t[StackTrace truncated - set log level of " + getName() + " to FINE for full details]" : "");
            }
            sourceClassName = loggerName;
            methodName = null;
            effectiveException = null;
        }
        logger.logp(level, sourceClassName, methodName, effectiveMessage, effectiveException);
    }

    @Override
    public void debug(final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, msg, null, true);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, String.format(messageTemplate, arg), null, true);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, String.format(messageTemplate, arg1, arg2), null, true);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3), null, true);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, true);
    }

    @Override
    public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, true);
    }

    @Override
    public void debug(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_DEBUG)
            return;

        _log(Level.FINE, null, ex, true);
    }

    @Override
    public void debug(final Throwable ex, final String msg) {
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
    public void error(final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, msg, null, isLogExactSourceLocation);
    }

    @Override
    public void error(final String messageTemplate, final Object arg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
    }

    @Override
    public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
    }

    @Override
    public void error(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.SEVERE, null, ex, isLogExactSourceLocation);
    }

    @Override
    public void error(final Throwable ex, final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.SEVERE, msg, ex, isLogExactSourceLocation);
    }

    @Override
    public void error(final Throwable ex, final String messageTemplate, final Object... args) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.SEVERE, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
    }

    @Override
    public void fatal(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_ERROR)
            return;

        _log(Level.SEVERE, null, ex, true);
    }

    @Override
    public void fatal(final Throwable ex, final String msg) {
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
    public void info(final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, msg, null, isLogExactSourceLocation);
    }

    @Override
    public void info(final String messageTemplate, final Object arg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
    }

    @Override
    public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
    }

    @Override
    public void info(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.INFO, null, ex, isLogExactSourceLocation);
    }

    @Override
    public void info(final Throwable ex, final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.INFO, msg, ex, isLogExactSourceLocation);
    }

    @Override
    public void info(final Throwable ex, final String messageTemplate, final Object... args) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.INFO, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
    }

    @Override
    public void infoNew(final Object newInstance) {
        Args.notNull("newInstance", newInstance);

        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_INFO)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.INFO, newInstance + " v" + Types.getVersion(newInstance.getClass()) + " instantiated.", null, isLogExactSourceLocation);
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
    protected void trace(final Method location, String msg) {
        if (LoggerConfig.isDebugMessagePrefixEnabled) {
            msg = location.getName() + "():" + msg;
        }
        logger.logp(Level.FINEST, location.getDeclaringClass().getName(), location.getName(), msg);
    }

    @Override
    public void trace(final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, msg, null, true);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, String.format(messageTemplate, arg), null, true);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, String.format(messageTemplate, arg1, arg2), null, true);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3), null, true);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, true);
    }

    @Override
    public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, true);
    }

    @Override
    public void trace(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, null, ex, true);
    }

    @Override
    public void trace(final Throwable ex, final String msg) {
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
    public void entry() {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(), null, true);
    }

    @Override
    public void entry(final Object arg1) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(arg1), null, true);
    }

    @Override
    public void entry(final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(arg1, arg2), null, true);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3), null, true);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3, arg4), null, true);
    }

    @Override
    public void entry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceEntry(arg1, arg2, arg3, arg4, arg5), null, true);
    }

    @Override
    public void exit() {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return;

        _log(Level.FINEST, formatTraceExit(), null, true);
    }

    @Override
    public <T> T exit(final T returnValue) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_TRACE)
            return returnValue;

        _log(Level.FINEST, formatTraceExit(returnValue), null, true);
        return returnValue;
    }

    @Override
    public void warn(final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, msg, null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
        _log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
    }

    @Override
    public void warn(final Throwable ex) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.WARNING, null, ex, isLogExactSourceLocation);
    }

    @Override
    public void warn(final Throwable ex, final String msg) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.WARNING, msg, ex, isLogExactSourceLocation);
    }

    @Override
    public void warn(final Throwable ex, final String messageTemplate, final Object... args) {
        final int effectiveLevel = getLevelInt();
        if (effectiveLevel > L_WARN)
            return;

        final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG || LoggerConfig.isCompactExceptionLoggingDisabled;
        _log(Level.WARNING, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
    }
}
