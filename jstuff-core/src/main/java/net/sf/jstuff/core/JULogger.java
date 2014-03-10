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
package net.sf.jstuff.core;

import java.util.logging.Level;

import net.sf.jstuff.core.validation.Args;

/**
 * Logger based on JUL Logger supporting the {@link java.util.Formatter} syntax.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class JULogger
{
	/**
	 * Returns a new logger instance named corresponding to the current class.
	 */
	public static JULogger create()
	{
		// getCallingClassName() must be in a separate line otherwise it will return a wrong name
		final String name = StackTrace.getCallingClassName();

		return new JULogger(java.util.logging.Logger.getLogger(name));
	}

	/**
	 * Returns a new logger instance named corresponding to the class passed as parameter.
	 */
	public static JULogger create(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);

		return new JULogger(java.util.logging.Logger.getLogger(clazz.getName()));
	}

	/**
	 * Returns a new logger instance named according to the name parameter.
	 */
	public static JULogger create(final String name)
	{
		Args.notNull("name", name);

		return new JULogger(java.util.logging.Logger.getLogger(name));
	}

	private final java.util.logging.Logger delegate;

	private JULogger(final java.util.logging.Logger delegate)
	{
		this.delegate = delegate;
	}

	public void debug(final String msg)
	{
		if (!delegate.isLoggable(Level.FINE)) return;
		delegate.fine(msg);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Object... args)
	{
		if (!delegate.isLoggable(Level.FINE)) return;
		delegate.fine(String.format(format, args));
	}

	public void debug(final String msg, final Throwable t)
	{
		if (!delegate.isLoggable(Level.FINE)) return;
		delegate.log(Level.FINE, msg, t);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void debug(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isLoggable(Level.FINE)) return;
		delegate.log(Level.FINE, String.format(format, args), t);
	}

	public void error(final String msg)
	{
		if (!delegate.isLoggable(Level.SEVERE)) return;
		delegate.severe(msg);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Object... args)
	{
		if (!delegate.isLoggable(Level.SEVERE)) return;
		delegate.severe(String.format(format, args));
	}

	public void error(final String msg, final Throwable t)
	{
		if (!delegate.isLoggable(Level.SEVERE)) return;
		delegate.log(Level.SEVERE, msg, t);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void error(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isLoggable(Level.SEVERE)) return;
		delegate.log(Level.SEVERE, String.format(format, args), t);
	}

	public String getName()
	{
		return delegate.getName();
	}

	public void info(final String msg)
	{
		if (!delegate.isLoggable(Level.INFO)) return;
		delegate.info(msg);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Object... args)
	{
		if (!delegate.isLoggable(Level.INFO)) return;
		delegate.info(String.format(format, args));
	}

	public void info(final String msg, final Throwable t)
	{
		if (!delegate.isLoggable(Level.INFO)) return;
		delegate.log(Level.INFO, msg, t);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void info(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isLoggable(Level.INFO)) return;
		delegate.log(Level.INFO, String.format(format, args), t);
	}

	public boolean isDebugEnabled()
	{
		return delegate.isLoggable(Level.FINE);
	}

	public boolean isErrorEnabled()
	{
		return delegate.isLoggable(Level.SEVERE);
	}

	public boolean isInfoEnabled()
	{
		return delegate.isLoggable(Level.INFO);
	}

	public boolean isTraceEnabled()
	{
		return delegate.isLoggable(Level.FINEST);
	}

	public boolean isWarnEnabled()
	{
		return delegate.isLoggable(Level.WARNING);
	}

	public void trace(final Object msg)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.finest(StackTrace.getCallingMethodName() + ": " + msg);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Object... args)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.finest(StackTrace.getCallingMethodName() + ": " + String.format(format, args));
	}

	public void trace(final String msg, final Throwable t)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.log(Level.FINEST, StackTrace.getCallingMethodName() + ": " + msg, t);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void trace(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.log(Level.FINEST, StackTrace.getCallingMethodName() + ": " + String.format(format, args), t);
	}

	public void traceMethodEntry(final Object... args)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;

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
		delegate.finest(sb.toString());
	}

	public void traceMethodExit()
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.finest("METHOD EXIT: " + StackTrace.getCallingMethodName());
	}

	public void traceMethodExit(final Object returnValue)
	{
		if (!delegate.isLoggable(Level.FINEST)) return;
		delegate.finest("METHOD EXIT: " + StackTrace.getCallingMethodName() + " returns with " + returnValue);
	}

	public void warn(final String msg)
	{
		if (!delegate.isLoggable(Level.WARNING)) return;
		delegate.warning(msg);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Object... args)
	{
		if (!delegate.isLoggable(Level.WARNING)) return;
		delegate.warning(String.format(format, args));
	}

	public void warn(final String msg, final Throwable t)
	{
		if (!delegate.isLoggable(Level.WARNING)) return;
		delegate.log(Level.WARNING, msg, t);
	}

	/**
	 * @param format A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the format string. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public void warn(final String format, final Throwable t, final Object... args)
	{
		if (!delegate.isLoggable(Level.WARNING)) return;
		delegate.log(Level.WARNING, String.format(format, args), t);
	}
}
