package net.sf.jstuff.core.logging;

import static org.slf4j.spi.LocationAwareLogger.*;

import java.util.Arrays;

import net.sf.jstuff.core.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class LoggerSLF4J extends Logger
{
	private final org.slf4j.Logger logger;
	private final LocationAwareLogger loggerEx;
	private final boolean isLocationAware;
	private final String loggerName;

	private static final String TIP = " (tip: set log level FINE to log the complete stacktrace)";

	private static final String THIS_CLASS_NAME = LoggerSLF4J.class.getName();

	protected LoggerSLF4J(final String name)
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
		final String logMsg;
		if (isLogExactSourceLocation && addSourceLocationToLogMessageIfDebugging)
		{
			/*
			 * if the current logger's level is DEBUG or TRACE we create full-blown log records for all levels
			 */
			final StackTraceElement caller = StackTrace.getCallingStackTraceElement(THIS_CLASS_NAME);

			if (caller == null) // should never happen
				throw new IllegalStateException("Unexpected stacktrace " + Arrays.toString(Thread.currentThread().getStackTrace()));

			logMsg = caller.getMethodName() + "():" + caller.getLineNumber() + " " + message;
		}
		else
			logMsg = message;

		if (isLocationAware)
			loggerEx.log(null, THIS_CLASS_NAME, level, logMsg, null, ex);
		else
			switch (level)
			{
				case TRACE_INT :
					logger.trace(logMsg, ex);
					break;
				case DEBUG_INT :
					logger.debug(logMsg, ex);
					break;
				case INFO_INT :
					logger.info(logMsg, ex);
					break;
				case WARN_INT :
					logger.info(logMsg, ex);
					break;
				case ERROR_INT :
					logger.info(logMsg, ex);
					break;
			}
		;
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
	public void debug(final String msg, final Throwable ex)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final Throwable ex)
	{
		if (!logger.isDebugEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(DEBUG_INT, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
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
	public void error(final String msg, final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(ERROR_INT, msg, ex, true);
		else
			_log(ERROR_INT, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void error(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(ERROR_INT, String.format(messageTemplate, args), ex, true);
		else
			_log(ERROR_INT, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void error(final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(ERROR_INT, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(ERROR_INT, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void fatal(final String msg, final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final Throwable ex)
	{
		if (!logger.isErrorEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		_log(ERROR_INT, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	/**
	 * @return the underlying java.util.Logger
	 */
	public org.slf4j.Logger getLogger()
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
	public void info(final String msg, final Throwable ex)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(INFO_INT, msg, ex, true);
		else
			_log(INFO_INT, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void info(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(INFO_INT, String.format(messageTemplate, args), ex, true);
		else
			_log(INFO_INT, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void info(final Throwable ex)
	{
		if (!logger.isInfoEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(INFO_INT, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(INFO_INT, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
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
	public void trace(final String msg, final Throwable ex)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final Throwable ex)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry()
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ()", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ([" + arg1 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "],[" + arg4 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "],[" + arg4 + "],[" + arg5 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceExit()
	{
		if (!logger.isTraceEnabled()) return;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD EXIT << *void*", null, isLogExactSourceLocation);
	}

	@Override
	public <T> T traceExit(final T returnValue)
	{
		if (!logger.isTraceEnabled()) return returnValue;

		final boolean isLogExactSourceLocation = true;
		_log(TRACE_INT, "METHOD EXIT << [" + returnValue + "]", null, isLogExactSourceLocation);
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
	public void warn(final String msg, final Throwable ex)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(WARN_INT, msg, ex, true);
		else
			_log(WARN_INT, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void warn(final String messageTemplate, final Throwable ex, final Object... args)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(WARN_INT, String.format(messageTemplate, args), ex, true);
		else
			_log(WARN_INT, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void warn(final Throwable ex)
	{
		if (!logger.isWarnEnabled()) return;

		final boolean isLogExactSourceLocation = logger.isDebugEnabled();
		if (isLogExactSourceLocation)
			_log(WARN_INT, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(WARN_INT, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}
}
