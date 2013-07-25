/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.core;

import java.util.Formatter;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Logger based on SLF4J supporting Java5 varargs and the {@link Formatter} syntax.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Logger
{
	/**
	 * Returns a new logger instance named corresponding to the current class.
	 */
	public static Logger create()
	{
		// getCallingClassName() must be in a separate line otherwise it will return a wrong name
		final String name = StackTrace.getCallingClassName();

		return new Logger(LoggerFactory.getLogger(name));
	}

	/**
	 * Returns a new logger instance named corresponding to the class passed as parameter.
	 */
	public static Logger create(final Class< ? > clazz)
	{
		return new Logger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Returns a new logger instance named according to the name parameter.
	 */
	public static Logger create(final String name)
	{
		return new Logger(LoggerFactory.getLogger(name));
	}

	private final String FQCN = Logger.class.getName();

	private final org.slf4j.Logger delegate;
	private final LocationAwareLogger delegateExt;

	private Logger(final org.slf4j.Logger delegate)
	{
		this.delegate = delegate;
		delegateExt = delegate instanceof LocationAwareLogger ? (LocationAwareLogger) delegate : null;
	}

	public void debug(final String msg)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Object... args)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(format, args);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, args, null);
	}

	public void debug(final String msg, final Throwable t)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(msg, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, msg, null, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(format, args, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, format, args, t);
	}

	public void error(final String msg)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Object... args)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(format, args);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, args, null);
	}

	public void error(final String msg, final Throwable t)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(msg, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(format, args, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, format, args, t);

	}

	public String getName()
	{
		return delegate.getName();
	}

	public void info(final String msg)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Object... args)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(format, args);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, format, args, null);
	}

	public void info(final String msg, final Throwable t)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(format, args, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, format, args, t);
	}

	public boolean isDebugEnabled()
	{
		return delegate.isDebugEnabled();
	}

	public boolean isErrorEnabled()
	{
		return delegate.isErrorEnabled();
	}

	public boolean isInfoEnabled()
	{
		return delegate.isInfoEnabled();
	}

	public boolean isTraceEnabled()
	{
		return delegate.isTraceEnabled();
	}

	public boolean isWarnEnabled()
	{
		return delegate.isWarnEnabled();
	}

	public void trace(final Object msg)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, StackTrace.getCallingMethodName() + ": " + msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + format, args);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, StackTrace.getCallingMethodName() + ": " + format, args, null);
	}

	public void trace(final String msg, final Throwable t)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + msg, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, StackTrace.getCallingMethodName() + ": " + msg, null, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + format, args, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, StackTrace.getCallingMethodName() + ": " + format, args, t);
	}

	public void traceMethodEntry(final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;

		final StringBuilder sb = new StringBuilder();
		sb.append("METHOD ENTRY: ").append(StackTrace.getCallingMethodName()).append('(');
		for (int i = 0; i < args.length; i++)
		{
			sb.append(args[i]);
			if (i != args.length - 1) sb.append(',');
		}
		sb.append(')');
		delegate.trace(sb.toString());
	}

	public void traceMethodExit()
	{
		if (!delegate.isTraceEnabled()) return;

		delegate.trace("METHOD EXIT: " + StackTrace.getCallingMethodName());
	}

	public void traceMethodExit(final Object returnValue)
	{
		if (!delegate.isTraceEnabled()) return;

		delegate.trace("METHOD EXIT: " + StackTrace.getCallingMethodName() + " returns with " + returnValue);
	}

	public void warn(final String msg)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(msg);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, null);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Object... args)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(format, args);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, format, args, null);
	}

	public void warn(final String msg, final Throwable t)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(msg, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, msg, null, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(format, args, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, format, args, t);
	}
}
