/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.logging;

import static org.slf4j.spi.LocationAwareLogger.*;

import java.util.Arrays;

import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class SLF4JLogger extends Logger
{
	private final org.slf4j.Logger logger;
	private final LocationAwareLogger loggerEx;
	private final boolean isLocationAware;
	private final String loggerName;

	SLF4JLogger(final String name)
	{
		logger = LoggerFactory.getLogger(name);
		loggerName = logger.getName();
		if (logger instanceof LocationAwareLogger)
		{
			isLocationAware = logger instanceof LocationAwareLogger;
			loggerEx = (LocationAwareLogger) logger;
		}
		else
		{
			isLocationAware = false;
			loggerEx = null;
		}
	}

	/**
	 * @param isLogExactSourceLocation if true the source location of the current log message will be determined and logged (should be only done
	 *            if logger is at least in DEBUG because it is a more expensive operation)
	 */
	private void _log(final int level, final String message, final Throwable ex, final boolean isLogExactSourceLocation)
	{
		String effectiveMessage;
		final Throwable effectiveException;
		if (isLogExactSourceLocation)
		{
			effectiveMessage = message;
			if (effectiveMessage == null && ex != null) effectiveMessage = "Unexpected exception";

			/*
			 * if the current logger's level is DEBUG or TRACE we add the logging method name + source location to all log messages
			 */
			if (LoggerConfig.isDebugMessagePrefixEnabled)
			{
				final StackTraceElement caller = StackTrace.getCallerStackTraceElement(DelegatingLogger.FQCN);

				if (caller == null) // should never happen
					throw new IllegalStateException("Unexpected stacktrace " + Arrays.toString(Thread.currentThread().getStackTrace()));

				if (!caller.getClassName().startsWith(INTERNAL_PACKAGE_NAME))
				{
					final String methodName = caller.getMethodName();
					effectiveMessage = methodName + "():" + caller.getLineNumber() + " " + effectiveMessage;
				}
			}
			effectiveException = ex;
		}
		else
		{
			if (ex == null)
				effectiveMessage = message;
			else
			{
				final StackTraceElement[] st = ex.getStackTrace();
				effectiveMessage = (message == null ? "" : message + " reason: ") + ex.getClass().getSimpleName() + ": " + ex.getMessage()
						+ (st != null && st.length > 0 ? "\n\tat " + st[0] + "\n\t[StackTrace truncated - set log level to FINE for full details]" : "");
			}
			effectiveException = null;
		}

		if (isLocationAware)
			loggerEx.log(null, DelegatingLogger.FQCN, level, effectiveMessage, null, effectiveException);
		else
			switch (level)
			{
				case TRACE_INT :
					logger.trace(effectiveMessage, effectiveException);
					break;
				case DEBUG_INT :
					logger.debug(effectiveMessage, effectiveException);
					break;
				case INFO_INT :
					logger.info(effectiveMessage, effectiveException);
					break;
				case WARN_INT :
					logger.info(effectiveMessage, effectiveException);
					break;
				case ERROR_INT :
					logger.info(effectiveMessage, effectiveException);
					break;
			}
	}

	@Override
	public void debug(final String msg)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final Throwable ex)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final Throwable ex, final String msg)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void error(final String msg)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(ERROR_INT, null, ex, isLogExactSourceLocation);
	}

	@Override
	public void error(final Throwable ex, final String msg)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(ERROR_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void error(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(ERROR_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(ERROR_INT, null, ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final Throwable ex, final String msg)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(ERROR_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(ERROR_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	/**
	 * @return the underlying java.util.Logger
	 */
	org.slf4j.Logger getLogger()
	{
		return logger;
	}

	@Override
	public String getName()
	{
		return loggerName;
	}

	@Override
	public void info(final String msg)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final Throwable ex)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(INFO_INT, null, ex, isLogExactSourceLocation);
	}

	@Override
	public void info(final Throwable ex, final String msg)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(INFO_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void info(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(INFO_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void infoNew(final Object newInstance)
	{
		Args.notNull("newInstance", newInstance);

		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(INFO_INT, newInstance + " " + Types.getVersion(newInstance.getClass()) + " instantiated.", null, isLogExactSourceLocation);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
	}

	@Override
	public void trace(final String msg)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final Throwable ex)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final Throwable ex, final String msg)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry()
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> ()", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> (" + argToString(arg1) + ")", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> (" + argToString(arg1) + ", " + argToString(arg2) + ")", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> (" + argToString(arg1) + ", " + argToString(arg2) + ", " + argToString(arg3) + ")", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> (" + argToString(arg1) + ", " + argToString(arg2) + ", " + argToString(arg3) + ", " + argToString(arg4) + ")", null,
				isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "ENTRY >> (" + argToString(arg1) + ", " + argToString(arg2) + ", " + argToString(arg3) + ", " + argToString(arg4) + ", "
				+ argToString(arg5) + ")", null, isLogExactSourceLocation);
	}

	@Override
	public void traceExit()
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "EXIT << *void*", null, isLogExactSourceLocation);
	}

	@Override
	public <T> T traceExit(final T returnValue)
	{
		if (!logger.isTraceEnabled()) return returnValue;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "EXIT << " + argToString(returnValue), null, isLogExactSourceLocation);
		return returnValue;
	}

	@Override
	public void warn(final String msg)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(WARN_INT, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final Throwable ex)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(WARN_INT, null, ex, isLogExactSourceLocation);
	}

	@Override
	public void warn(final Throwable ex, final String msg)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(WARN_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void warn(final Throwable ex, final String messageTemplate, final Object... args)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled() || LoggerConfig.isCompactExceptionLoggingDisabled;
		_log(WARN_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}
}
