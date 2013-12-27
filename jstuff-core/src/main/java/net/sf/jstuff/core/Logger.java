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

import net.sf.jstuff.core.validation.Args;

import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Logger based on SLF4J supporting the {@link java.util.Formatter} syntax.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class Logger
{
	private static final String FQCN = Logger.class.getName();

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
		Args.notNull("clazz", clazz);

		return new Logger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Returns a new logger instance named according to the name parameter.
	 */
	public static Logger create(final String name)
	{
		Args.notNull("name", name);

		return new Logger(LoggerFactory.getLogger(name));
	}

	private final org.slf4j.Logger delegate;
	private final LocationAwareLogger delegateExt;

	private Logger(final org.slf4j.Logger delegate)
	{
		Args.notNull("delegate", delegate);

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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the format string.
	 */
	public void debug(final String format, final Object arg)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(String.format(format, arg));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, String.format(format, arg), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Object... args)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(String.format(format, args));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, String.format(format, args), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the format string.
	 * @param arg2 Argument referenced by the format specifiers in the format string.
	 */
	public void debug(final String format, final Object arg1, final Object arg2)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(String.format(format, arg1, arg2));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, String.format(format, arg1, arg2), null, null);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isDebugEnabled()) return;
		if (delegateExt == null)
			delegate.debug(String.format(format, args), t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.DEBUG_INT, String.format(format, args), null, t);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the format string.
	 */
	public void error(final String format, final Object arg)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(String.format(format, arg));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, String.format(format, arg), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Object... args)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(String.format(format, args));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, String.format(format, args), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the format string.
	 * @param arg2 Argument referenced by the format specifiers in the format string.
	 */
	public void error(final String format, final Object arg1, final Object arg2)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(String.format(format, arg1, arg2));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, String.format(format, arg1, arg2), null, null);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isErrorEnabled()) return;
		if (delegateExt == null)
			delegate.error(String.format(format, args), t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.ERROR_INT, String.format(format, args), null, t);

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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the format string.
	 */
	public void info(final String format, final Object arg)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(String.format(format, arg));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, String.format(format, arg), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Object... args)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(String.format(format, args));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, String.format(format, args), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the format string.
	 * @param arg2 Argument referenced by the format specifiers in the format string.
	 */
	public void info(final String format, final Object arg1, final Object arg2)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(String.format(format, arg1, arg2));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, String.format(format, arg1, arg2), null, null);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isInfoEnabled()) return;
		if (delegateExt == null)
			delegate.info(String.format(format, args), t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.INFO_INT, String.format(format, args), null, t);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the format string.
	 */
	public void trace(final String format, final Object arg)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(String.format(format, arg));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, String.format(format, arg), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + String.format(format, args));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT,
					StackTrace.getCallingMethodName() + ": " + String.format(format, args), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the format string.
	 * @param arg2 Argument referenced by the format specifiers in the format string.
	 */
	public void trace(final String format, final Object arg1, final Object arg2)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(String.format(format, arg1, arg2));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT, String.format(format, arg1, arg2), null, null);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;
		if (delegateExt == null)
			delegate.trace(StackTrace.getCallingMethodName() + ": " + String.format(format, args), null, t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.TRACE_INT,
					StackTrace.getCallingMethodName() + ": " + String.format(format, args), null, t);
	}

	public void traceMethodEntry(final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;

		final StringBuilder sb = new StringBuilder() //
				.append("METHOD ENTRY: ")//
				.append(StackTrace.getCallingMethodName())//
				.append('(');
		for (int i = 0, l = args.length; i < l; i++)
		{
			sb.append(args[i]);
			if (i != l - 1) sb.append(',');
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the format string.
	 */
	public void warn(final String format, final Object arg)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(String.format(format, arg));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, String.format(format, arg), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Object... args)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(String.format(format, args));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, String.format(format, args), null, null);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the format string.
	 * @param arg2 Argument referenced by the format specifiers in the format string.
	 */
	public void warn(final String format, final Object arg1, final Object arg2)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(String.format(format, arg1, arg2));
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, String.format(format, arg1, arg2), null, null);
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
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isWarnEnabled()) return;
		if (delegateExt == null)
			delegate.warn(String.format(format, args), t);
		else
			delegateExt.log(null, FQCN, LocationAwareLogger.WARN_INT, String.format(format, args), null, t);
	}
}
