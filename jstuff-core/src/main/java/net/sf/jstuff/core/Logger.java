/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
	public static Logger get()
	{
		return new Logger(LoggerFactory.getLogger(StackTraceUtils.getThisClassName()));
	}

	/**
	 * Returns a new logger instance named corresponding to the class passed as parameter.
	 */
	public static Logger get(final Class< ? > clazz)
	{
		return new Logger(LoggerFactory.getLogger(clazz));
	}

	/**
	 * Returns a new logger instance named according to the name parameter.
	 */
	public static Logger get(final String name)
	{
		return new Logger(LoggerFactory.getLogger(name));
	}

	private final org.slf4j.Logger delegate;

	private Logger(final org.slf4j.Logger delegate)
	{
		this.delegate = delegate;
	}

	public void debug(final String msg)
	{
		delegate.debug(msg);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void debug(final String format, final Object... args)
	{
		if (delegate.isDebugEnabled()) delegate.debug(String.format(format, args));
	}

	public void debug(final String msg, final Throwable t)
	{
		delegate.debug(msg, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void debug(final String format, final Throwable t, final Object... args)
	{
		if (delegate.isDebugEnabled()) delegate.debug(String.format(format, args), t);
	}

	public void error(final String msg)
	{
		delegate.error(msg);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void error(final String format, final Object... args)
	{
		if (delegate.isErrorEnabled()) delegate.error(String.format(format, args));
	}

	public void error(final String msg, final Throwable t)
	{
		delegate.error(msg, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void error(final String format, final Throwable t, final Object... args)
	{
		if (delegate.isErrorEnabled()) delegate.error(String.format(format, args), t);
	}

	public String getName()
	{
		return delegate.getName();
	}

	public void info(final String msg)
	{
		delegate.info(msg);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void info(final String format, final Object... args)
	{
		if (delegate.isInfoEnabled()) delegate.info(String.format(format, args));
	}

	public void info(final String s, final Throwable t)
	{
		delegate.info(s, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void info(final String format, final Throwable t, final Object... args)
	{
		if (delegate.isInfoEnabled()) delegate.info(String.format(format, args), t);
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
		if (delegate.isTraceEnabled()) delegate.trace(StackTraceUtils.getCallingMethodName() + ": " + msg);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void trace(final String format, final Object... args)
	{
		if (delegate.isTraceEnabled())
			delegate.trace(StackTraceUtils.getCallingMethodName() + ": " + String.format(format, args));
	}

	public void trace(final String msg, final Throwable t)
	{
		if (delegate.isTraceEnabled()) delegate.trace(StackTraceUtils.getCallingMethodName() + ": " + msg, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void trace(final String format, final Throwable t, final Object... args)
	{
		if (delegate.isTraceEnabled())
			delegate.trace(StackTraceUtils.getCallingMethodName() + ": " + String.format(format, args), t);
	}

	public void traceMethodEntry(final Object... args)
	{
		if (!delegate.isTraceEnabled()) return;

		final StringBuilder sb = new StringBuilder();
		sb.append("METHOD ENTRY: ").append(StackTraceUtils.getCallingMethodName()).append('(');
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

		delegate.trace("METHOD EXIT: " + StackTraceUtils.getCallingMethodName());
	}

	public void traceMethodExit(final Object returnValue)
	{
		if (!delegate.isTraceEnabled()) return;

		delegate.trace("METHOD EXIT: " + StackTraceUtils.getCallingMethodName() + " returns with " + returnValue);
	}

	public void warn(final String msg)
	{
		delegate.warn(msg);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void warn(final String format, final Object... args)
	{
		if (delegate.isWarnEnabled()) delegate.warn(String.format(format, args));
	}

	public void warn(final String msg, final Throwable t)
	{
		delegate.warn(msg, t);
	}

	/**
	 * @param format A format string understandable by {@link Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored. 
	 */
	public void warn(final String format, final Throwable t, final Object... args)
	{
		if (delegate.isWarnEnabled()) delegate.warn(String.format(format, args), t);
	}
}
