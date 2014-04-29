package net.sf.jstuff.core.logging;

import java.util.Arrays;
import java.util.logging.Level;

import net.sf.jstuff.core.StackTrace;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

public class LoggerJUL extends Logger
{
	private final java.util.logging.Logger logger;
	private final String loggerName;

	private static final String TIP = " (tip: set log level FINE to log the complete stacktrace)";

	private static final int L_TRACE = java.util.logging.Level.FINEST.intValue();
	private static final int L_DEBUG = java.util.logging.Level.FINE.intValue();
	private static final int L_INFO = java.util.logging.Level.INFO.intValue();
	private static final int L_WARN = java.util.logging.Level.WARNING.intValue();
	private static final int L_ERROR = java.util.logging.Level.SEVERE.intValue();

	protected LoggerJUL(final String name)
	{
		logger = java.util.logging.Logger.getLogger(name);
		loggerName = name;
	}

	/**
	 * @param isLogExactSourceLocation if true the source location of the current log message will be determined and logged (should be only done
	 *            if logger is at least in DEBUG because it is a more expensive operation)
	 */
	private void _log(final Level level, final String message, final Throwable ex, final boolean isLogExactSourceLocation)
	{
		if (isLogExactSourceLocation)
		{
			/*
			 * if the current logger's level is DEBUG or TRACE we create full-blown log records for all levels
			 */
			final StackTraceElement caller = StackTrace.getCallingStackTraceElement(LoggerJUL.class);

			if (caller == null) // should never happen
				throw new IllegalStateException("Unexpected stacktrace " + Arrays.toString(Thread.currentThread().getStackTrace()));

			final String methodName = caller.getMethodName();
			final String logMsg;
			if (addSourceLocationToLogMessageIfDebugging)
				logMsg = methodName + "():" + caller.getLineNumber() + " " + message;
			else
				logMsg = message;

			logger.logp(level, caller.getClassName(), methodName, logMsg, ex);
		}
		else
			logger.logp(level, loggerName, null, message, ex);
	}

	@Override
	public void debug(final String msg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void debug(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_DEBUG) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINE, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	@Override
	public void error(final String msg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void error(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.SEVERE, msg, ex, true);
		else
			_log(Level.SEVERE, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void error(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.SEVERE, String.format(messageTemplate, args), ex, true);
		else
			_log(Level.SEVERE, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void error(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.SEVERE, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(Level.SEVERE, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void fatal(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void fatal(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_ERROR) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.SEVERE, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	/**
	 * @return the effective log level of the underlying java.util.Logger
	 */
	public int getLevelInt()
	{
		for (java.util.logging.Logger current = logger; current != null;)
		{
			final java.util.logging.Level level = current.getLevel();
			if (level != null) return level.intValue();
			current = current.getParent();
		}
		throw new IllegalStateException("Cannot determine logger level!");
	}

	/**
	 * @return the underlying java.util.Logger
	 */
	public java.util.logging.Logger getLogger()
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
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void info(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.INFO, msg, ex, true);
		else
			_log(Level.INFO, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void info(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.INFO, String.format(messageTemplate, args), ex, true);
		else
			_log(Level.INFO, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void info(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.INFO, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(Level.INFO, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void infoNew(final Object newInstance)
	{
		Args.notNull("newInstance", newInstance);

		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_INFO) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.INFO, newInstance + " " + Types.getVersion(newInstance.getClass()) + " instantiated.", null, isLogExactSourceLocation);
	}

	@Override
	public boolean isDebugEnabled()
	{
		final int effectiveLevel = getLevelInt();
		return effectiveLevel <= L_DEBUG;
	}

	@Override
	public boolean isErrorEnabled()
	{
		final int effectiveLevel = getLevelInt();
		return effectiveLevel <= L_ERROR;
	}

	@Override
	public boolean isInfoEnabled()
	{
		final int effectiveLevel = getLevelInt();
		return effectiveLevel <= L_INFO;
	}

	@Override
	public boolean isTraceEnabled()
	{
		final int effectiveLevel = getLevelInt();
		return effectiveLevel <= L_TRACE;
	}

	@Override
	public boolean isWarnEnabled()
	{
		final int effectiveLevel = getLevelInt();
		return effectiveLevel <= L_WARN;
	}

	@Override
	public void trace(final String msg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, msg, ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, String.format(messageTemplate, args), ex, isLogExactSourceLocation);
	}

	@Override
	public void trace(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "Unexpected exception occured: " + ex.getMessage(), ex, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry()
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ()", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ([" + arg1 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "],[" + arg4 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD ENTRY >> ([" + arg1 + "],[" + arg2 + "],[" + arg3 + "],[" + arg4 + "],[" + arg5 + "])", null, isLogExactSourceLocation);
	}

	@Override
	public void traceExit()
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD EXIT << *void*", null, isLogExactSourceLocation);
	}

	@Override
	public <T> T traceExit(final T returnValue)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_TRACE) return returnValue;

		final boolean isLogExactSourceLocation = true;
		_log(Level.FINEST, "METHOD EXIT << [" + returnValue + "]", null, isLogExactSourceLocation);
		return returnValue;
	}

	@Override
	public void warn(final String msg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, msg, null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, String.format(messageTemplate, arg), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, String.format(messageTemplate, arg1, arg2), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		_log(Level.WARNING, String.format(messageTemplate, arg1, arg2, arg3, arg4, arg5), null, isLogExactSourceLocation);
	}

	@Override
	public void warn(final String msg, final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.WARNING, msg, ex, true);
		else
			_log(Level.WARNING, msg + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void warn(final String messageTemplate, final Throwable ex, final Object... args)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.WARNING, String.format(messageTemplate, args), ex, true);
		else
			_log(Level.WARNING, String.format(messageTemplate, args) + " reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}

	@Override
	public void warn(final Throwable ex)
	{
		final int effectiveLevel = getLevelInt();
		if (effectiveLevel > L_WARN) return;

		final boolean isLogExactSourceLocation = effectiveLevel <= L_DEBUG;
		if (isLogExactSourceLocation)
			_log(Level.WARNING, "Unexpected exception occured: " + ex.getMessage(), ex, true);
		else
			_log(Level.WARNING, ex.getClass().getSimpleName() + ": " + ex.getMessage() + TIP, null, false);
	}
}
